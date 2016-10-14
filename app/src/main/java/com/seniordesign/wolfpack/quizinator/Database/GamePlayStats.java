package com.seniordesign.wolfpack.quizinator.Database;

/**
 * Represents game play stats
 * @creation 10/14/2016.
 */
public class GamePlayStats {
    private int score;
    private long timeElapsed;
    private int totalCardsCompleted;

    /*
     * @author kuczynskij (10/14/2016)
     */
    public int getTotalCardsCompleted() {
        return totalCardsCompleted;
    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    public void setTotalCardsCompleted(int totalCardsCompleted) {
        this.totalCardsCompleted = totalCardsCompleted;
    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    public long getTimeElapsed() {
        return timeElapsed;
    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    public int getScore() {
        return score;
    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    public void setScore(int score) {
        this.score = score;
    }
}
