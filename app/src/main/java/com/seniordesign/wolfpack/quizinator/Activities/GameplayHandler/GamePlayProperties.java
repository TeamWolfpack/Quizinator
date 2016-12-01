package com.seniordesign.wolfpack.quizinator.Activities.GameplayHandler;

import android.os.CountDownTimer;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScoresDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

/**
 * Created by farrowc on 11/30/2016.
 */

public class GamePlayProperties {

    private WifiDirectApp wifiDirectApp;

    private Rules rules;
    private Deck deck;
    private Card currentCard;

    private RulesDataSource rulesDataSource;
    private HighScoresDataSource highScoresDataSource;
    private DeckDataSource deckDataSource;

    private int deckIndex;
    private int deckLength;
    private int score;
    private int cardsPlayed;
    private boolean hasAnswered;

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

    private final Gson gson = new Gson();

    public Gson getGson() {
        return gson;
    }

    public WifiDirectApp getWifiDirectApp() {
        return wifiDirectApp;
    }

    public void setWifiDirectApp(WifiDirectApp wifiDirectApp) {
        this.wifiDirectApp = wifiDirectApp;
    }

    public int getCardsPlayed() {
        return cardsPlayed;
    }

    public void setCardsPlayed(int cardsPlayed) {
        this.cardsPlayed = cardsPlayed;
    }

    public boolean getHasAnswered() {
        return hasAnswered;
    }

    public void setHasAnswered(boolean hasAnswered) {
        this.hasAnswered = hasAnswered;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public CountDownTimer getGamePlayTimerStatic() {
        return gamePlayTimerStatic;
    }

    public void setGamePlayTimerStatic(CountDownTimer gamePlayTimerStatic) {
        this.gamePlayTimerStatic = gamePlayTimerStatic;
    }

    public CountDownTimer getGamePlayTimerRunning() {
        return gamePlayTimerRunning;
    }

    public void setGamePlayTimerRunning(CountDownTimer gamePlayTimerRunning) {
        this.gamePlayTimerRunning = gamePlayTimerRunning;
    }

    public long getGamePlayTimerRemaining() {
        return gamePlayTimerRemaining;
    }

    public void setGamePlayTimerRemaining(long gamePlayTimerRemaining) {
        this.gamePlayTimerRemaining = gamePlayTimerRemaining;
    }

    public CountDownTimer getCardTimerStatic() {
        return cardTimerStatic;
    }

    public void setCardTimerStatic(CountDownTimer cardTimerStatic) {
        this.cardTimerStatic = cardTimerStatic;
    }

    public CountDownTimer getCardTimerRunning() {
        return cardTimerRunning;
    }

    public void setCardTimerRunning(CountDownTimer cardTimerRunning) {
        this.cardTimerRunning = cardTimerRunning;
    }

    public CountDownTimer getCardTimerAreaBackgroundStatic() {
        return cardTimerAreaBackgroundStatic;
    }

    public void setCardTimerAreaBackgroundStatic(CountDownTimer cardTimerAreaBackgroundStatic) {
        this.cardTimerAreaBackgroundStatic = cardTimerAreaBackgroundStatic;
    }

    public CountDownTimer getCardTimerAreaBackgroundRunning() {
        return cardTimerAreaBackgroundRunning;
    }

    public void setCardTimerAreaBackgroundRunning(CountDownTimer cardTimerAreaBackgroundRunning) {
        this.cardTimerAreaBackgroundRunning = cardTimerAreaBackgroundRunning;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public Rules getRules() {
        return rules;
    }

    public void setRules(Rules rules) {
        this.rules = rules;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
    }

    public RulesDataSource getRulesDataSource() {
        return rulesDataSource;
    }

    public void setRulesDataSource(RulesDataSource rulesDataSource) {
        this.rulesDataSource = rulesDataSource;
    }

    public HighScoresDataSource getHighScoresDataSource() {
        return highScoresDataSource;
    }

    public void setHighScoresDataSource(HighScoresDataSource highScoresDataSource) {
        this.highScoresDataSource = highScoresDataSource;
    }

    public DeckDataSource getDeckDataSource() {
        return deckDataSource;
    }

    public void setDeckDataSource(DeckDataSource deckDataSource) {
        this.deckDataSource = deckDataSource;
    }

    public int getDeckIndex() {
        return deckIndex;
    }

    public void setDeckIndex(int deckIndex) {
        this.deckIndex = deckIndex;
    }

    public int getDeckLength() {
        return deckLength;
    }

    public void setDeckLength(int deckLength) {
        this.deckLength = deckLength;
    }
}
