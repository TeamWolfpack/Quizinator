package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;

/*
 * The main menu activity
 * @creation 10/4/2016
 */
public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle); //TODO method deprecated, can get rid of after reviewed
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // If service not started yet, start it.
        Intent serviceIntent = new Intent(this, ConnectionService.class);
        startService(serviceIntent);  // start the connection service

        //Check if wifiDirect is supported
        if(!isWifiDirectSupported(this)){
            ((Button)findViewById(R.id.hostGameButton)).setTextColor(ContextCompat.getColor(this,R.color.colorGrayedOut));
            ((Button)findViewById(R.id.joinGameButton)).setTextColor(ContextCompat.getColor(this,R.color.colorGrayedOut));
        }
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
        else if(id == R.id.nav_P2P_compatibility_check) {
            if(isWifiDirectSupported(this)){
                Toast.makeText(this, "Device is compatible with P2P", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Device is not compatible with P2P", Toast.LENGTH_SHORT).show();
            }
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
     * @author leonardj (10/26/16)
     */
    public void initiateHostGame(View v) {
        if(isWifiDirectSupported(this)) {
            enableWifi();
            final Intent intent = new Intent(this, HostGameActivity.class);
            intent.putExtra("isServer", true);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Device is not compatible with P2P hardware and unable to host", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * @author leonardj (10/26/16)
     */
    public void initiateJoinGame(View v) {
        if(isWifiDirectSupported(this)) {
            enableWifi();
            final Intent intent = new Intent(this, HostGameActivity.class);
            intent.putExtra("isServer", false);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Device is not compatible with P2P hardware and unable to join", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToCardsList(View v) {
        startActivity(new Intent(this, CardsActivity.class));
    }

    private boolean isWifiDirectSupported(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        FeatureInfo[] features = pm.getSystemAvailableFeatures();
        for (FeatureInfo info : features) {
            if (info != null && info.name != null && info.name.equalsIgnoreCase("android.hardware.wifi.direct")) {
                return true;
            }
        }
        return false;
    }

    private void enableWifi(){
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);
            Toast.makeText(this, R.string.enabling_wifi, Toast.LENGTH_LONG).show();
        }
    }

}
