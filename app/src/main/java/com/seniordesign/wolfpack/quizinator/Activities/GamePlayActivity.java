package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.seniordesign.wolfpack.quizinator.GameplayHandler.GamePlayHandler;
import com.seniordesign.wolfpack.quizinator.GameplayHandler.GamePlayProperties;
import com.seniordesign.wolfpack.quizinator.GameplayHandler.MultiplayerHandler;
import com.seniordesign.wolfpack.quizinator.GameplayHandler.SinglePlayerHandler;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.GamePlayStats;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScores;
import com.seniordesign.wolfpack.quizinator.Fragments.MultipleChoiceAnswerFragment;
import com.seniordesign.wolfpack.quizinator.Fragments.TrueFalseChoiceAnswerFragment;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

/*
 * The game play activity is...
 * @creation 09/28/2016
 */
public class GamePlayActivity
        extends AppCompatActivity
        implements TrueFalseChoiceAnswerFragment.OnFragmentInteractionListener,
        MultipleChoiceAnswerFragment.OnFragmentInteractionListener{

    GamePlayProperties properties;
    GamePlayHandler gamePlayHandler;

    /*
     * @author kuczynskij (09/28/2016)
     * @author farrowc (10/11/2016)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        properties = new GamePlayProperties();

        properties.setWifiDirectApp((WifiDirectApp)getApplication());
        if(getIntent().getExtras().getBoolean("GameMode")){
            System.out.println("SP");
            gamePlayHandler = new SinglePlayerHandler();
        }else{
            System.out.println("MP");
            gamePlayHandler = new MultiplayerHandler();
        }

        gamePlayHandler.handleInitialization(this, properties);
        initializeCardTimer(properties.getRules().getCardDisplayTime());
        initializeGameTimer(properties.getRules().getTimeLimit());
        initializeCorrectnessColorController();
        gamePlayHandler.handleInitializeGameplay(this,properties);
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    @Override
    protected void onResume() {
        super.onResume();
        gamePlayHandler.handleResume(this, properties);
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
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

    /*
     * @author farrowc (??/??/2016)
     */
    @Override
    public void onFragmentInteraction(String answer) {

    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    private boolean quickToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return true;
    }

    /*
     * @author farrowc (10/11/2016)
     */
    public void showCard(final Card card) {
        switch(card.getCardType()){
            case("TF"):
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().
                                beginTransaction()
                                .replace(R.id.answerArea, new TrueFalseChoiceAnswerFragment())
                                .commitNowAllowingStateLoss();
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
                                .commitNowAllowingStateLoss();
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
     * @author leonardj (12/14/16)
     */
    public void endGamePlay() {
        long time = properties.getRules().getCardDisplayTime() + properties.getCardsPlayed();
        endGamePlay(time);
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    public void endGamePlay(long totalGameTime) {
        if(properties.getCardTimerRunning()!=null) {
            properties.getCardTimerRunning().cancel();
        }
        if(properties.getGamePlayTimerRunning()!=null) {
            properties.getGamePlayTimerRunning().cancel();
        }
        final Intent intent =
                new Intent(this, EndOfGameplayActivity.class);
        GamePlayStats s = new GamePlayStats();
        s.setScore(properties.getScore());
        s.setTimeElapsed(totalGameTime);
        s.setTotalCardsCompleted(properties.getCardsPlayed());
        checkGameStatsAgainstHighScoresDB(s);
        intent.putExtra("gameStats", s);
        startActivity(intent);
        finish();
    }

    /*
     * Actions cannot be tested.
     * @author farrowc (??/??/2016)
     */
    public long answerClicked(View v) {
        properties.getCardTimerAreaBackgroundRunning().cancel();
        Button clickedButton = (Button) v;
        String answer = clickedButton.getText().toString();
        return gamePlayHandler.handleAnswerClicked(this, properties, answer);
    }

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/15/2016)
     */
    private String checkGameStatsAgainstHighScoresDB(GamePlayStats stats) {
        if (properties.getHighScoresDataSource().getAllHighScores().size() > 0) {
            HighScores h = properties.getHighScoresDataSource().getAllHighScores().get(0);
            if (properties.getScore() >= h.getBestScore()) {
                if (properties.getScore() > h.getBestScore() || stats.getTimeElapsed() < h.getBestTime()) {
                    h.setBestTime(stats.getTimeElapsed());
                }
                h.setBestScore(properties.getScore());
                //h.setDeckName(deck.getDeckName());
                h.setDeckName("Multiplayer game");
                properties.getHighScoresDataSource().deleteHighScore(h);
                properties.getHighScoresDataSource().createHighScore(h.getDeckName(), h.getBestTime(), h.getBestScore());
                return "Updated HighScores";
            }
            return "No HighScore";
        } else {
            properties.getHighScoresDataSource().createHighScore("Multiplayer game",
                    stats.getTimeElapsed(), properties.getScore());
            return "New HighScore";
        }
    }


    /*
     * @author farrowc (10/14/2016)
     */
    private boolean adjustCardTimerColor() {
        findViewById(R.id.cardTimeBackground).setBackgroundColor(Color.rgb(properties.getR(), properties.getG(), properties.getB()));
        properties.setR((int)(properties.getR()/1.05));
        properties.setG((int)(properties.getG()/1.05));
        properties.setB((int)(properties.getB()/1.05));
        return true;
    }

    /*
     * @author farrowc (10/14/2016)
     */
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

    /*
     * @author farrowc 10/14/2016
     */
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

    /*
     * @author farrowc 10/14/2016
     */
    public boolean initializeCardTimer(long time) {
        properties.setCardTimerStatic(new CountDownTimer(time, 10) {

            @Override
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.cardTimeBackground)).setText(
                        "Time Left: " + millisUntilFinished / 1000
                );
            }

            @Override
            public void onFinish() {
                gamePlayHandler.onFragmentInteraction(GamePlayActivity.this, properties, null);
                gamePlayHandler.handleNextCard(GamePlayActivity.this,properties);
            }
        });
        return true;
    }

    /*
     * @author farrowc (10/14/2016)
     */
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
        if (correct)
            properties.setScore(properties.getScore()+1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ((TextView) findViewById(R.id.scoreText)).setText("Score: " + properties.getScore());
            }
        });

        quickCorrectAnswerConfirmation(correct);
    }

    public boolean quickCorrectAnswerConfirmation(boolean correct) {
        setCorrectnessColors(correct);
        properties.setCardTimerAreaBackgroundRunning(properties.getCardTimerAreaBackgroundStatic().start());
        return true;
    }

}