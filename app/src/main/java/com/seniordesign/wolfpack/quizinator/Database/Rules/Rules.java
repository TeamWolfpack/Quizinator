package com.seniordesign.wolfpack.quizinator.Database.Rules;

/**
 * Represents rules
 * @creation 10/4/2016.
 */
public class Rules {

    private long id;
    private int maxCardCount;
    private long timeLimit;
    private long cardDisplayTime;
    /*
     * Using String because going to use an Array of Strings to limit what choice
     * of Card Type in that class instead of using an enum. We thought this
     * would be easier to implement than converting an enum to a string
     * or converting and storing blobs.
     */
    private String cardTypes;

    /*
     * @author kuczynskij (10/5/2016)
     */
    @Override
    public String toString(){
        return "Rules id(" + id + "), maxCardCount(" + maxCardCount +
                "), timeLimit(" + timeLimit + "), cardDisplayTime(" +
                cardDisplayTime + "), cardTypes(" + cardTypes + ").";
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

    public int getMaxCardCount() {
        return maxCardCount;
    }

    public void setMaxCardCount(int maxCardCount) {
        this.maxCardCount = maxCardCount;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public long getCardDisplayTime() {
        return cardDisplayTime;
    }

    public void setCardDisplayTime(long cardDisplayTime) {
        this.cardDisplayTime = cardDisplayTime;
    }

    public String getCardTypes() {
        return cardTypes;
    }

    public void setCardTypes(String cardTypes) {
        this.cardTypes = cardTypes;
    }
}
