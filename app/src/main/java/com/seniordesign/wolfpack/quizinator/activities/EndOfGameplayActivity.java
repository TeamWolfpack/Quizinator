package com.seniordesign.wolfpack.quizinator.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.database.GamePlayStats;
import com.seniordesign.wolfpack.quizinator.database.HighScores;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.wifiDirect.ConnectionService;

import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_DISCONNECT_FROM_ALL_PEERS;

public class EndOfGameplayActivity extends AppCompatActivity {

    private QuizDataSource highScoresDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_gameplay);
        setTitle(Constants.END_OF_GAMEPLAY);
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
                String.valueOf(gamePlayStats != null ? gamePlayStats.getScore() : 0)
        );
        long endOfGameSeconds = (gamePlayStats.getTimeElapsed()/1000)%60;
        String formattedGameSeconds = endOfGameSeconds < 10 ? "0" + endOfGameSeconds : "" + endOfGameSeconds;
        ((TextView)findViewById(R.id.endOfGameTimeText)).setText(
                (gamePlayStats.getTimeElapsed()/60000) + ":" + formattedGameSeconds
        );
        ((TextView)findViewById(R.id.endOfGameTotalCardsText)).setText(
                String.valueOf(gamePlayStats.getTotalCardsCompleted())
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
                String.valueOf(highScores.getBestScore()));
        ((TextView)findViewById(R.id.endOfGameHighScoreTimeText)).setText(
                (highScores.getBestTime()/60000) + ":" + formattedHighScoreSeconds
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

    @Override
    public void onBackPressed() {
        showMainMenu(this.getCurrentFocus());
    }
}
