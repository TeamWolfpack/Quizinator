package com.seniordesign.wolfpack.quizinator.GameplayHandler;

import com.seniordesign.wolfpack.quizinator.Activities.GamePlayActivity;

public interface GamePlayHandler {

    boolean handleInitialization(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    long handleAnswerClicked(GamePlayActivity gamePlayActivity, GamePlayProperties properties, String answer);

    boolean initializeDB(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    long handleNextCard(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    boolean handleCleanup(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    boolean handleResume(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    boolean handlePause(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    boolean handleDestroy(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    boolean handleInitializeGameplay(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    void onFragmentInteraction(GamePlayActivity gamePlayActivity, GamePlayProperties properties,String choice);

}
