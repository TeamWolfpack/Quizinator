package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.Database.GamePlayStats;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScores;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScoresDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.Fragments.MultipleChoiceAnswerFragment;
import com.seniordesign.wolfpack.quizinator.Fragments.TrueFalseChoiceAnswerFragment;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.Answer;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import java.util.Arrays;
import java.util.List;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_PLAYER_READY_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_SEND_ANSWER_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_SEND_CARD_ACTIVITY;

/**
 * This Activity is started in ConnectionSerive.onPullInData(...) when rule message
 * is received.
 */
public class MultiplayerGameplayActivity
        extends
            AppCompatActivity
        implements
            TrueFalseChoiceAnswerFragment.OnFragmentInteractionListener,
            MultipleChoiceAnswerFragment.OnFragmentInteractionListener{

    private static final String TAG = "ACT_MGA";


    /*
     * @author leonard (11/4/2016)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        //TODO This is temporary, the game timer will be fixed later
        findViewById(R.id.gamePlayTimeText).setVisibility(View.INVISIBLE);

        initializeGamePlay();
    }

    /**
     * This is called in ConnectionSerive.onPullInData(...) when a card message
     * is received.
     */
    /*
     * @author leonardj (11/5/16)
     */
    public void receivedNextCard(Card card) {
        hasAnswered = false;
        currentCard = card;
        showCard(currentCard);
        cardsPlayed++;
        cardTimerRunning = cardTimerStatic.start();
    }

    /**
     * This is called in ConnectionSerive.onPullInData(...) when a confirmation message
     * is received.
     */
    /*
     * @author leonardj (11/5/16)
     */
    public void answerConfirmed(boolean correct) {
        if (correct)
            score++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ((TextView) findViewById(R.id.scoreText)).setText("Score: " + score);
            }
        });
        quickCorrectAnswerConfirmation(correct);
    }

    /*
     * @author leonard (11/4/2016)
     */
    @Override
    public void onFragmentInteraction(String choice) {
        //Send message to host for validation
        choice = choice == null ? "" : choice;
        Answer answer = new Answer(wifiDirectApp.mDeviceName, choice);
        String json = gson.toJson(answer);
        ConnectionService.sendMessage(MSG_SEND_ANSWER_ACTIVITY, json);

        //wait for answer validation and next card
    }

    private boolean quickCorrectAnswerConfirmation(boolean correct) {
        setCorrectnessColors(correct);
        cardTimerAreaBackgroundRunning = cardTimerAreaBackgroundStatic.start();
        return true;
    }

    private void initializeGamePlay() {

    }

    /*
     * @author farrowc (10/11/2016)
     */
    private void showCard(final Card card) {
        switch(card.getCardType()){
            case("TF"):
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().
                                beginTransaction()
                                .replace(R.id.answerArea, new TrueFalseChoiceAnswerFragment())
                                .commitNow();
                        ((TextView) findViewById(R.id.questionTextArea))
                                .setText(card.getQuestion());
                        getSupportFragmentManager().executePendingTransactions();
                    }
                });
                break;
            case("MC"):
                final MultipleChoiceAnswerFragment mcFragment = new MultipleChoiceAnswerFragment();
                mcFragment.setChoiceA(card.getPossibleAnswers()[0]);
                mcFragment.setChoiceB(card.getPossibleAnswers()[1]);
                mcFragment.setChoiceC(card.getPossibleAnswers()[2]);
                mcFragment.setChoiceD(card.getPossibleAnswers()[3]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().
                                beginTransaction()
                                .replace(R.id.answerArea,mcFragment)
                                .commitNow();

                        ((TextView) findViewById(R.id.questionTextArea))
                                .setText(card.getQuestion());

                    }
                });



                break;
            default:
                break;


        }
    }

    /*
     * @author kuczynskij (10/13/2016)
     * @author leoanrdj (11/4/2016)
     */
    public void endGamePlay(long totalGameTime) {
        cardTimerRunning.cancel();
        final Intent intent =
                new Intent(this, EndOfGameplayActivity.class);
        GamePlayStats s = new GamePlayStats();
        s.setScore(score);
        s.setTimeElapsed(totalGameTime);
        s.setTotalCardsCompleted(cardsPlayed);
        checkGameStatsAgainstHighScoresDB(s);
        intent.putExtra("gameStats", s);
        startActivity(intent);
    }


    /*
     * Actions cannot be tested.
     * @author farrowc (??/??/2016)
     */
    public long answerClicked(View v) {
        cardTimerRunning.cancel();
        if(hasAnswered)
            return 0;
        hasAnswered = true;
        cardTimerAreaBackgroundRunning.cancel();
        Button clickedButton = (Button) v;
        String answer = clickedButton.getText().toString();
        onFragmentInteraction(answer);

        if (currentCard != null)
            return currentCard.getId();
        return -1;
    }

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/15/2016)
     */
    private String checkGameStatsAgainstHighScoresDB(GamePlayStats stats) {
        if (highScoresDataSource.getAllHighScores().size() > 0) {
            HighScores h = highScoresDataSource.getAllHighScores().get(0);
            if (score >= h.getBestScore()) {
                if (score > h.getBestScore() || stats.getTimeElapsed() < h.getBestTime()) {
                    h.setBestTime(stats.getTimeElapsed());
                }
                h.setBestScore(score);
                //h.setDeckName(deck.getDeckName());
                h.setDeckName("Multiplayer game");
                highScoresDataSource.deleteHighScore(h);
                highScoresDataSource.createHighScore(h.getDeckName(), h.getBestTime(), h.getBestScore());
                return "Updated HighScores";
            }
            return "No HighScore";
        } else {
            highScoresDataSource.createHighScore("Multiplayer game",
                    stats.getTimeElapsed(), score);
            return "New HighScore";
        }
    }

    /*
     * @author farrowc (10/14/2016)
     */
    private boolean adjustCardTimerColor() {
        findViewById(R.id.cardTimeBackground).setBackgroundColor(Color.rgb(r, g, b));
        r /= 1.05;
        g /= 1.05;
        b /= 1.05;
        return true;
    }

    /*
     * @author farrowc (10/14/2016)
     */
    private boolean setCorrectnessColors(boolean isAnswerCorrect) {
        if (isAnswerCorrect) {
            r = 10;
            g = 200;
            b = 10;
        } else {
            r = 200;
            g = 10;
            b = 10;
        }
        return true;
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    private boolean initializeDB() {
        highScoresDataSource = new HighScoresDataSource(this);
        return highScoresDataSource.open();
    }

    /*
     * @author farrowc 10/14/2016
     */
    private boolean initializeCardTimer(long time) {
        cardTimerStatic = new CountDownTimer(time, 10) {

            @Override
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.cardTimeBackground)).setText(
                        "Time Left: " + millisUntilFinished / 1000
                );
            }

            @Override
            public void onFinish() {
                onFragmentInteraction(null);
            }
        };
        return true;
    }

    /*
     * @author farrowc (10/14/2016)
     */
    private boolean initializeCorrectnessColorController() {
        cardTimerAreaBackgroundStatic = new CountDownTimer(1000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adjustCardTimerColor();
                    }
                });
            }

            @Override
            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.cardTimeBackground).setBackgroundColor(Color.rgb(0, 0, 0));
                    }
                });
            }
        };
        return true;
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    private boolean quickToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return true;
    }

    /*
     * @author leonard (11/4/2016)
     */
    @Override
    protected void onResume() {
        super.onResume();
        highScoresDataSource.open();
    }

    /*
     * @author leonard (11/4/2016)
     */
    @Override
    protected void onPause() {
        super.onPause();
        cleanUpOnExit();
    }

    /*
     * @author leonard (11/4/2016)
     */
    private void cleanUpOnExit() {
        highScoresDataSource.close();

        cardTimerStatic.cancel();
        cardTimerRunning.cancel();

        cardTimerAreaBackgroundStatic.cancel();
        cardTimerAreaBackgroundRunning.cancel();

        this.finish();
    }
}
