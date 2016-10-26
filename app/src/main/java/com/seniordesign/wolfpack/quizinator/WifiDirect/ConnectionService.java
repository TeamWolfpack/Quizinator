package com.seniordesign.wolfpack.quizinator.WifiDirect;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.*;

import java.nio.channels.SocketChannel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;

public class ConnectionService extends Service implements ChannelListener, PeerListListener, ConnectionInfoListener {  // callback of requestPeers{
	
	private static final String TAG = "PTP_Serv";
	
	private static ConnectionService _sinstance = null;

	private WorkHandler mWorkHandler;
    private  MessageHandler mHandler;
    
    boolean retryChannel = false;
    
    WifiDirectApp mApp;
    MainMenuActivity mActivity; // shall I use weak reference here ?
	ConnectionManager mConnMan;
	
	/**
     * @see Service#onCreate()
     */
    private void _initialize() {
    	if (_sinstance != null) {
            return;
        }

    	_sinstance = this;
    	mWorkHandler = new WorkHandler(TAG);
        mHandler = new MessageHandler(mWorkHandler.getLooper());
        
        mApp = (WifiDirectApp)getApplication();
        mApp.mP2pMan = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mApp.mP2pChannel = mApp.mP2pMan.initialize(this, mWorkHandler.getLooper(), null);
        
        mConnMan = new ConnectionManager(this);
    }
    
    public static ConnectionService getInstance(){
    	return _sinstance;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        _initialize();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	_initialize();
    	processIntent(intent);
    	return START_STICKY;
    }
    
    /**
     * process all wifi p2p intent caught by bcast recver.
     * P2P connection setup event sequence:
     * 1. after find, peers_changed to available, invited
     * 2. when connection established, this device changed to connected.
     * 3. for server, WIFI_P2P_CONNECTION_CHANGED_ACTION intent: p2p connected,
     *    for client, this device changed to connected first, then CONNECTION_CHANGED 
     * 4. WIFI_P2P_PEERS_CHANGED_ACTION: peer changed to connected.
     * 5. now both this device and peer are connected !
     * 
     * if select p2p server mode with create group, this device will be group owner automatically, with 
     * 1. this device changed to connected
     * 2. WIFI_P2P_CONNECTION_CHANGED_ACTION 
     */
    private void processIntent(Intent intent){
    	if( intent == null){
    		return;
    	}
    	
    	String action = intent.getAction();
    	
    	if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {  // this devices's wifi direct enabled state.
              int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
              if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                  // Wifi Direct mode is enabled
            	  mApp.mP2pChannel = mApp.mP2pMan.initialize(this, mWorkHandler.getLooper(), null);
            	  AppPreferences.setStringToPref(mApp, AppPreferences.PREF_NAME, AppPreferences.P2P_ENABLED, "1");
              } else {
            	  mApp.mThisDevice = null;  	// reset this device status
            	  mApp.mP2pChannel = null;
            	  mApp.mPeers.clear();
            	  if( mApp.mHomeActivity != null ){
            		  mApp.mHomeActivity.updateThisDevice(null);
            		  mApp.mHomeActivity.resetData();
            	  }
            	  AppPreferences.setStringToPref(mApp, AppPreferences.PREF_NAME, AppPreferences.P2P_ENABLED, "0");
              }
          } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {  
        	  // a list of peers are available after discovery, use PeerListListener to collect
              // request available peers from the wifi p2p manager. This is an
              // asynchronous call and the calling activity is notified with a
              // callback on PeerListListener.onPeersAvailable()
              if (mApp.mP2pMan != null) {
            	  mApp.mP2pMan.requestPeers(mApp.mP2pChannel, (PeerListListener) this);
              }
          } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
              if (mApp.mP2pMan == null) {
                  return;
              }

              NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
              if (networkInfo.isConnected()) {
            	  Log.d(TAG, "processIntent: WIFI_P2P_CONNECTION_CHANGED_ACTION: p2p connected ");
                  // Connected with the other device, request connection info for group owner IP. Callback inside details fragment.
                  mApp.mP2pMan.requestConnectionInfo(mApp.mP2pChannel, this);  
              } else {
            	  Log.d(TAG, "processIntent: WIFI_P2P_CONNECTION_CHANGED_ACTION: p2p disconnected, mP2pConnected = false..closeClient.."); // It's a disconnect
            	  mApp.mP2pConnected = false;
            	  mApp.mP2pInfo = null;   // reset connection info after connection done.
            	  mConnMan.closeClient();
            	  
            	  if( mApp.mHomeActivity != null ){
            		  mApp.mHomeActivity.resetData();
            	  }
              }
          } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {  
        	  // this device details has changed(name, connected, etc)
        	  mApp.mThisDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        	  mApp.mDeviceName = mApp.mThisDevice.deviceName;
        	  if( mApp.mHomeActivity != null ){
        		  mApp.mHomeActivity.updateThisDevice(mApp.mThisDevice);
        	  }
          }
    }
    
    /**
     * The channel to the framework Wifi P2p has been disconnected. could try re-initializing 
     */
    @Override
    public void onChannelDisconnected() {
    	if( !retryChannel ){
    		mApp.mP2pChannel = mApp.mP2pMan.initialize(this, mWorkHandler.getLooper(), null);
    		if( mApp.mHomeActivity != null) {
    			mApp.mHomeActivity.resetData();
    		}
    		retryChannel = true;
    	}else{
    		if( mApp.mHomeActivity != null) {
    			mApp.mHomeActivity.onChannelDisconnected();
    		}
    		stopSelf();
    	}
    }
    
    /**
     * the callback of requestPeers upon WIFI_P2P_PEERS_CHANGED_ACTION intent.
     */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
    	mApp.mPeers.clear();
    	mApp.mPeers.addAll(peerList.getDeviceList());
		
    	WifiP2pDevice connectedPeer = mApp.getConnectedPeer();
    	if( connectedPeer != null ){
    		//PTPLog.d(TAG, "onPeersAvailable : exist connected peer : " + connectedPeer.deviceName);
    	} else {
    		
    	}
    	
    	if(mApp.mP2pInfo != null && connectedPeer != null ){
    		if( mApp.mP2pInfo.groupFormed && mApp.mP2pInfo.isGroupOwner ){
    			mApp.startSocketServer();
    		}else if( mApp.mP2pInfo.groupFormed && connectedPeer != null ){
    			// XXX client path goes to connection info available after connection established.
    			// PTPLog.d(TAG, "onConnectionInfoAvailable: device is client, connect to group owner: startSocketClient ");
    			// mApp.startSocketClient(mApp.mP2pInfo.groupOwnerAddress.getHostAddress());
    		}
    	}
    	
    	if( mApp.mHomeActivity != null){
    		mApp.mHomeActivity.onPeersAvailable(peerList);
    	}
    }
    
    /**
     * the callback of when the _Requested_ connectino info is available. 
     * WIFI_P2P_CONNECTION_CHANGED_ACTION intent, requestConnectionInfo()
     */
    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
    	Log.d(TAG, "onConnectionInfoAvailable: " + info.groupOwnerAddress.getHostAddress());
        if (info.groupFormed && info.isGroupOwner ) {
			// XXX server path goes to peer connected.
            //new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
        	//PTPLog.d(TAG, "onConnectionInfoAvailable: device is groupOwner: startSocketServer ");
        	// mApp.startSocketServer();
        } else if (info.groupFormed) {
        	mApp.startSocketClient(info.groupOwnerAddress.getHostAddress());
        }
        mApp.mP2pConnected = true;
        mApp.mP2pInfo = info;   // connection info available
    }
    
    private void enableStartChatActivity() {
    	if( mApp.mHomeActivity != null ){
			mApp.mHomeActivity.onConnectionInfoAvailable(mApp.mP2pInfo);
    	}
    }
    
	@Override
	public IBinder onBind(Intent arg0) { return null; }
	
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
     * the main message process loop.
     */
    private void processMessage(Message msg) {
    	
        switch (msg.what) {
        case MSG_NULL:
        	break;
        case MSG_REGISTER_ACTIVITY:
        	onActivityRegister((MainMenuActivity) msg.obj, msg.arg1);
        	break;
        case MSG_STARTSERVER:
        	if( mConnMan.startServerSelector() >= 0){
        		enableStartChatActivity();
        	}
        	break;
        case MSG_STARTCLIENT:
        	if( mConnMan.startClientSelector((String)msg.obj) >= 0){
        		enableStartChatActivity();
        	}
        	break;
        case MSG_NEW_CLIENT:
        	mConnMan.onNewClient((SocketChannel)msg.obj);
        	break;
        case MSG_FINISH_CONNECT:
        	mConnMan.onFinishConnect((SocketChannel)msg.obj);
        	break;
        case MSG_PULLIN_DATA:
        	//onPullInData((SocketChannel)msg.obj, msg.getData());
        	break;
        case MSG_PUSHOUT_DATA:
        	onPushOutData((String)msg.obj);
        	break;
        case MSG_SELECT_ERROR:
        	mConnMan.onSelectorError();
        	break;
        case MSG_BROKEN_CONN:
        	mConnMan.onBrokenConn((SocketChannel)msg.obj);
        	break;
        default:
        	break;
        }
    }
    
    /**
     * register the activity that uses this service.
     */
    private void onActivityRegister(MainMenuActivity activity, int register){
    	Log.d(TAG, "onActivityRegister : activity register itself to service : " + register);
    	if( register == 1){
    		mActivity = activity;
    	}else{
    		mActivity = null;    // set to null explicitly to avoid mem leak.
    	}
    }
    
    /**
     * handle data push out request. 
     * If the sender is the server, pub to all client.
     * If the sender is client, only can send to the server.
     */
    private void onPushOutData(String data){
    	Log.d(TAG, "onPushOutData : " + data);
    	mConnMan.pushOutData(data);
    }
    
    /**
     * sync call to send data using conn man's channel, as conn man now is blocking on select
     */
    public int connectionSendData(String jsonstring) {
    	Log.d(TAG, "connectionSendData : " + jsonstring);
    	new SendDataAsyncTask(mConnMan, jsonstring).execute();
    	return 0;
    }
    
    /**
     * write data in an async task to avoid NetworkOnMainThreadException.
     */
    public class SendDataAsyncTask extends AsyncTask<Void, Void, Integer> {
    	private String data;
    	private ConnectionManager connman;
    	
    	public SendDataAsyncTask(ConnectionManager conn, String jsonstring) {
    		connman = conn;
    		data = jsonstring;
    	}
    	
		@Override
		protected Integer doInBackground(Void... params) {
			return connman.pushOutData(data);
		}
		 
		@Override
		protected void onPostExecute(Integer result) {
			Log.d(TAG, "SendDataAsyncTask : onPostExecute:  " + data + " len: " + result);
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
