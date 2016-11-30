package com.seniordesign.wolfpack.quizinator.GameplayHandler;

import com.seniordesign.wolfpack.quizinator.Activities.GamePlayActivity;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScoresDataSource;

/**
 * Created by farrowc on 11/29/2016.
 */

public class MultiplayerHandler implements GamePlayHandler {
    @Override
    public boolean handleInitialization(GamePlayActivity gamePlayActivity) {

        initializeDB(gamePlayActivity);
        initializeGamePlay();

        return true;
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    private boolean initializeDB(GamePlayActivity gamePlayActivity) {
        gamePlayActivity.highScoresDataSource = new HighScoresDataSource(this);
        return highScoresDataSource.open();
    }
}
