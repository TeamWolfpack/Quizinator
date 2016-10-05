package com.seniordesign.wolfpack.quizinator.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.seniordesign.wolfpack.quizinator.R;

/**
 * The new game settings activity is...
 * @creation 09/28/2016
 */
public class NewGameSettingsActivity extends AppCompatActivity {

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/4/2016)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_settings);
        setTitle("Game Settings");

        Spinner spinner = (Spinner) findViewById(R.id.card_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.card_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/4/2016)
     */
    public void startGame(View v){
        //Integer minutes = Integer.valueOf(((EditText)findViewById(R.id.game_minutes)).getText().toString());
        //Integer seconds = Integer.valueOf(((EditText)findViewById(R.id.game_seconds)).getText().toString());
        String cardType = ((Spinner)findViewById(R.id.card_type_spinner)).getSelectedItem().toString();

        final Intent startGameIntent = new Intent(this, GamePlayActivity.class);
        startActivity(startGameIntent);
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    public boolean loadRules(){
        return false;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    public boolean loadDeck(){
        return false;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    private boolean initializeDB(){
//        datasource = new ItemDataSource(this);
//        datasource.open();
        return true;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onResume(){
        super.onResume();
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onPause(){
        super.onPause();
    }
}
