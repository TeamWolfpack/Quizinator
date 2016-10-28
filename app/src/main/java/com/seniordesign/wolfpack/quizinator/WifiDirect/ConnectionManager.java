package com.seniordesign.wolfpack.quizinator.WifiDirect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

/**
 * This class encapsulate the NIO buffer and NIO channel on top of
 * socket. It is all abt NIO style.
 *
 * SSLServerSocketChannel, ServerSocketChannel, SocketChannel,
 * Selector, ByteBuffer, etc.
 *
 * NIO buffer (ByteBuffer) either in writing mode or in reading mode.
 * Need to flip the mode before reading or writing.
 *
 * You know when a socket channel disconnected when you read -1 or
 * write exception. You need app level ACK.
 *
 * @creation 10/26/2016
 */
public class ConnectionManager {

    private final String TAG = "ConnectionManager";

    private Context mContext;
    ConnectionService mService;
    WifiDirectApp mApp;

    // Server knows all clients. key is ip addr, value is socket channel.
    // when remote client screen on, a new connection with the same ip addr is established.
    private Map<String, SocketChannel> mClientChannels =
            new HashMap<String, SocketChannel>();

    // global selector and channels
    private Selector mClientSelector = null;
    private Selector mServerSelector = null;
    private ServerSocketChannel mServerSocketChannel = null;
    private SocketChannel mClientSocketChannel = null;

    String mClientAddr = null;
    String mServerAddr = null;

    public ConnectionManager(ConnectionService service) {
        mService = service;
        mApp = (WifiDirectApp) mService.getApplication();
    }

    public void configIPV4() {
        // by default Selector attempts to work on IPv6 stack.
        java.lang.System.setProperty("java.net.preferIPv4Stack",
                "true");
        java.lang.System.setProperty("java.net.preferIPv6Addresses",
                "false");
    }

    /**
     * Create a server socket channel to listen to the port for
     * incoming connections.
     */
    public static ServerSocketChannel createServerSocketChannel(int port)
            throws IOException {
        // Create a non-blocking socket channel
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.configureBlocking(false);
        ServerSocket serverSocket = ssChannel.socket();
        // bind to the port to listen.
        serverSocket.bind(new InetSocketAddress(port));
        return ssChannel;
    }

    /**
     * Creates a non-blocking socket channel to connect to
     * specified host name and port.
     * connect() is called on the new channel before it is returned.
     */
    public static SocketChannel createSocketChannel(String hostName,
                                    int port) throws IOException {
        // Create a non-blocking socket channel
        SocketChannel sChannel = SocketChannel.open();
        sChannel.configureBlocking(false);

        // Send a connection request to the server; this method is non-blocking
        sChannel.connect(new InetSocketAddress(hostName, port));
        return sChannel;
    }


    /**
     * Create a socket channel and connect to the host.
     * After return, the socket channel guaranteed to be connected.
     */
    public SocketChannel connectTo(String hostname, int port)
            throws Exception {
        SocketChannel sChannel = null;

        // connect to the remote host, port
        sChannel = createSocketChannel(hostname, port);

        // Before the socket is usable, the connection must
        // be completed. finishConnect().
        while (!sChannel.finishConnect()) {
            // blocking spin lock
        }

        // Socket channel is now ready to use
        return sChannel;
    }

    /**
     * Client, after p2p connection available, connect to group
     * owner and select monitoring the sockets. Start blocking
     * selector monitoring in an async task, infinite loop
     */
    public int startClientSelector(String host) {
        closeServer();   // close linger server.

        if (mClientSocketChannel != null) {
            Log.d(TAG, "startClientSelector : client already connected to server: " +
                    mClientSocketChannel.socket().getLocalAddress().getHostAddress());
            return -1;
        }

        try {
            // connected to the server upon start client.
            SocketChannel sChannel = connectTo(host, 1080);

            mClientSelector = Selector.open();
            mClientSocketChannel = sChannel;
            mClientAddr = mClientSocketChannel.socket().getLocalAddress().getHostName();
            sChannel.register(mClientSelector, SelectionKey.OP_READ);
            mApp.setMyAddress(mClientAddr);
            mApp.clearMessages();

            // start selector monitoring, blocking
            new SelectorAsyncTask(mService, mClientSelector).execute();
            return 0;

        } catch (Exception e) {
            mClientSelector = null;
            mClientSocketChannel = null;
            mApp.setMyAddress(null);

            return -1;
        }
    }

    /**
     * Create a selector to manage a server socket channel.
     * The registration process yields an object called a selection
     * key which identifies the selector/socket channel pair.
     */
    public int startServerSelector() {
        closeClient();   // close linger client, if exists.

        try {
            // create server socket and register to selector to listen OP_ACCEPT event
            // BindException if already bind.
            ServerSocketChannel sServerChannel = createServerSocketChannel(1080);
            mServerSocketChannel = sServerChannel;
            mServerAddr = mServerSocketChannel.socket().getInetAddress().getHostAddress();
            if ("0.0.0.0".equals(mServerAddr)) {
                mServerAddr = "Master";
            }
            ((WifiDirectApp) mService.getApplication()).setMyAddress(mServerAddr);

            mServerSelector = Selector.open();
            SelectionKey acceptKey = sServerChannel.register(
                    mServerSelector, SelectionKey.OP_ACCEPT);
            acceptKey.attach("accept_channel");
            mApp.mIsServer = true;

            //SocketChannel sChannel = createSocketChannel("hostname.com", 80);
            //sChannel.register(selector, SelectionKey.OP_CONNECT);  // listen to connect event.
            Log.d(TAG, "startServerSelector : started: " +
                    sServerChannel.socket().getLocalSocketAddress().toString());

            new SelectorAsyncTask(mService, mServerSelector).execute();
            return 0;

        } catch (Exception e) {
            Log.e(TAG, "startServerSelector : exception: " + e.toString());
            return -1;
        }
    }

