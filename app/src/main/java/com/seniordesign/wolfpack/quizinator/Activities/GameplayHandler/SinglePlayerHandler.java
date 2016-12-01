package com.seniordesign.wolfpack.quizinator.Activities.GameplayHandler;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Activities.GamePlayActivity;
import com.seniordesign.wolfpack.quizinator.Activities.NewGameSettingsActivity;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScoresDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by farrowc on 11/29/2016.
 */

public class SinglePlayerHandler implements GamePlayHandler {

    @Override
    public boolean handleInitialization(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        initializeDB(gamePlayActivity,properties);

        return false;
    }

    @Override
    public long handleAnswerClicked(GamePlayActivity gamePlayActivity, GamePlayProperties properties, String answer) {
        properties.getCardTimerAreaBackgroundRunning().cancel();
        if(answer==null){
            gamePlayActivity.quickCorrectAnswerConfirmation(false);
        }
        else if (answer.equals(properties.getCurrentCard().getCorrectAnswer())) {
            gamePlayActivity.quickCorrectAnswerConfirmation(true);
            properties.setScore(properties.getScore()+1);
        } else {
            gamePlayActivity.quickCorrectAnswerConfirmation(false);
        }
        return handleNextCard(gamePlayActivity, properties);
    }

    @Override
    public boolean initializeDB(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        int positiveDBConnections = 0;
        properties.setRulesDataSource(new RulesDataSource(gamePlayActivity));
        if (properties.getRulesDataSource().open()) {
            positiveDBConnections++;
            List<Rules> ruleList = properties.getRulesDataSource().getAllRules();
            properties.setRules(ruleList.get(ruleList.size() - 1));
        }
        properties.setHighScoresDataSource(new HighScoresDataSource(gamePlayActivity));
        if (properties.getHighScoresDataSource().open()) {
            positiveDBConnections++;
        }
        properties.setDeckDataSource(new DeckDataSource(gamePlayActivity));
        if (properties.getDeckDataSource().open()) {
            positiveDBConnections++;
            initializeDeck(properties);
            properties.setDeck(properties.getDeckDataSource().getAllDecks().get(0));
        }
        return (positiveDBConnections == 3);
    }

    @Override
    public long handleNextCard(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        properties.getCardTimerRunning().cancel();
        if (properties.getDeckLength() > properties.getDeckIndex()) {
            ((TextView) gamePlayActivity.findViewById(R.id.scoreText)).setText("Score: " + properties.getScore());

            //TODO Here set card to the card at the position of deckIndex

            properties.setCurrentCard(properties.getDeck().getCards().get(properties.getDeckIndex()));

            gamePlayActivity.showCard(properties.getCurrentCard());
            properties.setCardTimerRunning(properties.getCardTimerStatic().start());
            properties.setDeckIndex(properties.getDeckIndex()+1);
            properties.setCardsPlayed(properties.getCardsPlayed()+1);
        } else {
            gamePlayActivity.endGamePlay(properties.getRules().getTimeLimit() - properties.getGamePlayTimerRemaining());
        }
        return properties.getCurrentCard().getId();
    }

    @Override
    public boolean handleCleanup(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        properties.getRulesDataSource().close();
        properties.getHighScoresDataSource().close();
        properties.getDeckDataSource().close();

        properties.getGamePlayTimerStatic().cancel();
        properties.getGamePlayTimerRunning().cancel();

        properties.getCardTimerStatic().cancel();
        properties.getCardTimerRunning().cancel();

        properties.getCardTimerAreaBackgroundStatic().cancel();
        properties.getCardTimerAreaBackgroundRunning().cancel();
        return true;
    }

    @Override
    public boolean handleResume(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        properties.getRulesDataSource().open();
        properties.getHighScoresDataSource().open();
        return true;
    }

    @Override
    public boolean handlePause(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        return true;
    }

    @Override
    public boolean handleInitializeGameplay(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        //Deck stuff
        properties.getDeck().setDeckName("Sample");
        properties.setDeckLength(Math.min(properties.getRules().getMaxCardCount(),properties.getDeck().getCards().size()));
        //deckLength = Math.min(deck.getCards().length, rules.getMaxCardCount());
        properties.setCardTimerRunning(properties.getCardTimerStatic().start());
        properties.setGamePlayTimerRunning(properties.getGamePlayTimerStatic().start());
        properties.setCardTimerAreaBackgroundRunning(properties.getCardTimerAreaBackgroundStatic().start());
        properties.getCardTimerAreaBackgroundRunning().cancel();
        handleNextCard(gamePlayActivity, properties);
        return false;
    }

    @Override
    public void onFragmentInteraction(GamePlayActivity gamePlayActivity, GamePlayProperties properties, String choice) {
        //Do nothing
    }

    private Deck initializeDeck(GamePlayProperties properties) {
        Deck newDeck = new Deck();
        Card[] cards = new Card[10];
        cards[0] = new Card();
        cards[0].setQuestion("1+1 = ?");
        cards[0].setCorrectAnswer("2");
        String[] answerArea = {"1","2","3","4"};
        cards[0].setPossibleAnswers(answerArea);
        cards[0].setCardType("MC");
        cards[1] = new Card();
        cards[1].setQuestion("1*2 = 0");
        cards[1].setCorrectAnswer("False");
        cards[1].setCardType("TF");
        cards[2] = new Card();
        cards[2].setQuestion("4*5 = 20");
        cards[2].setCorrectAnswer("True");
        cards[2].setCardType("TF");
        cards[3] = new Card();
        cards[3].setQuestion("20*10 = 100");
        cards[3].setCorrectAnswer("False");
        cards[3].setCardType("TF");
        cards[4] = new Card();
        cards[4].setQuestion("10*91 = 901");
        cards[4].setCorrectAnswer("False");
        cards[4].setCardType("TF");
        cards[5] = new Card();
        cards[5].setQuestion("100^2 = 10000");
        cards[5].setCorrectAnswer("True");
        cards[5].setCardType("TF");
        cards[6] = new Card();
        cards[6].setQuestion("10*102 = 1002");
        cards[6].setCorrectAnswer("False");
        cards[6].setCardType("TF");
        cards[7] = new Card();
        cards[7].setQuestion("8/2 = 4");
        cards[7].setCorrectAnswer("True");
        cards[7].setCardType("TF");
        cards[8] = new Card();
        cards[8].setQuestion("120/4 = 30");
        cards[8].setCorrectAnswer("True");
        cards[8].setCardType("TF");
        cards[9] = new Card();
        cards[9].setQuestion("6*7 = 41");
        cards[9].setCorrectAnswer("False");
        cards[9].setCardType("TF");
        newDeck.setCards(Arrays.asList(cards));

        properties.getDeckDataSource().createDeck("Default", Arrays.asList(cards));

        return newDeck;
    }



}
