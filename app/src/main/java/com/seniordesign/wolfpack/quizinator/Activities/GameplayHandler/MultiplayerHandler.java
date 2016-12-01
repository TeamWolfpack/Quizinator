package com.seniordesign.wolfpack.quizinator.Activities.GameplayHandler;

import android.view.View;
import android.widget.Button;

import com.seniordesign.wolfpack.quizinator.Activities.GamePlayActivity;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScoresDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.Answer;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_PLAYER_READY_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_SEND_ANSWER_ACTIVITY;

/**
 * Created by farrowc on 11/29/2016.
 */

public class MultiplayerHandler implements GamePlayHandler {

    @Override
    public boolean handleInitialization(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        //TODO This is temporary, the game timer will be fixed later
        gamePlayActivity.findViewById(R.id.gamePlayTimeText).setVisibility(View.INVISIBLE);

        initializeDB(gamePlayActivity,properties);

        properties.setWifiDirectApp((WifiDirectApp)(gamePlayActivity.getApplication()));
        properties.getWifiDirectApp().mGameplayActivity = gamePlayActivity;

        //Log.d(TAG, gamePlayActivity.getIntent().getExtras().getString("Rules")); //TODO

        //load rules passed in by extra
        properties.setRules(properties.getGson().fromJson(gamePlayActivity.getIntent().getExtras().getString("Rules"), Rules.class));

        //Log.d(TAG, properties.getRules().toString()); //TODO

        //send message that player is ready to start
        ConnectionService.sendMessage(MSG_PLAYER_READY_ACTIVITY, properties.getWifiDirectApp().mDeviceName);

        properties.setHasAnswered(false);

        return true;
    }

    @Override
    public long handleAnswerClicked(GamePlayActivity gamePlayActivity, GamePlayProperties properties, String answer) {
        if(properties.getHasAnswered())
            return 0;
        properties.setHasAnswered(true);
        properties.getCardTimerAreaBackgroundRunning().cancel();
        onFragmentInteraction(gamePlayActivity, properties, answer);
        return 0;
    }

    @Override
    public boolean initializeDB(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        properties.setHighScoresDataSource(new HighScoresDataSource(gamePlayActivity));
        return properties.getHighScoresDataSource().open();
    }

    @Override
    public long handleNextCard(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        properties.setHasAnswered(false);
        gamePlayActivity.showCard(properties.getCurrentCard());
        properties.setCardsPlayed(properties.getCardsPlayed()+1);
        properties.setCardTimerRunning(properties.getCardTimerStatic().start());
        return properties.getCurrentCard().getId();
    }

    @Override
    public boolean handleCleanup(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        properties.getHighScoresDataSource().close();

        properties.getCardTimerStatic().cancel();
        properties.getCardTimerRunning().cancel();

        properties.getCardTimerAreaBackgroundStatic().cancel();
        properties.getCardTimerAreaBackgroundRunning().cancel();
        return true;
    }

    @Override
    public boolean handleResume(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        properties.getHighScoresDataSource().open();
        return true;
    }

    @Override
    public boolean handlePause(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        handleCleanup(gamePlayActivity,properties);
        return true;
    }

    @Override
    public boolean handleInitializeGameplay(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        properties.setCardTimerRunning(properties.getCardTimerStatic().start());
        properties.getCardTimerRunning().cancel();
        properties.setCardTimerAreaBackgroundRunning(properties.getCardTimerAreaBackgroundStatic().start());
        properties.getCardTimerAreaBackgroundRunning().cancel();
        return true;
    }

    /*
     * @author leonard (11/4/2016)
     */
    public void onFragmentInteraction(GamePlayActivity gamePlayActivity, GamePlayProperties properties,String choice) {
        //Send message to host for validation
        choice = choice == null ? "" : choice;
        Answer answer = new Answer(properties.getWifiDirectApp().mDeviceName, choice);
        String json = properties.getGson().toJson(answer);
        ConnectionService.sendMessage(MSG_SEND_ANSWER_ACTIVITY, json);

        //wait for answer validation and next card
    }
}
