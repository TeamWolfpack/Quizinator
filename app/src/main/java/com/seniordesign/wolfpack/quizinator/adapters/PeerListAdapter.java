package com.seniordesign.wolfpack.quizinator.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.wifiDirect.ConnectionService;

import java.util.List;

public class PeerListAdapter extends ArrayAdapter<WifiP2pDevice>{

    private int selectedIndex = ListView.NO_ID;
    private boolean isHost;
    private boolean connected;
    private int connectedIndex = ListView.NO_ID;

    public PeerListAdapter(Context context, int textViewResourceId,
                           List<WifiP2pDevice> devices, boolean isHost) {
        super(context, textViewResourceId, devices);
        this.isHost = isHost;
    }

    public void setSelectedIndex(int index){
        this.selectedIndex = index;
        notifyDataSetChanged();
    }

    public WifiP2pDevice getSelectedDevice() {
        return getItem(selectedIndex);
    }

    public void addPeers(List<WifiP2pDevice> peers) {
        clear();
        addAll(peers);
        if (!isHost && connected && !peers.isEmpty() &&
                peers.get(0).status == WifiP2pDevice.CONNECTED) {
            selectedIndex = 0;
            connectedIndex = 0;
        }
        notifyDataSetChanged();
    }

    public void clearPeers() {
        clear();
        notifyDataSetChanged();
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
        if (connected) {
            connectedIndex = selectedIndex;
        } else {
            connectedIndex = ListView.NO_ID;
            selectedIndex = ListView.NO_ID;
        }
        notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position,
                        View convertView,
                        @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row_devices, null);
        }
        WifiP2pDevice peerDevice = getItem(position);
        if (peerDevice != null) {
            TextView deviceNameTextView = (
                    TextView) v.findViewById(R.id.device_name);
            TextView deviceDetailsTextView =
                    (TextView) v.findViewById(R.id.device_details);
            deviceNameTextView.setText(peerDevice.deviceName);
            deviceDetailsTextView.setText(
                    ConnectionService.getDeviceStatus(peerDevice.status));
            handleButtonsPanel(v, position);
        }
        return v;
    }

    private void handleButtonsPanel(View v, int position){
        LinearLayout buttons = (LinearLayout) v.findViewById(R.id.buttonsPanel);
        Button connectButton = (Button) v.findViewById(R.id.btn_connect);
        Button disconnectButton = (Button) v.findViewById(R.id.btn_disconnect);

        //Always show connected Host buttons
        if (connected) {
            if(position == connectedIndex) {
                buttons.setVisibility(View.VISIBLE);
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
            } else {
                buttons.setVisibility(View.GONE);
            }
        } else {
            if(!isHost && position == selectedIndex){
                buttons.setVisibility(View.VISIBLE);
                if(connected && position == connectedIndex){
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(true);
                }else if(connected){
                    buttons.setVisibility(View.GONE);
                }
            }else{
                buttons.setVisibility(View.GONE);
            }
        }
    }
}
