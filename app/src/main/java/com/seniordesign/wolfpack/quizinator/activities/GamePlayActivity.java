package com.seniordesign.wolfpack.quizinator.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.filters.NumberFilter;
import com.seniordesign.wolfpack.quizinator.fragments.FreeResponseAnswerFragment;
import com.seniordesign.wolfpack.quizinator.fragments.TrueFalseAnswerFragment;
import com.seniordesign.wolfpack.quizinator.fragments.VerbalResponseAnswerFragment;
import com.seniordesign.wolfpack.quizinator.gameplayHandler.GamePlayHandler;
import com.seniordesign.wolfpack.quizinator.gameplayHandler.GamePlayProperties;
import com.seniordesign.wolfpack.quizinator.gameplayHandler.MultiplayerHandler;
import com.seniordesign.wolfpack.quizinator.gameplayHandler.SinglePlayerHandler;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.database.GamePlayStats;
import com.seniordesign.wolfpack.quizinator.database.HighScores;
import com.seniordesign.wolfpack.quizinator.fragments.MultipleChoiceAnswerFragment;
import com.seniordesign.wolfpack.quizinator.messages.Wager;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.Util;
import com.seniordesign.wolfpack.quizinator.views.CardIcon;
import com.seniordesign.wolfpack.quizinator.wifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.wifiDirect.WifiDirectApp;

import java.util.Arrays;
import java.util.Collections;

import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_ANSWER_CONFIRMATION_HANDSHAKE_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_SEND_WAGER_CONFIRMATION_ACTIVITY;

public class GamePlayActivity extends AppCompatActivity {

    GamePlayProperties properties;
    GamePlayHandler gamePlayHandler;
    android.app.AlertDialog wagerDialog;
    Wager wager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        properties = new GamePlayProperties();

