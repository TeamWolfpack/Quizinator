package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.AnalyticsUtils;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.DeviceDetailFragment;
import com.seniordesign.wolfpack.quizinator.WifiDirect.DeviceListFragment;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.ACT_CREATE;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.CAT_LOCATION;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.LAB_HOME;

/*
 * The main menu activity
 * @creation 10/4/2016
 */
public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DeviceListFragment.DeviceActionListener {

    private WifiDirectApp mApp;

    /*
     * @author farrowc (10/4/2016)
     * @author chuna (10/23/2016)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Main Menu");
        setSupportActionBar(toolbar);

        mApp = (WifiDirectApp)getApplication();
        mApp.mHomeActivity = this;

        // If service not started yet, start it.
        Intent serviceIntent = new Intent(this, ConnectionService.class);
        startService(serviceIntent);  // start the connection service

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle); //TODO method deprecated, can get rid of after reviewed
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /*
     * @author farrowc 10/4/2016
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
     * @author farrowc 10/4/2016
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    /*
     * @author farrowc 10/4/2016
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * @author farrowc 10/4/2016
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_quiz_bowl_rules) {
            Uri uriUrl = Uri.parse("https://www.naqt.com/rules.html");
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        }
        /*
        else if (id == R.id.nav_application_settings) {
            // For later sprints
        }
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
     * @author farrowc 10/4/2016
     */
    public void showGameSettings(View v){
        final Intent intent = new Intent(this, NewGameSettingsActivity.class);
        startActivity(intent);
    }

    /*
     * @author leonardj (10/24/16)
     */
    public void initiateDiscovery(View v){
//        if( !mApp.isP2pEnabled() ){
//            Toast.makeText(this, R.string.p2p_off_warning, Toast.LENGTH_LONG).show();
//            return;
//        }

        final DeviceListFragment fragment = (DeviceListFragment)getFragmentManager().findFragmentById(R.id.frag_list);
        fragment.onInitiateDiscovery();

        AnalyticsUtils.getInstance(mApp).trackEvent(CAT_LOCATION, ACT_CREATE, LAB_HOME, 2);
        mApp.mP2pMan.discoverPeers(mApp.mP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainMenuActivity.this, "Discovery Initiated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                fragment.clearPeers();
                Toast.makeText(MainMenuActivity.this, "Discovery Failed, try again... ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * process WIFI_P2P_THIS_DEVICE_CHANGED_ACTION intent, refresh this device.
     */
    public void updateThisDevice(final WifiP2pDevice device){
        runOnUiThread(new Runnable() {
            @Override public void run() {
                DeviceListFragment fragment = (DeviceListFragment)getFragmentManager().findFragmentById(R.id.frag_list);
                fragment.updateThisDevice(device);
            }
        });
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
                DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
                if (fragmentList != null) {
                    fragmentList.clearPeers();
                }
                if (fragmentDetails != null) {
                    fragmentDetails.resetViews();
                }
            }
        });
    }

    /**
     * handle p2p connection available, update UI.
     */
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
                fragmentDetails.onConnectionInfoAvailable(info);
            }
        });
    }

    /**
     * update the device list fragment.
     */
    public void onPeersAvailable(final WifiP2pDeviceList peerList){
        runOnUiThread(new Runnable() {
            @Override public void run() {
                DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
                fragmentList.onPeersAvailable(mApp.mPeers);  // use application cached list.
                DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);

                for(WifiP2pDevice d : peerList.getDeviceList()){
                    if( d.status == WifiP2pDevice.FAILED ){
                        fragmentDetails.resetViews();
                    }
                }
            }
        });
    }

    /**
     * The channel to the framework(WiFi direct) has been disconnected.
     * This is diff than the p2p connection to group owner.
     */
    public void onChannelDisconnected() {
        Toast.makeText(this, "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("WiFi Direct down, please re-enable WiFi Direct")
                .setCancelable(true)
                .setPositiveButton("Re-enable WiFi Direct", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                })
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog info = builder.create();
        info.show();
    }

    /**
     * user taps on peer from discovered list of peers, show this peer's detail.
     */
    @Override
    public void showDetails(WifiP2pDevice device) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);
    }

    @Override
    public void cancelDisconnect() {
        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (mApp.mP2pMan != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE || fragment.getDevice().status == WifiP2pDevice.INVITED) {
                mApp.mP2pMan.cancelConnect(mApp.mP2pChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainMenuActivity.this, "Aborting connection", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(MainMenuActivity.this, "cancelConnect: request failed. Please try again.. ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /**
     * user clicked connect button after discover peers.
     */
    @Override
    public void connect(WifiP2pConfig config) {
        // perform p2p connect upon users click the connect button. after connection, manager request connection info.
        mApp.mP2pMan.connect(mApp.mP2pChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                Toast.makeText(MainMenuActivity.this, "Connect success..", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainMenuActivity.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        fragment.resetViews();
        mApp.mP2pMan.removeGroup(mApp.mP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(MainMenuActivity.this, "disconnect failed.." + reasonCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                fragment.getView().setVisibility(View.GONE);
            }
        });
    }
}
