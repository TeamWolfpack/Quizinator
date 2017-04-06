package com.seniordesign.wolfpack.quizinator.database;

/**
 * Represents high scores
 * @creation 10/4/2016.
 */
public class HighScores {

    private long deckID;
    private long bestTime;
    private int bestScore;
    private long id;

    /*
     * @author kuczynskij (10/10/2016)
     */
    @Override
    public String toString(){
        return "Rules id(" + id + "), deckID(" + deckID +
                "), bestTime(" + bestTime + "), bestScore(" +
                bestScore + ").";
    }

    /*
     * @author farrowc (10/11/2016)
     */
    public String toFormattedString(){
        return "Score: " + bestScore + " Time:" + bestTime;
    }

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
    public long getDeckID() {
        return deckID;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public void setDeckID(long deckID) {
        this.deckID = deckID;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public long getBestTime() {
        return bestTime;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public void setBestTime(long bestTime) {
        this.bestTime = bestTime;
    }
}
