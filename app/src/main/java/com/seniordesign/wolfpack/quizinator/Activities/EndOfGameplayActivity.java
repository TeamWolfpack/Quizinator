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

/**
 * The end of game play activity is...
 * @creation 09/28/2016
 */
public class EndOfGameplayActivity extends AppCompatActivity {

    private HighScoresDataSource highScoresDataSource;
    private GamePlayStats gamePlayStats;

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_gameplay);
        initializeDB();
        gamePlayStats = getIntent().getExtras().getParcelable("gameStats");
        HighScores highScores = highScoresDataSource.getAllHighScores().get(0);
        ((TextView)findViewById(R.id.endOfGameHighScoreText)).setText("High Score: "+highScores.getBestScore());
        ((TextView)findViewById(R.id.endOfGameHighScoreTimeText)).setText(
                "High Score Time: " + (highScores.getBestTime()/1000000000)/60 + ":" + (highScores.getBestTime()/1000000000)%60
        );
    }

    public void showMainMenu(View v){
        final Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
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
    private boolean initializeDB(){
        int positiveDBConnections = 0;
        highScoresDataSource = new HighScoresDataSource(this);
        if(highScoresDataSource.open()){
            positiveDBConnections++;
        }
//        datasource = new ItemDataSource(this);
//        datasource.open();
        return (positiveDBConnections == 1);
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    private Time getGameDuration(){
        return null;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    private int getPlayersScore(){
        return 0;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    private int getCardsAnsweredRightTotal(){
        return 0;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    private boolean updateHighscores(){
        return false;
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
