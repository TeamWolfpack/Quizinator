package com.seniordesign.wolfpack.quizinator.Fragments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.PeerListAdapter;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

/**
 * A ListFragment that displays available peerDevicesList on discovery
 * and requests the parent activity to handle user interaction events.
 */
public class PeerListFragment extends ListFragment {

    private static final String TAG = "PTP_ListFrag";

    private List<WifiP2pDevice> peerDevicesList = new ArrayList<>();

    private ProgressDialog progressDialog = null;
    private View mContentView = null;
    private WifiP2pDevice myDevice;
    WifiP2pInfo wifiP2pInfo;

    private WifiDirectApp wifiDirectApp = null;
    private PeerListAdapter peerListAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        //initialize peer list adapter
        peerListAdapter = new PeerListAdapter(getActivity(),
                R.layout.row_devices, peerDevicesList);
        this.setListAdapter(peerListAdapter);
        //find application reference
        wifiDirectApp = (WifiDirectApp) getActivity().getApplication();
        onPeersAvailable(wifiDirectApp.mPeers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mContentView = inflater.inflate(R.layout.device_list, container);
        return mContentView;
    }

    /**
     * Initiate a connection with the peer.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick");
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        ((DeviceActionListener) getActivity()).showDetails(device);
        peerListAdapter.setSelectedIndex(position, wifiDirectApp.mIsServer);
    }

    public void onConnectButtonClicked(){
        WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = myDevice.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            // least inclination to be group owner.
                // 15 is highest group owner (host)
                // 0 is lowest (player)
            config.groupOwnerIntent = wifiDirectApp.isHost();
        dismissProgressDialog();
        // perform p2p connect upon user click the connect button,
        // connect available handle when connection done.
        ((PeerListFragment.DeviceActionListener) getActivity()).connect(config);
    }

    public void onDisconnectButtonClicked(){
        ((PeerListFragment.DeviceActionListener) getActivity()).disconnect();
    }

    /**
     * Update UI for this myDevice.
     */
    public void updateThisDevice(WifiP2pDevice myDevice) {
        Log.d(TAG, "updateThisDevice");
        TextView deviceNameTextView =
                (TextView) mContentView.findViewById(R.id.my_name);
        TextView deviceDetailsTextView =
                (TextView) mContentView.findViewById(R.id.my_status);
        if (myDevice != null) {
            this.myDevice = myDevice;
            deviceNameTextView.setText(myDevice.deviceName);
            deviceDetailsTextView.setText(
                    ConnectionService.getDeviceStatus(myDevice.status));
        } else if (this.myDevice != null) {
            deviceNameTextView.setText(this.myDevice.deviceName);
            deviceDetailsTextView.setText(R.string.wifi_direct_disabled);
        }
    }

    /**
     * the callback defined in PeerListListener to get the async result
     * from WifiP2pManager.requestPeers(channel, PeerListListener);
     */
    public void onPeersAvailable(List<WifiP2pDevice> peerList) {
        Log.d(TAG, "onPeersAvailable");
        dismissProgressDialog();
        peerDevicesList.clear();
        List<WifiP2pDevice> toRemove = new ArrayList<>();
        //filter peers based on if the device is the host
        if (wifiDirectApp.mIsServer) {
            Log.d(TAG, "onPeersAvailable: wifiDirectApp.mServer is true (HOST)");
            for (WifiP2pDevice device : peerList) {
                if (device.isGroupOwner())
                    toRemove.add(device);
            }
        } else {
            Log.d(TAG, "onPeersAvailable: wifiDirectApp.mServer is false (CLIENT)");
            for (WifiP2pDevice device : peerList) {
                if (!device.isGroupOwner())
                    toRemove.add(device);
            }
        }
        //concurrent modification exception thrown if the user joins and leaves quickly
        //use of iterator instead of collection.removeAll resolves threading problem
        Iterator<WifiP2pDevice> iteratorOfPeersToRemove = toRemove.iterator();
        while (iteratorOfPeersToRemove.hasNext()) {
            WifiP2pDevice deviceToRemove = iteratorOfPeersToRemove.next();
            if (peerList.contains(deviceToRemove))
                peerList.remove(deviceToRemove);
        }
        peerDevicesList.addAll(peerList);
        ((PeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public void clearPeers() {
        Log.d(TAG, "clearPeers");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                peerDevicesList.clear();
                ((PeerListAdapter) getListAdapter()).notifyDataSetChanged();
            }
        });
    }

    public void onInitiateDiscovery() {
        Log.d(TAG, "onInitiateDiscovery");
        dismissProgressDialog();
        progressDialog = ProgressDialog.show(getActivity(),
                "Press back to cancel", "finding peerDevicesList",
                true, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
    }

    /**
     * p2p connection setup, proceed to setup socket connection.
     */
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        Log.d(TAG, "onConnectionInfoAvailable");
        dismissProgressDialog();
        this.wifiP2pInfo = info;
    }

    /**
     * Updates the UI with myDevice data
     */
    public void showDetails(WifiP2pDevice device) {
        Log.d(TAG, "showDetail");
        this.myDevice = device;
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable
     * operation.
     */
    public void resetViews() {
        Log.d(TAG, "resetViews");
        dismissProgressDialog();
    }

    /**
     * Hides the progress dialog which is used to ask the user to
     * accept connections from another peer.
     */
    private boolean dismissProgressDialog(){
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            return true;
        }
        return false;
    }

    /**
     * An interface-callback for the activity to listen to fragment
     * interaction events.
     */
    public interface DeviceActionListener {
        void showDetails(WifiP2pDevice device);
//        void cancelDisconnect();
        void connect(WifiP2pConfig config);
        void disconnect();
    }
}
