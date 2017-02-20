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
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Database.Card;
import com.seniordesign.wolfpack.quizinator.Database.Rules;
import com.seniordesign.wolfpack.quizinator.Messages.Answer;
import com.seniordesign.wolfpack.quizinator.Messages.Confirmation;
import com.seniordesign.wolfpack.quizinator.Messages.QuizMessage;

public class ConnectionService extends Service implements
        ChannelListener, PeerListListener, ConnectionInfoListener {

    private static final String TAG = "ConnServ";

    private static ConnectionService connectionService = null;

    private WorkHandler mWorkHandler;
    private MessageHandler mHandler;

    boolean retryChannel = false;

    WifiDirectApp wifiDirectApp;
    MainMenuActivity mActivity;
    ConnectionManager mConnMan;

    private void initializeConnectionService() {
        Log.d(TAG, "initializeConnectionService");

        if (connectionService != null) {
            return;
        }

        connectionService = this;
        mWorkHandler = new WorkHandler(TAG);
        mHandler = new MessageHandler(mWorkHandler.getLooper());

        wifiDirectApp = (WifiDirectApp) getApplication();
        wifiDirectApp.mP2pMan = (WifiP2pManager)
                getSystemService(Context.WIFI_P2P_SERVICE);
        wifiDirectApp.mP2pChannel =
                wifiDirectApp.mP2pMan.initialize(this,
                        mWorkHandler.getLooper(), null);

        mConnMan = new ConnectionManager(this);
    }

    public static ConnectionService getInstance() {
        return connectionService;
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
        return START_STICKY;
    }

    private void processIntent(Intent intent) {
        if (intent == null)
            return;
        String action = intent.getAction();
        if (action == null)
            return;
        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                deviceWifiStateChangedAction(intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1));
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                //find all peers
                deviceWifiPeersChangedAction();
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                //if select p2p server mode with create group, this
                // device will be group owner automatically
                if (wifiDirectApp.mP2pMan == null)
                    return;
                deviceConnectionChangedAction(intent);
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                //p2p connected, for client, this device changed to
                // connected first
                deviceDetailsHaveChanged(intent);
                break;
            default:
                break;
        }
    }

    private boolean deviceWifiStateChangedAction(int state) {
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            wifiDirectApp.mP2pChannel =
                    wifiDirectApp.mP2pMan.initialize(this,
                    mWorkHandler.getLooper(), null);
            AppPreferences.setStringToPref(wifiDirectApp,
                    AppPreferences.PREF_NAME,
                    AppPreferences.P2P_ENABLED, "1");
            return true;
        } else {
            wifiDirectApp.mThisDevice = null;
            wifiDirectApp.mP2pChannel = null;
            wifiDirectApp.mPeers.clear();
            if (wifiDirectApp.mHomeActivity != null) {
                wifiDirectApp.mHomeActivity.updateThisDevice(null);
                wifiDirectApp.mHomeActivity.resetData();
            }
            AppPreferences.setStringToPref(wifiDirectApp,
                    AppPreferences.PREF_NAME,
                    AppPreferences.P2P_ENABLED,
                    "0");
            return false;
        }
    }

    private boolean deviceWifiPeersChangedAction() {
        if(wifiDirectApp.mManageActivity != null)
            wifiDirectApp.mManageActivity.validateAnswer(null);
        if (wifiDirectApp.mP2pMan != null && wifiDirectApp.mP2pChannel != null) {
            wifiDirectApp.mP2pMan.requestPeers(wifiDirectApp.mP2pChannel, this);
            return true;
        }
        return false;
    }

    private boolean deviceConnectionChangedAction(Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (networkInfo.isConnected()) {
            Log.d(TAG, "deviceConnectionChangedAction: WIFI_P2P_CONNECTION_CHANGED_ACTION: p2p connected ");
            // Connected with the other device, request connection
            // info for group owner IP. Callback inside details fragment.
            wifiDirectApp.mP2pMan.requestConnectionInfo(wifiDirectApp.mP2pChannel, this);
            return true;
        } else {
            // It's a disconnect
            Log.d(TAG, "deviceConnectionChangedAction: WIFI_P2P_CONNECTION_CHANGED_ACTION: p2p " +
                    "disconnected, mP2pConnected = false..closeClient..");
            wifiDirectApp.mP2pConnected = false;
            wifiDirectApp.mP2pInfo = null;
            mConnMan.closeClient();
            if (wifiDirectApp.mHomeActivity != null)
                wifiDirectApp.mHomeActivity.resetData();
            if (wifiDirectApp.mGameplayActivity != null)
                wifiDirectApp.mGameplayActivity.endGamePlay();
            return false;
        }
    }

    private boolean deviceDetailsHaveChanged(Intent intent) {
        if(intent.hasExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)){
            wifiDirectApp.mThisDevice =
                    intent.getParcelableExtra(
                            WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            wifiDirectApp.mDeviceName =
                    wifiDirectApp.mThisDevice.deviceName;
            if (wifiDirectApp.mHomeActivity != null) {
                wifiDirectApp.mHomeActivity.updateThisDevice(
                        wifiDirectApp.mThisDevice);
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
            wifiDirectApp.mP2pChannel =
                    wifiDirectApp.mP2pMan.initialize(this,
                            mWorkHandler.getLooper(), null);
            if (wifiDirectApp.mHomeActivity != null)
                wifiDirectApp.mHomeActivity.resetData();
            retryChannel = true;
        } else {
            if (wifiDirectApp.mHomeActivity != null)
                wifiDirectApp.mHomeActivity.onChannelDisconnected();
            stopSelf();
        }
    }

    /**
     * The callback of requestPeers upon
     * WIFI_P2P_PEERS_CHANGED_ACTION intent.
     */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.d(TAG, "onPeersAvailable");
        wifiDirectApp.mPeers.clear();
        wifiDirectApp.mPeers.addAll(peerList.getDeviceList());
        if (wifiDirectApp.mP2pInfo != null &&
                    wifiDirectApp.getConnectedPeer() != null &&
                    wifiDirectApp.mP2pInfo.groupFormed &&
                    wifiDirectApp.mP2pInfo.isGroupOwner) {
                wifiDirectApp.startSocketServer();
        }
        if (wifiDirectApp.mHomeActivity != null)
            wifiDirectApp.mHomeActivity.onPeersAvailable(peerList);
    }

    /**
     * the callback of when the _Requested_ connectino info is available.
     * WIFI_P2P_CONNECTION_CHANGED_ACTION intent, requestConnectionInfo()
     */
    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (info.groupFormed && !info.isGroupOwner && info.groupOwnerAddress != null)
            wifiDirectApp.startSocketClient(info.groupOwnerAddress.getHostAddress());
        wifiDirectApp.mP2pConnected = true;
        wifiDirectApp.mP2pInfo = info;
    }

    private void sendMessageToUpDatePeerListFragment() {
        if (wifiDirectApp.mHomeActivity != null)
            wifiDirectApp.mHomeActivity.onConnectionInfoAvailable(wifiDirectApp.mP2pInfo);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return new Binder();
    }

    public Handler getHandler() {
        return mHandler;
    }

    final class MessageHandler extends Handler {
        MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            processMessage(msg);
        }
    }

    private void processMessage(Message msg) {
        Log.d(TAG, "processMessage: message - " + msg.toString());
        switch (msg.what) {
            case MSG_NULL:
                break;
            case MSG_REGISTER_ACTIVITY:
                onActivityRegister((MainMenuActivity) msg.obj, msg.arg1);
                break;
            case MSG_STARTSERVER:
                if (mConnMan.startServerSelector() >= 0)
                    sendMessageToUpDatePeerListFragment();
                break;
            case MSG_STARTCLIENT:
                if (mConnMan.startClientSelector((String) msg.obj) >= 0)
                    sendMessageToUpDatePeerListFragment();
                break;
            case MSG_NEW_CLIENT:
                mConnMan.onNewClient((SocketChannel) msg.obj);
                break;
            case MSG_FINISH_CONNECT:
                mConnMan.onFinishConnect((SocketChannel) msg.obj);
                break;
            case MSG_PULLIN_DATA:
                onPullInData(msg.getData());
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
                Log.d(TAG, "received an error related to the connection manager");
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
        Log.d(TAG, "onActivityRegister: activity register " +
                "itself to service : " + register);
        if (register == 1)
            mActivity = activity;
        else
            mActivity = null;
    }

    /**
     * Handle data push out request.
     *  If the sender is the server, pub to all client.
     *  If the sender is client, only can send to the server.
     */
    private void onPushOutData(String data) {
        Log.d(TAG, "onPushOutData : " + data);
        mConnMan.pushOutData(data);
    }

    public static boolean sendMessage(int code, String message) {
        Message result = getInstance().getHandler().obtainMessage();
            result.what = code;
            result.obj = message;
        return getInstance().getHandler().sendMessage(result);
    }

    private String createQuizMessage(int code, String message) {
        return new Gson().toJson(new QuizMessage(code, message));
    }

    private void pushCardOut(String data){
        Log.d(TAG, "pushCardOut: " + data);
        mConnMan.pushOutData(createQuizMessage(MSG_SEND_CARD_ACTIVITY, data));
    }

    private void pushAllRulesOut(String data){
        Log.d(TAG, "pushAllRulesOut: " + data);
        mConnMan.pushOutData(createQuizMessage(MSG_SEND_RULES_ACTIVITY, data));
    }

    private void pushReadyOut(String data) {
        Log.d(TAG, "pushReadyOut: " + data);
        mConnMan.pushOutData(createQuizMessage(MSG_PLAYER_READY_ACTIVITY, data));
    }

    private void pushAnswerOut(String data) {
        Log.d(TAG, "pushAnswerOut: " + data);
        mConnMan.pushOutData(createQuizMessage(MSG_SEND_ANSWER_ACTIVITY, data));
    }

    private void pushConfirmationOut(String data) {
        Log.d(TAG, "pushConfirmationOut: " + data);
        Confirmation confirmation = new Gson().fromJson(data, Confirmation.class);
        mConnMan.publishDataToSingleClient(
                createQuizMessage(
                    MSG_ANSWER_CONFIRMATION_ACTIVITY,
                    String.valueOf(confirmation.getConfirmation())),
                confirmation.getClientAddress());
    }

    private void pushEndOfGameOut(String data) {
        Log.d(TAG, "pushEndOfGameOut: " + data);
        mConnMan.pushOutData(createQuizMessage(MSG_END_OF_GAME_ACTIVITY, data));
    }

    private void pushDisconnectAllPeersOut(String data) {
        Log.d(TAG, "pushDisconnectAllPeersOut: " + data);
        mConnMan.pushOutData(createQuizMessage(MSG_DISCONNECT_FROM_ALL_PEERS, data));
    }

    private String onPullInData(Bundle b){
        String data = b.getString("DATA");
        Log.d(TAG, "onPullInData: received message - " + data);
        Gson gson = new Gson();
        List<QuizMessage> messages = parseInData(data);
        for (QuizMessage msg: messages) {
            String message = msg.getMessage();
            switch(msg.getCode()){
                case MSG_SEND_RULES_ACTIVITY:
                    wifiDirectApp.mHomeActivity.startMultiplayerGamePlay(
                            gson.fromJson(message, Rules.class));
                    break;
                case MSG_SEND_CARD_ACTIVITY:
                    wifiDirectApp.mGameplayActivity.receivedNextCard(
                            gson.fromJson(message, Card.class));
                    break;
                case MSG_PLAYER_READY_ACTIVITY:
                    if (wifiDirectApp.mManageActivity != null)
                        wifiDirectApp.mManageActivity.deviceIsReady(message);
                    break;
                case MSG_SEND_ANSWER_ACTIVITY:
                    wifiDirectApp.mManageActivity.validateAnswer(
                            gson.fromJson(message, Answer.class));
                    break;
                case MSG_ANSWER_CONFIRMATION_ACTIVITY:
                    wifiDirectApp.mGameplayActivity.answerConfirmed(
                            Boolean.parseBoolean(message));
                    break;
                case MSG_END_OF_GAME_ACTIVITY:
                    wifiDirectApp.mGameplayActivity.endGamePlay(
                            Long.parseLong(message));
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

    public  List<QuizMessage> parseInData(String data) {
        Log.d(TAG, "parseInData: " + data);
        Gson gson = new Gson();
        ArrayList<QuizMessage> messages = new ArrayList<>();
        String[] chunks = data.split("\\}\\{");
        for (int i = 0; i < chunks.length; i++) {
            String chunk = chunks[i];
            if (i < chunks.length - 1)
                chunk += "}";
            if (i > 0)
                chunk = "{" + chunk;
            messages.add(gson.fromJson(chunk, QuizMessage.class));
        }
        return messages;
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
