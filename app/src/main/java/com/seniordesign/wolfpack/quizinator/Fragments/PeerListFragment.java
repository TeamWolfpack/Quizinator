package com.seniordesign.wolfpack.quizinator.fragments;

import java.util.List;

import android.app.ListFragment;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.adapters.PeerListAdapter;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.wifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.wifiDirect.WifiDirectApp;

/**
 * A ListFragment that displays available peerDevicesList on discovery
 * and requests the parent activity to handle user interaction events.
 */
public class PeerListFragment extends ListFragment {

    private static final String TAG = "PTP_ListFrag";

    private View mContentView;
    private WifiP2pDevice myDevice;
    private PeerListAdapter peerListAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        WifiDirectApp wifiDirectApp = (WifiDirectApp) getActivity().getApplication();

        //initialize peer list adapter
        peerListAdapter = new PeerListAdapter(getActivity(), R.layout.row_devices,
                wifiDirectApp.getFilteredPeerList(), wifiDirectApp.mIsServer);
        setListAdapter(peerListAdapter);
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
        peerListAdapter.setSelectedIndex(position);
    }

    public void setConnected(boolean connected) {
        if (peerListAdapter != null)
            peerListAdapter.setConnected(connected);
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
        } else {
            deviceNameTextView.setText(R.string.empty);
            deviceDetailsTextView.setText(R.string.wifi_direct_disabled);
        }
    }

    public WifiP2pDevice getSelectedDevice(){
        return peerListAdapter.getSelectedDevice();
    }

    /**
     * the callback defined in PeerListListener to get the async result
     * from WifiP2pManager.requestPeers(channel, PeerListListener);
     */
    public void onPeersAvailable(List<WifiP2pDevice> peerList) {
        Log.d(TAG, "onPeersAvailable " + peerList.size());
        peerListAdapter.addPeers(peerList);
    }

    public void clearPeers() {
        Log.d(TAG, "clearPeers");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                peerListAdapter.clearPeers();
            }
        });
    }

    /**
     * An interface-callback for the activity to listen to fragment
     * interaction events.
     */
    public interface DeviceActionListener {
        void connect(WifiP2pConfig config);
        void disconnect();
    }
}
