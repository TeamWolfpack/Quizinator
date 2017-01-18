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
    private long deckId;
    /*
     * Using String because going to use an Array of Strings to limit what choice
     * of Card Type in that class instead of using an enum. We thought this
     * would be easier to implement than converting an enum to a string
     * or converting and storing blobs.
     */
    private String cardTypes;

    @Override
    public String toString(){
        return "Rules id(" + id + "), maxCardCount(" + maxCardCount +
                "), timeLimit(" + timeLimit + "), cardDisplayTime(" +
                cardDisplayTime + "), cardTypes(" + cardTypes + "), deckId(" +
                deckId + ").";
    }

    public long getId() {
        return id;
    }

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

    public long getDeckId() { return deckId; }

    public void setDeckId(int deckId) { this.deckId = deckId; }
}
