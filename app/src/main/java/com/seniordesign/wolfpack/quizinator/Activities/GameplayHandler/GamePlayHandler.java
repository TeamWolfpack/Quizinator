package com.seniordesign.wolfpack.quizinator.Activities.GameplayHandler;

import com.seniordesign.wolfpack.quizinator.Activities.GamePlayActivity;

/**
 * Created by farrowc on 11/29/2016.
 */

public interface GamePlayHandler {

    public boolean handleInitialization(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    public long handleAnswerClicked(GamePlayActivity gamePlayActivity, GamePlayProperties properties, String answer);

    public boolean initializeDB(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    public long handleNextCard(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    public boolean handleCleanup(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    public boolean handleResume(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    public boolean handlePause(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    public boolean handleInitializeGameplay(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    public void onFragmentInteraction(GamePlayActivity gamePlayActivity, GamePlayProperties properties,String choice);

}
