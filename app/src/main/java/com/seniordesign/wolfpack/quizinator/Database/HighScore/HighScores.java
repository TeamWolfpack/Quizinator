package com.seniordesign.wolfpack.quizinator.Database.HighScore;

import java.sql.Time;

/**
 * @creation 10/4/2016.
 */
public class HighScores {

    private String deckName;
    private Time bestTime;
    private int bestScore;
    private long id;

    /*
     * @author kuczynskij (10/5/2016)
     */
    public long getId() {
        return id;
    }

    /*
     * @author kuczynskij (10/5/2016)
     */
    public void setId(long id) {
        this.id = id;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public int getBestScore() {
        return bestScore;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public String getDeckName() {
        return deckName;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public Time getBestTime() {
        return bestTime;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public void setBestTime(Time bestTime) {
        this.bestTime = bestTime;
    }
}
