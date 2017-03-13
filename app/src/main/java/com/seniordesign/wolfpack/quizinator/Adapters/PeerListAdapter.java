package com.seniordesign.wolfpack.quizinator.Adapters;

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
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;

import java.util.List;

import static android.graphics.Typeface.BOLD;

public class PeerListAdapter extends ArrayAdapter<WifiP2pDevice>{

    private int selectedIndex = ListView.NO_ID;
    private boolean isHost;
    private boolean connected;
    private int connectedIndex = ListView.NO_ID;
    private List<WifiP2pDevice> devices;

    public PeerListAdapter(Context context, int textViewResourceId,
                               List<WifiP2pDevice> devices, boolean isHost) {
        super(context, textViewResourceId, devices);
        this.devices = devices;
        this.isHost = isHost;
    }

    public void setSelectedIndex(int index){
        this.selectedIndex = index;
        notifyDataSetChanged();
    }

    public WifiP2pDevice getSelectedDevice() {
        return devices.get(selectedIndex);
    }

    public void addPeers(List<WifiP2pDevice> peers) {
        devices = peers;
        clear();
        addAll(devices);
        notifyDataSetChanged();
    }

    public void clearPeers() {
        devices.clear();
        clear();
        notifyDataSetChanged();
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
        if (connected)
            connectedIndex = selectedIndex;
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
        WifiP2pDevice peerDevice = devices.get(position);
        if (peerDevice != null) {
            TextView deviceNameTextView = (
                    TextView) v.findViewById(R.id.device_name);
            TextView deviceDetailsTextView =
                    (TextView) v.findViewById(R.id.device_details);
            deviceNameTextView.setText(peerDevice.deviceName);
            deviceDetailsTextView.setText(
                    ConnectionService.getDeviceStatus(peerDevice.status));
            LinearLayout buttons = (LinearLayout) v.findViewById(R.id.buttonsPanel);
            if(!isHost && position == selectedIndex){
                buttons.setVisibility(View.VISIBLE);
                Button connectButton = (Button) v.findViewById(R.id.btn_connect);
                Button disconnectButton = (Button) v.findViewById(R.id.btn_disconnect);
                if (connected && position == connectedIndex) {
                    //deviceNameTextView.setTypeface(null, BOLD);
                    //deviceDetailsTextView.setTypeface(null, BOLD);
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(true);
                }
            }else{
                buttons.setVisibility(View.GONE);
            }
        }
        return v;
    }
}
