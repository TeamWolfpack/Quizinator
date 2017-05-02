package com.seniordesign.wolfpack.quizinator.messages;

import android.support.v4.util.Pair;

import java.util.Map;

public class EndOfGameMessage {

    private Map<String, Pair<String, Integer>> playerScores;
    private long gameTime;

    public EndOfGameMessage(Map<String, Pair<String, Integer>> playerScores, long gameTime) {
        this.playerScores = playerScores;
        this.gameTime = gameTime;
    }

    public Map<String, Pair<String, Integer>> getPlayerScores() {
        return playerScores;
    }

    public long getGameTime() {
        return gameTime;
    }
}
