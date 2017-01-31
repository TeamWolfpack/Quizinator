package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Database.GamePlayStats;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScores;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScoresDataSource;
import com.seniordesign.wolfpack.quizinator.R;

import java.sql.Time;

public class EndOfGameplayActivity extends AppCompatActivity {

    private HighScoresDataSource highScoresDataSource;
    private GamePlayStats gamePlayStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_gameplay);
        initializeDB();

        if (getIntent().getExtras() != null) {
            gamePlayStats = getIntent().getExtras().getParcelable("gameStats");
        } else {
            gamePlayStats = new GamePlayStats();
            gamePlayStats.setScore(0);
            gamePlayStats.setTimeElapsed(0);
            gamePlayStats.setTotalCardsCompleted(0);
        }

        ((TextView)findViewById(R.id.endOfGameScoreText)).setText(
                "Score: " + gamePlayStats.getScore()
        );
        long endOfGameSeconds = (gamePlayStats.getTimeElapsed()/1000)%60;
        String formattedGameSeconds = endOfGameSeconds < 10 ? "0" + endOfGameSeconds : "" + endOfGameSeconds;
        ((TextView)findViewById(R.id.endOfGameTimeText)).setText(

                "Time: " + (gamePlayStats.getTimeElapsed()/60000) + ":" + formattedGameSeconds
        );
        ((TextView)findViewById(R.id.endOfGameTotalCardsText)).setText(
                "Total Cards: " + gamePlayStats.getTotalCardsCompleted()
        );

        HighScores highScores = new HighScores();
        if (highScoresDataSource.getAllHighScores().size() > 0) {
            highScores = highScoresDataSource.getAllHighScores().get(0);
        } else {
            highScores.setBestScore(0);
            highScores.setBestTime(0);
            highScores.setDeckName("");
        }
        long highScoreSeconds = (highScores.getBestTime()/1000)%60;
        String formattedHighScoreSeconds = highScoreSeconds < 10 ? "0" + highScoreSeconds : "" + highScoreSeconds;
        ((TextView)findViewById(R.id.endOfGameHighScoreText)).setText("High Score: "+highScores.getBestScore());
        ((TextView)findViewById(R.id.endOfGameHighScoreTimeText)).setText(
                "High Score Time: " + (highScores.getBestTime()/60000) + ":" + formattedHighScoreSeconds
        );
    }

    public void showMainMenu(View v){
        finish();
    }

    private boolean initializeDB(){
        int positiveDBConnections = 0;
        highScoresDataSource = new HighScoresDataSource(this);
        if(highScoresDataSource.open()){
            positiveDBConnections++;
        }
        return (positiveDBConnections == 1);
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onResume(){
        super.onResume();
        highScoresDataSource.open();
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onPause(){
        super.onPause();
        highScoresDataSource.close();
    }
}
