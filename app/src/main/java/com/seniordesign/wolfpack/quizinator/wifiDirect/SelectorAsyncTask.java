package com.seniordesign.wolfpack.quizinator.wifiDirect;

import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class SelectorAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "PTP_Sel";
    private ConnectionService mConnService;
    private Selector mSelector;

    SelectorAsyncTask(ConnectionService connectionService,
                             Selector selector) {
        Log.d(TAG, "SelectorAsyncTask constructor");
        mConnService = connectionService;
        mSelector = selector;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        select();
        return null;
    }

    private void select() {
        while (true) {
            try {
                Log.d(TAG, "select: selector monitoring");
                mSelector.select();
                Iterator<SelectionKey> keys = mSelector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey selKey = keys.next();
                    keys.remove();
                    processSelectionKey(mSelector, selKey);
                }
            } catch (Exception e) {
                Log.e(TAG, "select: error - " + e.toString());
                notifyConnectionService(MSG_SELECT_ERROR, null, null);
                break;
            }
        }
    }

    /**
     * Process the event popped to the selector.
     */
    private void processSelectionKey(Selector selector,
                            SelectionKey selKey) {
        // there is a connection to the server socket channel
        if (selKey.isValid() && selKey.isAcceptable()) {
            openHostSocket(selector, selKey);
        } else if (selKey.isValid() && selKey.isConnectable()) {
            // client connect to server got the response
            openClientSocket(selKey);
        } else if (selKey.isValid() && selKey.isReadable()) {
            readFromSocket(selKey);
        } else if (selKey.isValid() && selKey.isWritable()) {
            writeToSocket(selKey);
        }
    }

    private boolean openHostSocket(Selector selector, SelectionKey selKey){
        Log.d(TAG, "openHostSocket");
        ServerSocketChannel ssChannel = (ServerSocketChannel) selKey.channel();
        try {
            SocketChannel sChannel = ssChannel.accept();
            sChannel.configureBlocking(false);
            SelectionKey socketKey = sChannel.register(selector,
                    SelectionKey.OP_READ);
            socketKey.attach("accepted_client " +
                    sChannel.socket().getInetAddress().getHostAddress());
            notifyConnectionService(MSG_NEW_CLIENT, sChannel, null);
        } catch (IOException e) {
            selKey.cancel();
            Log.e(TAG, "openHostSocket error:  " + e.toString());
        }
        return true;
    }

    private boolean openClientSocket(SelectionKey selKey) {
        Log.d(TAG, "openClientSocket");
        SocketChannel sChannel = (SocketChannel) selKey.channel();
        try {
            if (!sChannel.finishConnect())
                selKey.cancel();
        } catch (IOException e) {
            selKey.cancel();
            Log.e(TAG, "openClientSocket error:  " + e.toString());
        }
        notifyConnectionService(MSG_FINISH_CONNECT, sChannel, null);
        return true;
    }

    private boolean readFromSocket(SelectionKey selKey){
        Log.d(TAG, "processSelectionKey: remote client is " +
                "readable, read data: " + selKey.attachment());
        doReadable((SocketChannel) selKey.channel());
        return true;
    }

    private boolean writeToSocket(SelectionKey selKey){
        Log.d(TAG, "writeToSocket: writing to socket");
        selKey.channel();
        return true;
    }

    /**
     * Handle the readable event from selector.
     */
    private void doReadable(SocketChannel channel) {
        String data = readData(channel);
        if (data != null) {
            Bundle b = new Bundle();
            b.putString("DATA", data);
            notifyConnectionService(MSG_PULLIN_DATA, channel, b);
        }
    }

    /**
     * Read data when OP_READ event.
     */
    private String readData(SocketChannel sChannel) {
        ByteBuffer buf = ByteBuffer.allocate(1024 * 4);
        String jsonString = null;
        try {
            buf.clear();
            int numBytesRead = sChannel.read(buf);
            if (numBytesRead == -1)
                handleBrokenSocket(sChannel);
            else
                jsonString = readBuffer(buf);
        } catch (Exception e) {
            Log.e(TAG, "readData : exception: " + e.toString());
            notifyConnectionService(MSG_BROKEN_CONN, sChannel, null);
        }
        Log.d(TAG, "readData: content: " + jsonString);
        return jsonString;
    }

    /**
     * If socket channel is broken (reads -1) then remove it from
     * the selector.
     */
    private boolean handleBrokenSocket(SocketChannel sChannel){
        Log.e(TAG, "readData: channel closed due to read -1");
        try {
            sChannel.close();
        } catch (IOException e) {
            Log.e(TAG, "handleBrokenSocket: exception - " + e.toString());
        }
        notifyConnectionService(MSG_BROKEN_CONN, sChannel, null);
        return true;
    }

    private String readBuffer(ByteBuffer buf){
        Log.d(TAG, "readData: bufpos: limit : " + buf.position() +
                " : " + buf.limit() + " : " + buf.capacity());
        buf.flip();
        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes);
        return new String(bytes);
    }

    private void notifyConnectionService(int what, Object obj, Bundle data) {
        Handler hdl = mConnService.getHandler();
        Message msg = hdl.obtainMessage();
        msg.what = what;
        if (obj != null)
            msg.obj = obj;
        if (data != null)
            msg.setData(data);
        hdl.sendMessage(msg);
    }
}
