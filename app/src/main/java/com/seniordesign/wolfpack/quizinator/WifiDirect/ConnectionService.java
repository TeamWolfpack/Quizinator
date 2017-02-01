package com.seniordesign.wolfpack.quizinator.WifiDirect;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.*;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Activities.HostGameActivity;
import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Database.Card;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Messages.Answer;
import com.seniordesign.wolfpack.quizinator.Messages.Confirmation;
import com.seniordesign.wolfpack.quizinator.Messages.QuizMessage;

/**
 * @creation 10/26/2016
 */
public class ConnectionService extends Service implements
        ChannelListener,
        PeerListListener,
        ConnectionInfoListener {  // callback of requestPeers{

    private static final String TAG = "ConnServ";

    private static ConnectionService _sinstance = null;

    private WorkHandler mWorkHandler;
    private MessageHandler mHandler;

    boolean retryChannel = false;

    WifiDirectApp wifiDirectApp;
    MainMenuActivity mActivity; // shall I use weak reference here ?
    ConnectionManager mConnMan;

    private void initializeConnectionService() {
        Log.d(TAG, "initializeConnectionService");

        if (_sinstance != null) {
            return;
        }

        _sinstance = this;
        mWorkHandler = new WorkHandler(TAG);
        mHandler = new MessageHandler(mWorkHandler.getLooper());

        wifiDirectApp = (WifiDirectApp) getApplication();
        wifiDirectApp.mP2pMan = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifiDirectApp.mP2pChannel = wifiDirectApp.mP2pMan.initialize(this, mWorkHandler.getLooper(), null);

        mConnMan = new ConnectionManager(this);
    }

    public static ConnectionService getInstance() {
        return _sinstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeConnectionService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initializeConnectionService();
        processIntent(intent);
        Log.d(TAG, "onStartCommand: START_STICKY - " + START_STICKY); //TODO remove later
        return START_STICKY;
    }

    private void processIntent(Intent intent) {
        if (intent == null)
            return;
        String action = intent.getAction();
        Log.d(TAG, "processIntent: Action - " + action); //TODO remove later
        if (action == null)
            return;
        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                Log.d(TAG, "processIntent: State change"); //TODO remove later
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                Log.d(TAG, "processIntent: State - " + state); //TODO remove later
                deviceWifiStateChangedAction(state);
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                //find all peers
                Log.d(TAG, "processIntent: peers changed"); //TODO remove later
                deviceWifiPeersChangedAction();
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                //if select p2p server mode with create group, this
                // device will be group owner automatically
                if (wifiDirectApp.mP2pMan == null)
                    return;
                Log.d(TAG, "processIntent: connection changed"); //TODO remove later
                deviceConnectionChangedAction(intent);
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                //p2p connected, for client, this device
                // changed to connected first
                deviceDetailsHaveChanged(intent);
                Log.d(TAG, "processIntent: this device changed"); //TODO remove later
                break;
            default:
                break;
        }
    }

    private boolean deviceWifiStateChangedAction(int state) {
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            // Wifi Direct mode is enabled
            wifiDirectApp.mP2pChannel = wifiDirectApp.mP2pMan.initialize(this,
                    mWorkHandler.getLooper(), null);
            AppPreferences.setStringToPref(wifiDirectApp,
                    AppPreferences.PREF_NAME, AppPreferences.P2P_ENABLED, "1");
            return true;
        } else {
            wifiDirectApp.mThisDevice = null;    // reset this device status
            wifiDirectApp.mP2pChannel = null;
            wifiDirectApp.mPeers.clear();
            if (wifiDirectApp.mHomeActivity != null) {
                wifiDirectApp.mHomeActivity.updateThisDevice(null);
                wifiDirectApp.mHomeActivity.resetData();
            }
            AppPreferences.setStringToPref(wifiDirectApp,
                    AppPreferences.PREF_NAME, AppPreferences.P2P_ENABLED, "0");
            return false;
        }
    }

    private boolean deviceWifiPeersChangedAction() {
        // a list of peers are available after discovery,
        // use PeerListListener to collect request available
        // peers from the wifi p2p manager. This is an
        // asynchronous call and the calling activity is
        // notified with callback on
        // PeerListListener.onPeersAvailable()
        if(wifiDirectApp.mManageActivity != null){
            wifiDirectApp.mManageActivity.validateAnswer(null);
        }
        if (wifiDirectApp.mP2pMan != null && wifiDirectApp.mP2pChannel != null) {
            wifiDirectApp.mP2pMan.requestPeers(wifiDirectApp.mP2pChannel, this);
            return true;
        }
        return false;
    }

    private boolean deviceConnectionChangedAction(Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (networkInfo.isConnected()) {
            Log.d(TAG, "processIntent: WIFI_P2P_CONNECTION_CHANGED_ACTION: p2p connected ");
            // Connected with the other device, request connection
            // info for group owner IP. Callback inside details fragment.
            wifiDirectApp.mP2pMan.requestConnectionInfo(wifiDirectApp.mP2pChannel, this);
            return true;
        } else {
            // It's a disconnect
            Log.d(TAG, "processIntent: WIFI_P2P_CONNECTION_CHANGED_ACTION: p2p " +
                    "disconnected, mP2pConnected = false..closeClient..");
            wifiDirectApp.mP2pConnected = false;
            wifiDirectApp.mP2pInfo = null;   // reset connection info
            // after connection done.
            mConnMan.closeClient();
            if (wifiDirectApp.mHomeActivity != null)
                wifiDirectApp.mHomeActivity.resetData();

            //End gameplay if client
            if (wifiDirectApp.mGameplayActivity != null)
                wifiDirectApp.mGameplayActivity.endGamePlay();

            return false;
        }
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    private boolean deviceDetailsHaveChanged(Intent intent) {
        if(intent.hasExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)){
            wifiDirectApp.mThisDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            wifiDirectApp.mDeviceName = wifiDirectApp.mThisDevice.deviceName;
            if (wifiDirectApp.mHomeActivity != null) {
                wifiDirectApp.mHomeActivity.updateThisDevice(wifiDirectApp.mThisDevice);
                return true;
            }
        }
        return false;
    }

    /**
     * The channel to the framework Wifi P2p has been disconnected.
     * Could try re-initializing.
     */
    @Override
    public void onChannelDisconnected() {
        if (!retryChannel) {
            wifiDirectApp.mP2pChannel = wifiDirectApp.mP2pMan.initialize(this, mWorkHandler.getLooper(), null);
            if (wifiDirectApp.mHomeActivity != null) {
                wifiDirectApp.mHomeActivity.resetData();
            }
            retryChannel = true;
        } else {
            if (wifiDirectApp.mHomeActivity != null) {
                wifiDirectApp.mHomeActivity.onChannelDisconnected();
            }
            stopSelf();
        }
    }

    /**
     * The callback of requestPeers upon
     * WIFI_P2P_PEERS_CHANGED_ACTION intent.
     */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.d(TAG, "onPeersAvailable: peers available");
        wifiDirectApp.mPeers.clear();
        wifiDirectApp.mPeers.addAll(peerList.getDeviceList());
        WifiP2pDevice connectedPeer = wifiDirectApp.getConnectedPeer();
        if (connectedPeer != null) {
            //PTPLog.d(TAG, "onPeersAvailable : exist connected peer : " + connectedPeer.deviceName);
        }
        if (wifiDirectApp.mP2pInfo != null && connectedPeer != null) {
            if (wifiDirectApp.mP2pInfo.groupFormed && wifiDirectApp.mP2pInfo.isGroupOwner) {
                wifiDirectApp.startSocketServer();
            } else if (wifiDirectApp.mP2pInfo.groupFormed && connectedPeer != null) {
                // XXX client path goes to connection info available after connection established.
                // PTPLog.d(TAG, "onConnectionInfoAvailable: device is client, connect to group owner: startSocketClient ");
                // mApp.startSocketClient(mApp.mP2pInfo.groupOwnerAddress.getHostAddress());
            }
        }
        if (wifiDirectApp.mHomeActivity != null) {
            wifiDirectApp.mHomeActivity.onPeersAvailable(peerList);
        }
    }

    /**
     * the callback of when the _Requested_ connectino info is available.
     * WIFI_P2P_CONNECTION_CHANGED_ACTION intent, requestConnectionInfo()
     */
    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if(info.groupOwnerAddress != null)
            Log.d(TAG, "onConnectionInfoAvailable: " + info.groupOwnerAddress.getHostAddress());
        if (info.groupFormed && info.isGroupOwner) {
            // XXX server path goes to peer connected.
            //new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
            //Log.d(TAG, "onConnectionInfoAvailable: device is groupOwner: startSocketServer ");
            // mApp.startSocketServer();
        } else if (info.groupFormed) {
            wifiDirectApp.startSocketClient(info.groupOwnerAddress.getHostAddress());
        }
        wifiDirectApp.mP2pConnected = true;
        wifiDirectApp.mP2pInfo = info;   // connection info available
    }

    private void sendMessageToUpDatePeerListFragment() {
        if (wifiDirectApp.mHomeActivity != null) {
            wifiDirectApp.mHomeActivity.onConnectionInfoAvailable(wifiDirectApp.mP2pInfo);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return new Binder();
    }

    public Handler getHandler() {
        return mHandler;
    }

    /**
     * message handler looper to handle all the msg sent to location manager.
     */
    final class MessageHandler extends Handler {
        public MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            processMessage(msg);
        }
    }

    /**
     * The main message process loop.
     */
    private void processMessage(Message msg) {
        Log.d(TAG, "processMessage: message - " + msg.toString());

        switch (msg.what) {
            case MSG_NULL:
                break;
            case MSG_REGISTER_ACTIVITY:
                onActivityRegister((MainMenuActivity) msg.obj, msg.arg1);
                break;
            case MSG_STARTSERVER:
                if (mConnMan.startServerSelector() >= 0) {
                    sendMessageToUpDatePeerListFragment();
                }
                break;
            case MSG_STARTCLIENT:
                if (mConnMan.startClientSelector((String) msg.obj) >= 0) {
                    sendMessageToUpDatePeerListFragment();
                }
                break;
            case MSG_NEW_CLIENT:
                mConnMan.onNewClient((SocketChannel) msg.obj);
                break;
            case MSG_FINISH_CONNECT:
                mConnMan.onFinishConnect((SocketChannel) msg.obj);
                break;
            case MSG_PULLIN_DATA:
                onPullInData((SocketChannel)msg.obj, msg.getData());
                break;
            case MSG_PUSHOUT_DATA:
                onPushOutData((String) msg.obj);
                break;
            case MSG_SEND_RULES_ACTIVITY:
                pushAllRulesOut((String) msg.obj);
                break;
            case MSG_SEND_CARD_ACTIVITY:
                pushCardOut((String) msg.obj);
                break;
            case MSG_PLAYER_READY_ACTIVITY:
                pushReadyOut((String) msg.obj);
                break;
            case MSG_SEND_ANSWER_ACTIVITY:
                pushAnswerOut((String) msg.obj);
                break;
            case MSG_ANSWER_CONFIRMATION_ACTIVITY:
                pushConfirmationOut((String) msg.obj);
                break;
            case MSG_END_OF_GAME_ACTIVITY:
                pushEndOfGameOut((String) msg.obj);
                break;
            case MSG_SELECT_ERROR:
                mConnMan.onSelectorError();
                break;
            case MSG_BROKEN_CONN:
                mConnMan.onBrokenConnection((SocketChannel) msg.obj);
                break;
            case MSG_DISCONNECT_FROM_ALL_PEERS:
                pushDisconnectAllPeersOut((String) msg.obj);
                break;
            default:
                break;
        }
    }

    /**
     * Register the activity that uses this service.
     */
    private void onActivityRegister(MainMenuActivity activity,
                                    int register) {
        Log.d(TAG, "onActivityRegister : activity register " +
                "itself to service : " + register);
        if (register == 1) {
            mActivity = activity;
        } else {
            // set to null explicitly to avoid mem leak.
            mActivity = null;
        }
    }

    /**
     * Handle data push out request.
     * If the sender is the server, pub to all client.
     * If the sender is client, only can send to the server.
     */
    private void onPushOutData(String data) {
        Log.d(TAG, "onPushOutData : " + data);
        mConnMan.pushOutData(data);
    }

    /*
     * @author leonardj (11/4/16)
     */
    public static boolean sendMessage(int code, String message) {
        Message result = getInstance().getHandler().obtainMessage();
        result.what = code;
        result.obj = message;
        return getInstance().getHandler().sendMessage(result);
    }


    /*
     * @author leonardj (11/4/16)
     */
    private String createQuizMessage(int code, String message) {
        return new Gson().toJson(new QuizMessage(code, message));
    }

    /*
     * @author leonardj (10/31/2016)
     */
    private void pushCardOut(String data){
        Log.d(TAG, "pushCardOut: " + data);

        String message = createQuizMessage(MSG_SEND_CARD_ACTIVITY, data);
        mConnMan.pushOutData(message);
    }

    /*
     * @author kuczynskij (11/01/2016)
     * @author leonardj (10/31/2016)
     */
    private void pushAllRulesOut(String data){
        Log.d(TAG, "pushAllRulesOut: " + data);
        //anything we may need to the rules string

        String message = createQuizMessage(MSG_SEND_RULES_ACTIVITY, data);
        mConnMan.pushOutData(message);
    }

    /*
     * @author leonardj (11/4/16)
     */
    private void pushReadyOut(String data) {
        Log.d(TAG, "pushReadyOut: " + data);

        String message = createQuizMessage(MSG_PLAYER_READY_ACTIVITY, data);
        mConnMan.pushOutData(message);
    }

    /*
     * @author leonardj (11/4/16)
     */
    private void pushAnswerOut(String data) {
        Log.d(TAG, "pushAnswerOut: " + data);

        String message = createQuizMessage(MSG_SEND_ANSWER_ACTIVITY, data);
        mConnMan.pushOutData(message);
    }

    /*
     * @author leonardj (11/4/16)
     */
    private void pushConfirmationOut(String data) {
        Log.d(TAG, "pushConfirmationOut: " + data);

        Confirmation confirmation = new Gson().fromJson(data, Confirmation.class);
        String message = createQuizMessage(
                MSG_ANSWER_CONFIRMATION_ACTIVITY,
                String.valueOf(confirmation.getConfirmation())
        );
        mConnMan.publishDataToSingleClient(message, confirmation.getClientAddress());
    }

    /**
     * Allows the host to send out a message to end the game for all
     * players in the game session.
     * @param data
     */
    private void pushEndOfGameOut(String data) {
        Log.d(TAG, "pushEndOfGameOut: " + data);

        String message = createQuizMessage(MSG_END_OF_GAME_ACTIVITY, data);
        mConnMan.pushOutData(message);
    }

    private void pushDisconnectAllPeersOut(String data) {
        Log.d(TAG, "pushDisconnectAllPeersOut: " + data);
        String message = createQuizMessage(MSG_DISCONNECT_FROM_ALL_PEERS, data);
        mConnMan.pushOutData(message);
    }

    /**
     * service handle data in come from socket channel
     */
    private String onPullInData(SocketChannel schannel, Bundle b){
        String data = b.getString("DATA");
        Log.d(TAG, "onDataIn : recvd msg : " + data);

        Gson gson = new Gson();
        List<QuizMessage> messages = parseInData(data);

        for (QuizMessage msg: messages) {
            int code = msg.getCode();
            String message = msg.getMessage();
            Log.d(TAG, "DataInMessage : code: " + code + ", msg: " + message);

            switch(code){
                case MSG_SEND_RULES_ACTIVITY:
                    Rules r = gson.fromJson(message, Rules.class);
                    Log.d(TAG, r.toString()); //TODO
                    wifiDirectApp.mHomeActivity.startMultiplayerGamePlay(r);
                    break;
                case MSG_SEND_CARD_ACTIVITY:
                    Card card = gson.fromJson(message, Card.class);
                    wifiDirectApp.mGameplayActivity.receivedNextCard(card);
                    break;
                case MSG_PLAYER_READY_ACTIVITY:
                    String deviceName = message;
                    Log.d(TAG, "deviceName - " + deviceName);
                    if (wifiDirectApp.mManageActivity != null)
                        wifiDirectApp.mManageActivity.deviceIsReady(deviceName);
                    break;
                case MSG_SEND_ANSWER_ACTIVITY:
                    Answer answer = gson.fromJson(message, Answer.class);
                    wifiDirectApp.mManageActivity.validateAnswer(answer);
                    break;
                case MSG_ANSWER_CONFIRMATION_ACTIVITY:
                    boolean correct = Boolean.parseBoolean(message);
                    wifiDirectApp.mGameplayActivity.answerConfirmed(correct);
                    break;
                case MSG_END_OF_GAME_ACTIVITY:
                    long totalGameTime = Long.parseLong(message);
                    wifiDirectApp.mGameplayActivity.endGamePlay(totalGameTime);
                    break;
                case MSG_DISCONNECT_FROM_ALL_PEERS:
                    if(wifiDirectApp.mHomeActivity != null){
                        wifiDirectApp.mHomeActivity.disconnect();
                        wifiDirectApp.mHomeActivity.finish();
                    }
                    break;
            }
        }
        return data;
    }

    /*
     * @author leonardj (11/29/16)
     */
    public  List<QuizMessage> parseInData(String data) {
        Gson gson = new Gson();
        ArrayList<QuizMessage> messages = new ArrayList<>();

        Log.d(TAG, "inData : " + data);

        String[] chunks = data.split("\\}\\{");
        Log.d(TAG, "Message Count : " + chunks.length);
        for (int i = 0; i < chunks.length; i++) {
            String chunk = chunks[i];
            if (i < chunks.length - 1) {
                chunk += "}";
            }
            if (i > 0) {
                chunk = "{" + chunk;
            }

            Log.d(TAG, "Chunk : " + chunk);
            QuizMessage message = gson.fromJson(chunk, QuizMessage.class);
            messages.add(message);
            Log.d(TAG, "Messages in List : " + messages.size());
        }
        return messages;
    }

    /**
     * Sync call to send data using conn man's channel,
     * as conn man now is blocking on select.
     */
    public int connectionSendData(String jsonstring) {
        Log.d(TAG, "connectionSendData : " + jsonstring);
        new SendDataAsyncTask(mConnMan, jsonstring).execute();
        return 0;
    }

    /**
     * Write data in an async task to avoid
     * NetworkOnMainThreadException.
     */
    public class SendDataAsyncTask extends AsyncTask<Void, Void, Integer> {
        private String data;
        private ConnectionManager connman;

        public SendDataAsyncTask(ConnectionManager conn,
                                 String jsonstring) {
            connman = conn;
            data = jsonstring;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return connman.pushOutData(data);
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.d(TAG, "SendDataAsyncTask : onPostExecute:  " +
                    data + " len: " + result);
        }
    }

    public static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown = " + deviceStatus;
        }
    }
}
