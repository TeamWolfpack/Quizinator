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

import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Card.TFCard;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.Database.GamePlayStats;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScores;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScoresDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.Fragments.TrueFalseChoiceAnswerFragment;
import com.seniordesign.wolfpack.quizinator.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The game play activity is...
 * @creation 09/28/2016
 */
public class GamePlayActivity
        extends AppCompatActivity
        implements TrueFalseChoiceAnswerFragment.OnFragmentInteractionListener {

    private Rules rules;
    private Deck deck;
    private Card currentCard;

    private RulesDataSource rulesDataSource;
    private HighScoresDataSource highScoresDataSource;
    private DeckDataSource deckDataSource;

    private int deckIndex;
    private int deckLength;
    private int score;

    private CountDownTimer gamePlayTimerStatic;
    private CountDownTimer gamePlayTimerRunning;
    private long gamePlayTimerRemaining;

    private CountDownTimer cardTimerStatic;
    private CountDownTimer cardTimerRunning;

    private CountDownTimer cardTimerAreaBackgroundStatic;
    private CountDownTimer cardTimerAreaBackgroundRunning;
    private int r;
    private int g;
    private int b;

    /*
     * @author kuczynskij (09/28/2016)
     * @author farrowc (10/11/2016)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        initializeDB();
        //rules = new Rules();
        //rules.setTimeLimit(60000);
        initializeGameTimer(rules.getTimeLimit());
        initializeCardTimer(rules.getCardDisplayTime());
        initializeCorrectnessColorController();
        initializeGamePlay();
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    @Override
    protected void onResume() {
        super.onResume();
        rulesDataSource.open();
        highScoresDataSource.open();
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    @Override
    protected void onPause() {
        super.onPause();
        rulesDataSource.close();
        highScoresDataSource.close();
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    private void cleanUpOnExit() {
        rulesDataSource.close();
        highScoresDataSource.close();
        this.finish();
        //remove if left unused due to creation of new intent
    }

    /*
     * @author farrowc (??/??/2016)
     */
    @Override
    public void onFragmentInteraction(String answer) {
        if (answer.equals(currentCard.getCorrectAnswer())) {
            //quickToast("Beautiful!");
            quickCorrectAnswerConfirmation(true);
            score++;
        } else {
            //quickToast("You Suck!");
            quickCorrectAnswerConfirmation(false);
        }
    }

    private boolean quickCorrectAnswerConfirmation(boolean correct) {
        setCorrectnessColors(correct);
        cardTimerAreaBackgroundRunning = cardTimerAreaBackgroundStatic.start();
        /*
        if(correct){
            findViewById(R.id.cardTimeBackground).setBackgroundColor(Color.rgb(10,200,10));
        }else{
            findViewById(R.id.cardTimeBackground).setBackgroundColor(Color.RED);
        }
        */
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
     * @author farrowc (10/11/2016)
     * @author kuczynskij (10/13/2016)
     */
    private void initializeGamePlay() {
        //Deck stuff
        deck = initializeDeck();
        deck.setDeckName("Sample");
        deckLength = Math.min(rules.getMaxCardCount(),deck.getCards().size());
        //deckLength = Math.min(deck.getCards().length, rules.getMaxCardCount());
        cardTimerRunning = cardTimerStatic.start();
        gamePlayTimerRunning = gamePlayTimerStatic.start();
        cardTimerAreaBackgroundRunning = cardTimerRunning.start();
        cardTimerAreaBackgroundRunning.cancel();
        switchToNewCard(deck, deckIndex);
    }

    /*
     * @author farrowc (10/11/2016)
     */
    private long switchToNewCard(Deck deck, int deckIndex) {
        cardTimerRunning.cancel();
        if (deckLength > deckIndex) {
            ((TextView) findViewById(R.id.scoreText)).setText("Score: " + score);

            //TODO Here set card to the card at the position of deckIndex

            currentCard = deck.getCards().get(deckIndex);

            showCard(currentCard);
            cardTimerRunning = cardTimerStatic.start();
            this.deckIndex++;
        } else {
            endGamePlay();
        }
        return currentCard.getId();
    }

    /*
     * @author farrowc (10/11/2016)
     */
    private void showCard(Card card) {
        getSupportFragmentManager().
                beginTransaction()
                .replace(R.id.answerArea, new TrueFalseChoiceAnswerFragment())
                .commit();
        ((TextView) findViewById(R.id.questionTextArea))
                .setText(card.getQuestion());
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    private void endGamePlay() {
        cardTimerRunning.cancel();
        gamePlayTimerRunning.cancel();
        final Intent intent =
                new Intent(this, EndOfGameplayActivity.class);
        checkGameStatsAgainstHighScoresDB();
        GamePlayStats s = new GamePlayStats();
        s.setScore(score);
        s.setTimeElapsed(rules.getTimeLimit() - gamePlayTimerRemaining);
        s.setTotalCardsCompleted(deckIndex);
        intent.putExtra("gameStats", s);
        startActivity(intent);
    }

    /*
     * Actions cannot be tested.
     * @author farrowc (??/??/2016)
     */
    public long answerClicked(View v) {
        cardTimerAreaBackgroundRunning.cancel();
        Button clickedButton = (Button) v;
        String answer = clickedButton.getText().toString();
        this.onFragmentInteraction(answer);
        return switchToNewCard(deck, deckIndex);
    }

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/15/2016)
     */
    private String checkGameStatsAgainstHighScoresDB() {
        if (highScoresDataSource.getAllHighScores().size() > 0) {
            HighScores h = highScoresDataSource.getAllHighScores().get(0);
            if (score >= h.getBestScore()) {
                if (score > h.getBestScore() || rules.getTimeLimit() - gamePlayTimerRemaining < h.getBestTime()) {
                    h.setBestTime(rules.getTimeLimit() - gamePlayTimerRemaining);
                }
                h.setBestScore(score);
                h.setDeckName(deck.getDeckName());
                highScoresDataSource.deleteHighScore(h);
                highScoresDataSource.createHighScore(h.getDeckName(), h.getBestTime(), h.getBestScore());
                return "Updated HighScores";
            }
            return "No HighScore";
        } else {
            highScoresDataSource.createHighScore(deck.getDeckName(),
                    rules.getTimeLimit() - gamePlayTimerRemaining, score);
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
     * @author farrowc (10/13/2016)
     * TEMP returns a sample deck for testing
     * The deck database is incomplete right now
     */
    private Deck initializeDeck() {
        Deck newDeck = new Deck();
        Card[] cards = new Card[5];
        cards[0] = new TFCard();
        cards[0].setQuestion("This is True.");
        cards[0].setCorrectAnswer("True");
        cards[1] = new TFCard();
        cards[1].setQuestion("This is False.");
        cards[1].setCorrectAnswer("False");
        cards[2] = new TFCard();
        cards[2].setQuestion("2+2 = 4");
        cards[2].setCorrectAnswer("True");
        cards[3] = new TFCard();
        cards[3].setQuestion("1*2 = 2");
        cards[3].setCorrectAnswer("True");
        cards[4] = new TFCard();
        cards[4].setQuestion("2/1 = 1");
        cards[4].setCorrectAnswer("False");
        newDeck.setCards(Arrays.asList(cards));

        return newDeck;
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    private boolean initializeDB() {
        int positiveDBConnections = 0;
        rulesDataSource = new RulesDataSource(this);
        if (rulesDataSource.open()) {
            positiveDBConnections++;
            rules = rulesDataSource.getAllRules().get(0);
        }
        highScoresDataSource = new HighScoresDataSource(this);
        if (highScoresDataSource.open()) {
            positiveDBConnections++;
        }
        deckDataSource = new DeckDataSource(this);
        if (deckDataSource.open()) {
            positiveDBConnections++;
            //deck = deckDataSource.getAllDecks().get(0);
        }
        return (positiveDBConnections == 3);
    }

    /*
     * @author farrowc 10/14/2016
     */
    private boolean initializeGameTimer(long time) {
        gamePlayTimerStatic = new CountDownTimer(time, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.gamePlayTimeText)).setText(
                        "Game Time: " + millisUntilFinished / 60000 + ":" + millisUntilFinished / 1000 % 60
                );
                gamePlayTimerRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                endGamePlay();
            }
        };
        return true;
    }

    /*
     * @author farrowc 10/14/2016
     */
    private boolean initializeCardTimer(long time) {
        cardTimerStatic = new CountDownTimer(time, 10) {

            @Override
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.cardTimeBackground)).setText(
                        "Time Left: " + millisUntilFinished / 1000 + "." + millisUntilFinished / 10 % 100
                );
            }

            @Override
            public void onFinish() {
                switchToNewCard(deck, deckIndex);
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
                adjustCardTimerColor();
            }

            @Override
            public void onFinish() {
                findViewById(R.id.cardTimeBackground).setBackgroundColor(Color.rgb(0, 0, 0));

            }
        };
        return true;
    }
}