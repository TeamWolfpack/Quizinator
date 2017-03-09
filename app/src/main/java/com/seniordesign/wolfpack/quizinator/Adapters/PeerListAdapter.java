package com.seniordesign.wolfpack.quizinator.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.wifiDirect.ConnectionService;

import java.util.List;

public class PeerListAdapter extends ArrayAdapter<WifiP2pDevice>{

    private int selectedIndex = ListView.NO_ID;
    private boolean isHost;
    private List<WifiP2pDevice> devices;

    public PeerListAdapter(Context context, int textViewResourceId,
                               List<WifiP2pDevice> devices) {
        super(context, textViewResourceId, devices);
        this.devices = devices;
    }

    public void setSelectedIndex(int index, boolean isHost){
        this.selectedIndex = index;
        this.isHost = isHost;
        // re-draw the list by informing the view of the changes
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
            if (deviceNameTextView != null) {
                deviceNameTextView.setText(peerDevice.deviceName);
            }
            if (deviceDetailsTextView != null) {
                deviceDetailsTextView.setText(
                        ConnectionService.getDeviceStatus(
                                peerDevice.status));
            }
            LinearLayout buttons =
                    (LinearLayout) v.findViewById(R.id.buttonsPanel);
            if(!isHost && position == selectedIndex){
                buttons.setVisibility(View.VISIBLE);
            }else{
                buttons.setVisibility(View.GONE);
            }
        }
        return v;
    }
}