        properties.setWifiDirectApp((WifiDirectApp) getApplication());
        if (getIntent().getExtras().getBoolean("GameMode")) {
            gamePlayHandler = new SinglePlayerHandler();
        } else {
            gamePlayHandler = new MultiplayerHandler();
        }
        gamePlayHandler.handleInitialization(this, properties);
        initializeCardTimer(properties.getRules().getCardDisplayTime());
        initializeGameTimer(properties.getRules().getTimeLimit());
        initializeCorrectnessColorController();
        gamePlayHandler.handleInitializeGameplay(this, properties);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gamePlayHandler.handleResume(this, properties);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gamePlayHandler.handlePause(this, properties);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gamePlayHandler.handleDestroy(this, properties);
    }

    public void showCard(final Card card) {
        Constants.CARD_TYPES cardType =
                Constants.CARD_TYPES.values()[card.getCardType()];
        switch (cardType) {
            case TRUE_FALSE:
                showCardHelper(card, new TrueFalseAnswerFragment());
                break;
            case MULTIPLE_CHOICE:
                final MultipleChoiceAnswerFragment mcFragment =
                        new MultipleChoiceAnswerFragment();
                Collections.shuffle(Arrays.asList(card.getPossibleAnswers()));
                mcFragment.setChoiceA(card.getPossibleAnswers()[0]);
                mcFragment.setChoiceB(card.getPossibleAnswers()[1]);
                mcFragment.setChoiceC(card.getPossibleAnswers()[2]);
                mcFragment.setChoiceD(card.getPossibleAnswers()[3]);
                showCardHelper(card, mcFragment);
                break;
            case FREE_RESPONSE:
                showCardHelper(card, new FreeResponseAnswerFragment());
                break;
            case VERBAL_RESPONSE:
                showCardHelper(card, new VerbalResponseAnswerFragment());
                break;
        }
    }

    private void showCardHelper(
            final Card card,
            final android.support.v4.app.Fragment frag) {
        runOnUiThread(new Runnable() {
            @SuppressLint("CommitTransaction")
            @Override
            public void run() {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.answerArea, frag)
                        .commitNowAllowingStateLoss();
                Util.updateCardTypeIcon(card, (CardIcon) findViewById(R.id.questionCardTypeIcon));
                ((TextView) findViewById(R.id.questionTextArea)).setText(card.getQuestion());
                getSupportFragmentManager().executePendingTransactions();
            }
        });
    }

    public void endGamePlay() {
        long time = properties.getRules().getCardDisplayTime() +
                properties.getCardsPlayed();
        endGamePlay(time);
    }

    public void endGamePlay(long totalGameTime) {
        if (properties.getCardTimerRunning() != null) {
            properties.getCardTimerRunning().cancel();
        }
        if (properties.getGamePlayTimerRunning() != null) {
            properties.getGamePlayTimerRunning().cancel();
        }
        final Intent intent = new Intent(this, EndOfGameplayActivity.class);
        GamePlayStats s = new GamePlayStats();
        s.setScore(properties.getScore());
        s.setTimeElapsed(totalGameTime);
        s.setTotalCardsCompleted(properties.getCardsPlayed());
        if(properties.getDeck()!=null){
            s.setDeckID(properties.getDeck().getId());
        }
        else{
            s.setDeckID(-1);
        }
        checkGameStatsAgainstHighScoresDB(s);
        intent.putExtra("gameStats", s);
        startActivity(intent);
        finish();
    }

    public void answerClicked(View v) {
        properties.getCardTimerAreaBackgroundRunning().cancel();
        Button clickedButton = (Button) v;
        String answer = clickedButton.getText().toString();
        if (answer.equals("Submit")) {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            EditText freeResponseAnswer = (EditText) findViewById(R.id.freeResponseAnswerArea);
            answer = freeResponseAnswer.getText().toString();
        }
        gamePlayHandler.handleAnswerClicked(this, properties, answer);
    }

    private String checkGameStatsAgainstHighScoresDB(GamePlayStats stats) {
        if(properties.getDeck()==null){
            return Constants.NO_HIGH_SCORE;//TODO maybe change this to a MULTIPLAYER_HIGH_SCORE
        }
        if (properties.getDataSource().getAllHighScores().size() > 0) {
            HighScores h = null;
            for(HighScores hs : properties.getDataSource().getAllHighScores()){
                if(hs.getDeckID() == properties.getDeck().getId()){
                    h = hs;
                    break;
                }
            }
            if(h == null){
                properties.getDataSource().createHighScore(
                        properties.getDeck().getId(),
                        stats.getTimeElapsed(), properties.getScore());
                return Constants.NEW_HIGH_SCORE;
            }
            else if (properties.getScore() >= h.getBestScore()) {
                if (properties.getScore() > h.getBestScore() || stats.getTimeElapsed() < h.getBestTime()) {
                    h.setBestTime(stats.getTimeElapsed());
                }
                h.setBestScore(properties.getScore());
                h.setDeckID(properties.getDeck().getId());
                properties.getDataSource().deleteHighScore(h);
                properties.getDataSource().createHighScore(h.getDeckID(), h.getBestTime(), h.getBestScore());
                return Constants.UPDATED_HIGH_SCORE;
            }
            return Constants.NO_HIGH_SCORE;
        } else {

            properties.getDataSource().createHighScore(
                    properties.getDeck().getId(),
                    stats.getTimeElapsed(), properties.getScore());
            return Constants.NEW_HIGH_SCORE;
        }
    }

    private boolean adjustCardTimerColor() {
        findViewById(R.id.cardTimeBackground).setBackgroundColor(Color.rgb(properties.getR(), properties.getG(), properties.getB()));
        properties.setR((int) (properties.getR() / 1.05));
        properties.setG((int) (properties.getG() / 1.05));
        properties.setB((int) (properties.getB() / 1.05));
        return true;
    }

    public boolean setCorrectnessColors(boolean isAnswerCorrect) {
        if (isAnswerCorrect) {
            properties.setR(10);
            properties.setG(200);
            properties.setB(10);
        } else {
            properties.setR(200);
            properties.setG(10);
            properties.setB(10);
        }
        return true;
    }

    public boolean initializeGameTimer(long time) {
        properties.setGamePlayTimerStatic(new CountDownTimer(time, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.gamePlayTimeText)).setText(
                        "Game Time: " + millisUntilFinished / 60000 + ":" + millisUntilFinished / 1000 % 60
                );
                properties.setGamePlayTimerRemaining(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                endGamePlay(properties.getRules().getTimeLimit() - properties.getGamePlayTimerRemaining());
            }
        });
        return true;
    }

    public boolean initializeCardTimer(long time) {
        properties.setCardTimerStatic(new CountDownTimer(time, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.cardTimeBackground)).setText(
                        String.valueOf("Time Left: " + (millisUntilFinished / 1000))
                );
            }

            @Override
            public void onFinish() {
                gamePlayHandler.onFragmentInteraction(GamePlayActivity.this, properties, null);
                properties.setHasAnswered(true);

                // Wait for Moderator if time runs out
                if (Boolean.parseBoolean(properties.getCurrentCard().getModeratorNeeded()))
                    return;

                gamePlayHandler.handleNextCard(GamePlayActivity.this, properties);
            }
        });
        return true;
    }

    public boolean initializeCorrectnessColorController() {
        properties.setCardTimerAreaBackgroundStatic(new CountDownTimer(1000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                adjustCardTimerColor();
            }

            @Override
            public void onFinish() {
                findViewById(R.id.cardTimeBackground).setBackgroundColor(Color.rgb(0, 0, 0));
            }
        });
        return true;
    }

    public void receivedNextCard(Card card) {
        properties.setCurrentCard(card);
        gamePlayHandler.handleNextCard(this, properties);
    }

    public void answerConfirmed(boolean correct) {
        int score = properties.getCurrentCard().getPoints();
        if (!(wager == null)) {
            score = wager.getWager();
        }
        if (!correct) {
            if (properties.getCurrentCard().isDoubleEdge())
                score = -score;
            else
                score = 0;
        }

        properties.setScore(Math.max(0, properties.getScore() + score));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.scoreText)).setText("Score: " + properties.getScore());
            }
        });
        if (gamePlayHandler instanceof MultiplayerHandler) {
            ConnectionService.sendMessage(
                    MSG_ANSWER_CONFIRMATION_HANDSHAKE_ACTIVITY,
                    properties.getWifiDirectApp().mMyIpAddress);
        }
        quickCorrectAnswerConfirmation(correct);
    }

    public boolean quickCorrectAnswerConfirmation(boolean correct) {
        setCorrectnessColors(correct);
        properties.setCardTimerAreaBackgroundRunning(properties.getCardTimerAreaBackgroundStatic().start());
        return true;
    }

    public void onSkipQuestionClick(View v) {
        if (properties.getHasAnswered())
            return;
        gamePlayHandler.onFragmentInteraction(GamePlayActivity.this, properties, null);
        properties.setHasAnswered(true);

        // Wait for Moderator if time runs out
        if (Boolean.parseBoolean(properties.getCurrentCard().getModeratorNeeded()))
            return;

        gamePlayHandler.handleNextCard(GamePlayActivity.this, properties);
    }

    public void createWager() {
        properties.getCardTimerRunning().cancel();

        LayoutInflater li = LayoutInflater.from(this);
        final View promptsView = li.inflate(R.layout.fragment_create_wager, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle(Constants.FINAL_QUESTION)
                .setPositiveButton(Constants.ACCEPT, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        sendWager();
                        dialog.cancel();
                    }
                });

        wagerDialog = alertDialogBuilder.create();
        wagerDialog.show();
        EditText wagerEditText = (EditText) wagerDialog.findViewById(R.id.wager_value);
        NumberFilter wagerFilter = new NumberFilter(0, properties.getScore(), false);
        wagerEditText.setOnFocusChangeListener(wagerFilter);
    }

    public void sendWager() {
        EditText wagerEditText = (EditText) wagerDialog.findViewById(R.id.wager_value);
        int wagerVal = 0;
        if (!wagerEditText.getText().toString().equals(""))
            wagerVal = Integer.parseInt(wagerEditText.getText().toString());
        wagerVal = Math.min(wagerVal,properties.getScore());
        wagerVal = Math.max(wagerVal, 0);
        wager = new Wager(
                properties.getWifiDirectApp().mDeviceName,
                properties.getWifiDirectApp().mMyIpAddress,
                properties.getWifiDirectApp().mThisDevice.deviceAddress,
                wagerVal
        );
        String json = properties.getGson().toJson(wager);
        ConnectionService.sendMessage(MSG_SEND_WAGER_CONFIRMATION_ACTIVITY, json);
    }
}