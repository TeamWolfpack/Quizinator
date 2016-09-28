package com.seniordesign.wolfpack.quizinator.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.seniordesign.wolfpack.quizinator.R;

/**
 * The game play activity is...
 * @creation 09/28/2016
 */
public class GamePlayActivity extends AppCompatActivity {

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    public String onButtonClick(View view){
        return null;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    private boolean addToGameStatsDB(){
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
