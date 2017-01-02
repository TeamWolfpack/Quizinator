package com.seniordesign.wolfpack.quizinator.Fragments;

import java.util.ArrayList;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.PeerListAdapter;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events
 */
public class DeviceListFragment extends ListFragment {

    private static final String TAG = "PTP_ListFrag";

    private List<WifiP2pDevice> peers = new ArrayList<>();
    private ProgressDialog progressDialog = null;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;

    private WifiDirectApp mApp = null;
    private PeerListAdapter peerListAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        //initialize peer list adapter
        peerListAdapter = new PeerListAdapter(getActivity(), R.layout.row_devices, peers);
        this.setListAdapter(peerListAdapter);

        mApp = (WifiDirectApp) getActivity().getApplication();
        onPeersAvailable(mApp.mPeers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mContentView = inflater.inflate(R.layout.device_list, null);
        return mContentView;
    }

    public void onConnectButtonClicked(){
        WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            // 15 is highest group owner (host)
            // 0 is lowest (player)
            config.groupOwnerIntent = mApp.isHost();  // least inclination to be group owner.
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        // perform p2p connect upon user click the connect button, connect available handle when connection done.
        ((DeviceListFragment.DeviceActionListener) getActivity()).connect(config);
    }

    public void onDisconnectButtonClicked(){
        ((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
    }

    /**
     * @return this device
     */
    public WifiP2pDevice getDevice() {
        return device;
    }

    /**
     * Initiate a connection with the peer.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick");
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        ((DeviceActionListener) getActivity()).showDetails(device);
        peerListAdapter.setSelectedIndex(position);
    }

    /**
     * Update UI for this device.
     *
     * @param device WifiP2pDevice object
     */
    //TODO -> move to peer adapter if possible
    public void updateThisDevice(WifiP2pDevice device) { // callback of this device details changed bcast event.
        Log.d(TAG, "updateThisDevice");
        TextView nameview = (TextView) mContentView.findViewById(R.id.my_name);
        TextView statusview = (TextView) mContentView.findViewById(R.id.my_status);
        if (device != null) {
            this.device = device;
            nameview.setText(device.deviceName);
            statusview.setText(ConnectionService.getDeviceStatus(device.status));
        } else if (this.device != null) {
            nameview.setText(this.device.deviceName);
            //TODO -> move this string to the Android strings for this app
            statusview.setText("WiFi Direct Disabled, please re-enable.");
        }
    }

    /**
     * the callback defined in PeerListListener to get the async result
     * from WifiP2pManager.requestPeers(channel, PeerListListener);
     */
    public void onPeersAvailable(List<WifiP2pDevice> peerList) {   // the callback to collect peer list after discover.
        Log.d(TAG, "onPeersAvailable");
        if (progressDialog != null && progressDialog.isShowing()) {  // dismiss progressbar first.
            progressDialog.dismiss();
        }
        peers.clear();

        ArrayList<WifiP2pDevice> toRemove = new ArrayList();
        if (mApp.mIsServer) {
            Log.d(TAG, "onPeersAvailable: mApp.mServer is true (HOST)");
            for (WifiP2pDevice device : peerList) {
                if (device.isGroupOwner())
                    toRemove.add(device);
            }
        } else {
            Log.d(TAG, "onPeersAvailable: mApp.mServer is false (CLIENT)");
            for (WifiP2pDevice device : peerList) {
                if (!device.isGroupOwner())
                    toRemove.add(device);
            }
        }
        peerList.removeAll(toRemove);

        peers.addAll(peerList);
        //TODO -> fix whatever this is
        ((PeerListAdapter) getListAdapter()).notifyDataSetChanged();

        if (peers.size() == 0) {
            return;
        }
    }

    public void clearPeers() {
        Log.d(TAG, "clearPeers");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                peers.clear();
                //TODO -> fix whatever this is
                ((PeerListAdapter) getListAdapter()).notifyDataSetChanged();
            }
        });

    }

    public void onInitiateDiscovery() {
        Log.d(TAG, "onInitiateDiscovery");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true,
            true, new DialogInterface.OnCancelListener() {
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
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        Log.d(TAG, "showDetail");
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        Log.d(TAG, "resetViews");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        this.getView().setVisibility(View.GONE);
    }

    /**
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    public interface DeviceActionListener {

        void showDetails(WifiP2pDevice device);

        void cancelDisconnect();

        void connect(WifiP2pConfig config);

        void disconnect();
    }

}