    /**
     * Handle selector error, re-start.
     */
    public void onSelectorError() {
        Log.e(TAG, " onSelectorError : do nothing for now.");
        // new SelectorAsyncTask(mService, mSelector).execute();
    }

    /**
     * A device can only be either group owner, or group client, not
     * both. When we start as client, close server, if existing due
     * to linger connection.
     */
    public void closeServer() {
        if (mServerSocketChannel != null) {
            try {
                mServerSocketChannel.close();
                mServerSelector.close();
            } catch (Exception e) {

            } finally {
                mApp.mIsServer = false;
                mServerSocketChannel = null;
                mServerSelector = null;
                mServerAddr = null;
                mClientChannels.clear();
            }
        }
    }

    public void closeClient() {
        if (mClientSocketChannel != null) {
            try {
                mClientSocketChannel.close();
                mClientSelector.close();
            } catch (Exception e) {

            } finally {
                mClientSocketChannel = null;
                mClientSelector = null;
                mClientAddr = null;
            }
        }
    }

    /**
     * Read out -1, connection broken, remove it from clients collection
     */
    public void onBrokenConnection(SocketChannel schannel) {
        try {
            String peeraddr = schannel.socket().getInetAddress().getHostAddress();
            if (mApp.mIsServer) {
                mClientChannels.remove(peeraddr);
                Log.d(TAG, "onBrokenConnection : client down: " + peeraddr);
            } else {
                Log.d(TAG, "onBrokenConnection : set null client " +
                        "channel after server down: " + peeraddr);
                closeClient();
            }
            schannel.close();
        } catch (Exception e) {
            //PTPLog.e(TAG, "onBrokenConnection: close channel: " + e.toString());
        }
    }

    /**
     * Server handle new client coming in.
     */
    public void onNewClient(SocketChannel schannel) {
        String ipaddr = schannel.socket().getInetAddress().getHostAddress();
        Log.d(TAG, "onNewClient : server added remote client: " + ipaddr);
        mClientChannels.put(ipaddr, schannel);
    }

    /**
     * Client's connect to server success.
     */
    public void onFinishConnect(SocketChannel schannel) {
        String clientaddr = schannel.socket().getLocalAddress().getHostAddress();
        String serveraddr = schannel.socket().getInetAddress().getHostAddress();
        Log.d(TAG, "onFinishConnect : client connect to server succeed : " +
                clientaddr + " -> " + serveraddr);
        mClientSocketChannel = schannel;
        mClientAddr = clientaddr;
        ((WifiDirectApp) mService.getApplication()).setMyAddress(mClientAddr);
    }

    /**
     * Client send data into server, server pub to all clients.
     */
    public void onDataIn(SocketChannel schannel, String data) {
        Log.d(TAG, "connection onDataIn : " + data);
        // push all _other_ clients if the device is the server
        if (mApp.mIsServer) {
            publishDataToAllClients(data, schannel);
        }
    }

    /**
     * Write byte buf to the socket channel.
     */
    private int writeData(SocketChannel sChannel, String jsonString) {
        byte[] buf = jsonString.getBytes();
        // wrap the buf into byte buffer
        ByteBuffer bytebuf = ByteBuffer.wrap(buf);
        int nwritten = 0;
        try {
            //bytebuf.flip();  // no flip after creating from wrap.
            Log.d(TAG, "writeData: start:limit = " +
                    bytebuf.position() + " : " + bytebuf.limit());
            nwritten = sChannel.write(bytebuf);
        } catch (Exception e) {
            // Connection may have been closed
            Log.e(TAG, "writeData: exception : " + e.toString());
            onBrokenConnection(sChannel);
        }
        Log.d(TAG, "writeData: content: " + new String(buf) +
                "  : len: " + nwritten);
        return nwritten;
    }

    /**
     * Server publish data to all the connected clients.
     */
    private void publishDataToAllClients(String msg,
                                         SocketChannel incomingChannel) {
        Log.d(TAG, "publishDataToAllClients : isServer ? " +
                mApp.mIsServer + " msg: " + msg);
        if (!mApp.mIsServer) {
            return;
        }

        for (SocketChannel s : mClientChannels.values()) {
            if (s != incomingChannel) {
                String peeraddr = s.socket().getInetAddress().getHostAddress();
                Log.d(TAG, "publishDataToAllClients : Server pub data to:  " + peeraddr);
                writeData(s, msg);
            }
        }
    }

    /**
     * The device want to push out data.
     *  If the device is client, the only channel is to the server.
     *  If the device is server, it just pub the data to all clients
     *      for now.
     */
    public int pushOutData(String jsonString) {
        if (!mApp.mIsServer) {   // device is client, can only send
            // to server
            sendDataToServer(jsonString);
        } else {
            // server pub to all clients, msg already appended with
            // sender addr inside send button handler.
            publishDataToAllClients(jsonString, null);
        }
        return 0;
    }

    /**
     * Whenever client write to server, carry the format
     *  of "client_addr : msg "
     */
    private int sendDataToServer(String jsonString) {
        if (mClientSocketChannel == null) {
            Log.d(TAG, "sendDataToServer: channel not connected ! waiting...");
            return 0;
        }
        String hostAddress = mClientSocketChannel.socket().getInetAddress().getHostAddress();
        Log.d(TAG, "sendDataToServer: " + mClientAddr + " -> " +
                hostAddress + " : " + jsonString);
        return writeData(mClientSocketChannel, jsonString);
    }
}