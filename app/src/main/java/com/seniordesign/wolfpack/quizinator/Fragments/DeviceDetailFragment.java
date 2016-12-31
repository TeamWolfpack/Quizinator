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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

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
}
