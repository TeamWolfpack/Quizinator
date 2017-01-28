package com.seniordesign.wolfpack.quizinator.GameplayHandler;

import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Activities.GamePlayActivity;
import com.seniordesign.wolfpack.quizinator.Database.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScoresDataSource;
import com.seniordesign.wolfpack.quizinator.Database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.Collections;
import java.util.List;

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
            properties.setScore(properties.getScore() + properties.getCurrentCard().getPoints());
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
        properties.setQuizDataSource(new QuizDataSource(gamePlayActivity)); //TODO
        if (properties.getDataSource().open()) {
            positiveDBConnections++;

            //Filter and Shuffle deck
            Deck deck = properties.getDataSource()
                    .getDeckWithId(properties.getRules().getDeckId())
                    .filter(properties.getRules());
            List<Card> cards = deck.getCards();
            Collections.shuffle(cards);
            deck.setCards(cards);
            properties.setDeck(deck);
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
        properties.getDataSource().close();

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
    public boolean handleDestroy(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        return true;
    }

    @Override
    public boolean handleInitializeGameplay(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        Rules rules = properties.getRules();
        properties.setDeckLength(Math.min(rules.getMaxCardCount(), properties.getDeck().getCards().size()));
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
}
