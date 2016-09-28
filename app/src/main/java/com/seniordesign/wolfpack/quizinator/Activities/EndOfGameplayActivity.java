package com.seniordesign.wolfpack.quizinator.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.seniordesign.wolfpack.quizinator.R;

import java.sql.Time;

public class EndOfGameplayActivity extends AppCompatActivity {

//    private ItemDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_gameplay);
    }

    public String onClick(View view){
        return null;
    }

    private boolean initializeDB(){
//        datasource = new ItemDataSource(this);
//        datasource.open();
        return true;
    }

    private Time getGameDuration(){
        return null;
    }

    private int getPlayersScore(){
        return 0;
    }

    private int getCardsAnsweredRightTotal(){
        return 0;
    }

    private boolean updateHighscores(){
        return false;
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}
