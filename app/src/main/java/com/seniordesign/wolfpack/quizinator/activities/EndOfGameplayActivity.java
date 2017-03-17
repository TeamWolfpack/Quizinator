package com.seniordesign.wolfpack.quizinator.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.database.GamePlayStats;
import com.seniordesign.wolfpack.quizinator.database.HighScores;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.R;

public class EndOfGameplayActivity extends AppCompatActivity {

    private QuizDataSource highScoresDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_gameplay);
        initializeDB();

        GamePlayStats gamePlayStats;
        if (getIntent().getExtras() != null) {
            gamePlayStats = getIntent().getExtras().getParcelable("gameStats");
        } else {
            gamePlayStats = new GamePlayStats();
            gamePlayStats.setScore(0);
            gamePlayStats.setTimeElapsed(0);
            gamePlayStats.setTotalCardsCompleted(0);
        }

        ((TextView)findViewById(R.id.endOfGameScoreText)).setText(
                String.valueOf("Score: " + gamePlayStats.getScore())
        );
        long endOfGameSeconds = (gamePlayStats.getTimeElapsed()/1000)%60;
        String formattedGameSeconds = endOfGameSeconds < 10 ? "0" + endOfGameSeconds : "" + endOfGameSeconds;
        ((TextView)findViewById(R.id.endOfGameTimeText)).setText(

                "Time: " + (gamePlayStats.getTimeElapsed()/60000) + ":" + formattedGameSeconds
        );
        ((TextView)findViewById(R.id.endOfGameTotalCardsText)).setText(
                String.valueOf("Total Cards: " + gamePlayStats.getTotalCardsCompleted())
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
        ((TextView)findViewById(R.id.endOfGameHighScoreText)).setText(
                String.valueOf("High Score: "+highScores.getBestScore()));
        ((TextView)findViewById(R.id.endOfGameHighScoreTimeText)).setText(
                "High Score Time: " + (highScores.getBestTime()/60000) + ":" + formattedHighScoreSeconds
        );
    }

    public void showMainMenu(View v){
        final Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean initializeDB(){
        int positiveDBConnections = 0;
        highScoresDataSource = new QuizDataSource(this);
        if(highScoresDataSource.open()){
            positiveDBConnections++;
        }
        return (positiveDBConnections == 1);
    }

    @Override
    protected void onResume(){
        super.onResume();
        highScoresDataSource.open();
    }

    @Override
    protected void onPause(){
        super.onPause();
        highScoresDataSource.close();
    }
}