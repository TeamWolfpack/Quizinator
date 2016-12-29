/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seniordesign.wolfpack.quizinator.WifiDirect;

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
        Log.d(TAG, "onActivityCreated: Start"); //TODO Remove later, for debug purposes
        super.onActivityCreated(savedInstanceState);
        mApp = (WifiDirectApp) getActivity().getApplication();
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach: Start"); //TODO Remove later, for debug purposes
        super.onAttach(activity);
        // onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart -> onResume
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Start"); //TODO Remove later, for debug purposes

        mContentView = inflater.inflate(R.layout.device_detail, null);

        // connect button, per
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreateView: Connect Button pressed"); //TODO Remove later, for debug purposes
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;

                // 15 is highest group owner (host)
                // 0 is lowest (player)
                config.groupOwnerIntent = mApp.isHost();  // least inclination to be group owner.
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
//                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
//                        "Connecting to :" + device.deviceAddress, true, true,  // cancellable
//                        new DialogInterface.OnCancelListener() {
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceListFragment.DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        });
                // perform p2p connect upon user click the connect button, connect available handle when connection done.
                ((DeviceListFragment.DeviceActionListener) getActivity()).connect(config);
            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onCreateView: Disconnect Button pressed"); //TODO Remove later, for debug purposes
                        ((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
                    }
                });

        // p2p connected, manager request connection info done, group owner elected. 
        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onCreateView: Send Message Button pressed"); //TODO Remove later, for debug purposes
//                        // Allow user to pick an image from Gallery or other registered apps
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        intent.setType("image/*");
//                        //startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                        //Log.d(TAG, "start_client button clicked, start chat activity !");
                        //((MainMenuActivit)getActivity()).startChatActivity("Hi Jimmy");  // no init msg if started from button click.

                        //Actually send the message
                        String message = "Hi Jimmy";
                        Log.d(TAG, "pushOutMessage : " + message);
                        Message msg = ConnectionService.getInstance().getHandler().obtainMessage();
                            msg.what = MSG_PUSHOUT_DATA;
                            msg.obj = message;
                        ConnectionService.getInstance().getHandler().sendMessage(msg);
                    }
                });
        return mContentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: Start"); //TODO Remove later, for debug purposes
        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.

//        Uri uri = data.getData();
//        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
//        statusText.setText("Sending: " + uri);
//        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
//        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
//        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
//        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());
//        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
//        getActivity().startService(serviceIntent);
    }

    /**
     * p2p connection setup, proceed to setup socket connection.
     */
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        Log.d(TAG, "onConnectionInfoAvailable: Start"); //TODO Remove later, for debug purposes
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        Log.d(TAG, "onConnectionInfoAvailable: " + info.groupOwnerAddress.getHostAddress());
        if (info.groupFormed && info.isGroupOwner) {
            //new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
            Log.d(TAG, "onConnectionInfoAvailable: device is groupOwner: startSocketServer done ");
        } else if (info.groupFormed) {
            Log.d(TAG, "onConnectionInfoAvailable: device is client, connect to group owner: startSocketClient done ");
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources().getString(R.string.client_text));
        }

        if (!mApp.mIsServer && mApp.mMyAddress == null) {
            Toast.makeText(mApp, "Connect to Server Failed, Please try again...", Toast.LENGTH_LONG).show();
        } else {
            // hide the connect button and enable start chat button
            mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        Log.d(TAG, "showDetail: Start"); //TODO Remove later, for debug purposes
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        Log.d(TAG, "resetViews: Start"); //TODO Remove later, for debug purposes
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }

    /*
     * leonardj
     *
     * 99% sure this code is from wifi-direct-demo and they forgot to delete it.
     */

//    /**
//     * A simple server socket that accepts connection and writes some data on
//     * the stream.
//     */
//    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {
//
//        private Context context;
//        private TextView statusText;
//
//        /**
//         * @param context
//         * @param statusText
//         */
//        public FileServerAsyncTask(Context context, View statusText) {
//            this.context = context;
//            this.statusText = (TextView) statusText;
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            try {
//                ServerSocket serverSocket = new ServerSocket(8988);
//
//                Socket client = serverSocket.accept();
//
//                final File f = new File(Environment.getExternalStorageDirectory() + "/"
//                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
//                        + ".jpg");
//
//                File dirs = new File(f.getParent());
//                if (!dirs.exists())
//                    dirs.mkdirs();
//                f.createNewFile();
//
//                InputStream inputstream = client.getInputStream();
//                copyFile(inputstream, new FileOutputStream(f));
//                serverSocket.close();
//                return f.getAbsolutePath();
//            } catch (IOException e) {
//                return null;
//            }
//        }
//
//        /*
//         * (non-Javadoc)
//         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
//         */
//        @Override
//        protected void onPostExecute(String result) {
//            if (result != null) {
//                statusText.setText("File copied - " + result);
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
//                context.startActivity(intent);
//            }
//
//        }
//
//        /*
//         * (non-Javadoc)
//         * @see android.os.AsyncTask#onPreExecute()
//         */
//        @Override
//        protected void onPreExecute() {
//            statusText.setText("Opening a server socket");
//        }
//
//    }
//
//    public static boolean copyFile(InputStream inputStream, OutputStream out) {
//        byte buf[] = new byte[1024];
//        int len;
//        try {
//            while ((len = inputStream.read(buf)) != -1) {
//                out.write(buf, 0, len);
//
//            }
//            out.close();
//            inputStream.close();
//        } catch (IOException e) {
//            return false;
//        }
//        return true;
//    }

}