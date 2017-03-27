package com.seniordesign.wolfpack.quizinator.gameplayHandler;

import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seniordesign.wolfpack.quizinator.activities.GamePlayActivity;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.database.Deck;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.database.Rules;
import com.seniordesign.wolfpack.quizinator.R;

import java.lang.reflect.Type;
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
        properties.setDataSource(new QuizDataSource(gamePlayActivity));
        if (!properties.getDataSource().open())
            return false;

        List<Rules> ruleList = properties.getDataSource().getAllRules();
        properties.setRules(ruleList.get(0));

        //Filter and Shuffle deck
        Type listType = new TypeToken<List<Constants.CARD_TYPES>>(){}.getType();
        List<Constants.CARD_TYPES> cardTypeList = new Gson().fromJson(properties.getRules().getCardTypes(), listType);
        Deck deck = properties.getDataSource()
                .getFilteredDeck(properties.getRules().getDeckId(), cardTypeList, false);
        List<Card> cards = deck.getCards();
        Collections.shuffle(cards);
        deck.setCards(cards);
        properties.setDeck(deck);
        return true;
    }

    @Override
    public long handleNextCard(GamePlayActivity gamePlayActivity, GamePlayProperties properties) {
        properties.getCardTimerRunning().cancel();
        if (properties.getDeckLength() > properties.getDeckIndex()) {
            ((TextView) gamePlayActivity.findViewById(R.id.scoreText)).setText(String.valueOf(properties.getScore()));

            //TODO Here set card to the card at the position of deckIndex

            properties.setCurrentCard(properties.getDeck().getCards().get(properties.getDeckIndex()));

            // TODO NOT WHERE WE SHOULD DO THIS!!!! CLEAN ME UP LATER!!!!!!!!!!!!!!!!!!!!!
            if(Boolean.parseBoolean(properties.getCurrentCard().getModeratorNeeded())){
                properties.setDeckIndex(properties.getDeckIndex()+1);
                return handleNextCard(gamePlayActivity,properties);
            }

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
        properties.getDataSource().open();
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
