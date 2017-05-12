package com.seniordesign.wolfpack.quizinator.activities;

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
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.wifiDirect.ConnectionService;

import static com.seniordesign.wolfpack.quizinator.Constants.NO_DECK_WARNING;
import static com.seniordesign.wolfpack.quizinator.Constants.NO_HIGHSCORES_WARNING;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private QuizDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Constants.MAIN_MENU);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // If service not started yet, start it.
        Intent serviceIntent = new Intent(this, ConnectionService.class);
        startService(serviceIntent);  // start the connection service

        //Init data source
        initializeDB();

        //Check if wifiDirect is supported
        if(!isWifiDirectSupported(this)){
            ((Button)findViewById(R.id.hostGameButton)).setTextColor(ContextCompat.getColor(this,R.color.colorGrayedOut));
            ((Button)findViewById(R.id.joinGameButton)).setTextColor(ContextCompat.getColor(this,R.color.colorGrayedOut));
        }
    }

    private boolean initializeDB(){
        dataSource = new QuizDataSource(this);
        return dataSource.open();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_quiz_bowl_rules) {
            Uri uriUrl = Uri.parse(Constants.NAQT_RULES_URL);
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
        else if(id == R.id.nav_show_decks) {
            startActivity(new Intent(this, DecksActivity.class));
        }
        else if(id == R.id.nav_show_cards){
            startActivity(new Intent(this, CardsActivity.class));
        }
        else if (id == R.id.help_singleplayer) {
            Uri uriUrl = Uri.parse(Constants.HELP_SINGLEPLAYER);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        }
        else if (id == R.id.help_multi_host) {
            Uri uriUrl = Uri.parse(Constants.HELP_MULTI_HOST);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        }
        else if (id == R.id.help_multi_player) {
            Uri uriUrl = Uri.parse(Constants.HELP_MULTI_PLAYER);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        }
        else if (id == R.id.help_custom) {
            Uri uriUrl = Uri.parse(Constants.HELP_CUSTOM);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        }
        else if (id == R.id.help_rules) {
            Uri uriUrl = Uri.parse(Constants.HELP_RULES);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showGameSettings(View v){
        if (dataSource.getAllDecks().isEmpty()) {
            Toast.makeText(this, NO_DECK_WARNING, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, NewGameSettingsActivity.class);
        intent.putExtra(Constants.MULTIPLAYER, false);
        startActivity(intent);
    }

    public void initiateHostGame(View v) {
        if (dataSource.getAllDecks().isEmpty()) {
            Toast.makeText(this, NO_DECK_WARNING, Toast.LENGTH_SHORT).show();
            return;
        }
        if(isWifiDirectSupported(this)) {
            enableWifi();
            final Intent intent = new Intent(this, HostGameActivity.class);
            intent.putExtra("isServer", true);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Device is not compatible with P2P hardware and unable to host", Toast.LENGTH_SHORT).show();
        }
    }

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

    public void initiateHighscores(View v) {
        if (dataSource.getAllHighScores().isEmpty()) {
            Toast.makeText(this, NO_HIGHSCORES_WARNING, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, HighscoresActivity.class);
        startActivity(intent);
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

    @Override
    protected void onResume(){
        super.onResume();
        dataSource.open();
    }

    @Override
    protected void onPause(){
        super.onPause();
        dataSource.close();
    }
}
