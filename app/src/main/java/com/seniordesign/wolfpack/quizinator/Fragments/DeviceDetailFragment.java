package com.seniordesign.wolfpack.quizinator.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_PUSHOUT_DATA;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment {

    private static final String TAG = "PTP_Detail";

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;

    WifiDirectApp mApp = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mApp = (WifiDirectApp) getActivity().getApplication();
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
        // onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart -> onResume
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        mContentView = inflater.inflate(R.layout.device_detail, null);

        //TODO -> this is one of the buttons that will need to be altered during Sprint 4
        // connect button, per
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
            }
        });

        // p2p connected, manager request connection info done, group owner elected. 
//        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Actually send the message
//                String message = "Hi Jimmy";
//                Log.d(TAG, "pushOutMessage : " + message);
//                Message msg = ConnectionService.getInstance().getHandler().obtainMessage();
//                    msg.what = MSG_PUSHOUT_DATA;
//                    msg.obj = message;
//                ConnectionService.getInstance().getHandler().sendMessage(msg);
//            }
//        });
        return mContentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
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

        //TODO -> this chunk of code exists to display device information under the connect buttons, from here to here
//        // The owner IP is now known.
//        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
//        view.setText(getResources().getString(R.string.group_owner_text)
//                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
//                : getResources().getString(R.string.no)));
//
//        // InetAddress from WifiP2pInfo struct.
//        view = (TextView) mContentView.findViewById(R.id.device_info);
//        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());
        //TODO -> to here

        //TODO -> had to do with send message button, remove me from here
//        Log.d(TAG, "onConnectionInfoAvailable: " + info.groupOwnerAddress.getHostAddress());
//        if (info.groupFormed && info.isGroupOwner) {
//            //new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
//            Log.d(TAG, "onConnectionInfoAvailable: device is groupOwner: startSocketServer done ");
//        } else if (info.groupFormed) {
//            Log.d(TAG, "onConnectionInfoAvailable: device is client, connect to group owner: startSocketClient done ");
//            //((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources().getString(R.string.client_text));
//        }
        //TODO -> to here, assuming it doesn't break everything

        //TODO -> this block of code originally hid/showed the chat button and the connect button, from here
//        if (!mApp.mIsServer && mApp.mMyAddress == null) {
//            Toast.makeText(mApp, "Connect to Server Failed, Please try again...", Toast.LENGTH_LONG).show();
//        } else {
//            // hide the connect button and enable start chat button
//            mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
//            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
//        }
        //TODO -> to here
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    //TODO -> this whole method updates the detail
    public void showDetails(WifiP2pDevice device) {
        Log.d(TAG, "showDetail");
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
//        //TODO -> updates fragment with device information, from here
//        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
//        view.setText(device.deviceAddress);
//        view = (TextView) mContentView.findViewById(R.id.device_info);
//        view.setText(device.toString());
        //TODO -> to here
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        Log.d(TAG, "resetViews");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        //TODO -> remove these commented out block later, from here
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
//        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
//            view.setText(R.string.empty);
//            view = (TextView) mContentView.findViewById(R.id.device_info);
//            view.setText(R.string.empty);
//            view = (TextView) mContentView.findViewById(R.id.group_owner);
//            view.setText(R.string.empty);

//            view = (TextView) mContentView.findViewById(R.id.status_text);
//            view.setText(R.string.empty);
//        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        //TODO -> to here
        this.getView().setVisibility(View.GONE);
    }
}
