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
 */
public class ConnectionManager {

    private final String TAG = "PTP_ConMan";

    private ConnectionService mService;
    private WifiDirectApp mApp;

    // Server knows all clients. key is ip addr, value is socket channel.
    // when remote client screen on, a new connection with the same ip
    // addr is established.
    private Map<String, SocketChannel> mClientChannels = new HashMap<>();

    // global selector and channels
    private Selector mClientSelector = null;
    private Selector mServerSelector = null;
    private ServerSocketChannel mServerSocketChannel = null;
    private SocketChannel mClientSocketChannel = null;

    private String mClientAddr = null;
    private String mServerAddr = null;

    public ConnectionManager(ConnectionService service) {
        Log.d(TAG, "create a ConnectionManager()");
        mService = service;
        mApp = (WifiDirectApp) mService.getApplication();
    }

    /**
     * Create a server socket channel to listen to the port for
     * incoming connections.
     */
    private static ServerSocketChannel createServerSocketChannel(
            int port) throws IOException {
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.configureBlocking(false);
        ServerSocket serverSocket = ssChannel.socket();
        serverSocket.bind(new InetSocketAddress(port));
        return ssChannel;
    }

    /**
     * Creates a non-blocking socket channel to connect to
     * specified host name and port.
     * connect() is called on the new channel before it is returned.
     */
    private static SocketChannel createSocketChannel(String hostName,
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
    private SocketChannel connectTo(String hostname, int port) {
        Log.d(TAG, "connectTo");
        SocketChannel sChannel = null;
        try{
            sChannel = createSocketChannel(hostname, port);
            while (!sChannel.finishConnect()); // blocking spin lock
        }catch(IOException e){
            Log.d(TAG, "connectTo error: " + e);
        }
        return sChannel;
    }

    /**
     * Client, after p2p connection available, connect to group
     * owner and select monitoring the sockets. Start blocking
     * selector monitoring in an async task, infinite loop
     */
    int startClientSelector(String host) {
        Log.d(TAG, "startClientSelector, clientSocketChannel: " + mClientSocketChannel);
        closeServer();
        if (mClientSocketChannel != null)
            return -1;
        try {
            SocketChannel sChannel = connectTo(host, 1080);
            mClientSelector = Selector.open();
            mClientSocketChannel = sChannel;
            mClientAddr = mClientSocketChannel.socket().getLocalAddress().getHostName();
            sChannel.register(mClientSelector, SelectionKey.OP_READ);
            mApp.setMyAddress(mClientAddr);
            mApp.clearMessages();
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
    int startServerSelector() {
        Log.d(TAG, "startServerSelector");
        closeClient();
        try {
            ServerSocketChannel sServerChannel = createServerSocketChannel(1080);
            mServerSocketChannel = sServerChannel;
            mServerAddr = mServerSocketChannel.socket().getInetAddress().getHostAddress();
            if ("0.0.0.0".equals(mServerAddr))
                mServerAddr = "Master";
            ((WifiDirectApp) mService.getApplication()).setMyAddress(mServerAddr);
            mServerSelector = Selector.open();
            SelectionKey acceptKey = sServerChannel.register(
                            mServerSelector, SelectionKey.OP_ACCEPT);
            acceptKey.attach("accept_channel");
            mApp.mIsServer = true;
            new SelectorAsyncTask(mService, mServerSelector).execute();
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "startServerSelector: exception " + e.toString());
            return -1;
        }
    }

    /**
     * A device can only be either group owner, or group client, not
     * both. When we start as client, close server, if existing due
     * to linger connection.
     */
    private void closeServer() {
        Log.d(TAG, "closeServer");
        if (mServerSocketChannel != null) {
            try {
                mServerSocketChannel.close();
                mServerSelector.close();
            } catch (Exception e) {
                Log.e(TAG, "error: " + e.toString());
            } finally {
                mApp.mIsServer = false;
                mServerSocketChannel = null;
                mServerSelector = null;
                mServerAddr = null;
                mClientChannels.clear();
            }
        }
    }

    void closeClient() {
        Log.d(TAG, "closeClient");
        if (mClientSocketChannel != null) {
            try {
                mClientSocketChannel.close();
                mClientSelector.close();
            } catch (Exception e) {
                Log.d(TAG, "closeClient error: " + e);
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
    void onBrokenConnection(SocketChannel channel) {
        try {
            if (mApp.mIsServer)
                mClientChannels.remove(channel.socket().getInetAddress().getHostAddress());
            else
                closeClient();
            channel.close();
        } catch (Exception e) {
            Log.d(TAG, "onBrokenConnection error: " + e);
        }
    }

    /**
     * Server handle new client coming in.
     */
    void onNewClient(SocketChannel channel) {
        Log.d(TAG, "onNewClient");
        mClientChannels.put(channel.socket().getInetAddress().
                getHostAddress(), channel);
    }

    /**
     * Client's connect to server success.
     */
    void onFinishConnect(SocketChannel channel) {
        Log.d(TAG, "onFinishConnect");
        mClientSocketChannel = channel;
        ((WifiDirectApp) mService.getApplication()).setMyAddress(
                channel.socket().getLocalAddress().getHostAddress());
    }

    /**
     * Write byte buf to the socket channel.
     */
    private int writeData(SocketChannel sChannel, String jsonString) {
        byte[] buf = jsonString.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.wrap(buf);
        int numberWritten = 0;
        try {
            numberWritten = sChannel.write(byteBuffer);
        } catch (Exception e) {
            onBrokenConnection(sChannel);
        }
        return numberWritten;
    }

    /**
     * Server publish data to all the connected clients.
     */
    private void publishDataToAllClients(String msg,
                                     SocketChannel incomingChannel) {
        Log.d(TAG, "publishDataToAllClients");
        if (!mApp.mIsServer)
            return;
        for (SocketChannel s : mClientChannels.values()) {
            if (s != incomingChannel && s.socket().getInetAddress() != null)
                writeData(s, msg);
        }
    }

    public boolean publishDataToSingleClient(String msg,
                                          String clientAddress) {
        Log.d(TAG, "publishDataToSingleClient");
        if (!mApp.mIsServer)
            return false;
        for (SocketChannel s : mClientChannels.values()) {
            if (s.socket().getInetAddress() == null)
                continue;
            if (clientAddress.equals(s.socket().getInetAddress().
                    getHostAddress())) {
                writeData(s, msg);
                return true;
            }
        }
        return false;
    }

    void pushOutData(String jsonString) {
        Log.d(TAG, "pushOutData");
        if (!mApp.mIsServer)
            sendDataToServer(jsonString);
        else
            publishDataToAllClients(jsonString, null);
    }

    /**
     * Whenever client write to server, carry the format
     *  of "client_addr : msg "
     */
    private int sendDataToServer(String jsonString) {
        Log.d(TAG, "sendDataToServer");
        if (mClientSocketChannel == null)
            return 0;
        return writeData(mClientSocketChannel, jsonString);
    }
}
