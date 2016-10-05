package com.seniordesign.wolfpack.quizinator.Database.Rules;

import java.sql.Time;

/**
 * @creation 10/4/2016.
 */
public class Rules {

    private int maxCardCount;
    private Time timeLimit;
    private Time cardDisplayTime;
    /*
     * Using String because going to use an Array of Strings to limit what choice
     * of Card Type in that class instead of using an enum. We thought this
     * would be easier to implement than converting an enum to a string
     * or converting and storing blobs.
     */
    private String cardTypes;

    public Rules(){

    }

    public int getMaxCardCount() {
        return maxCardCount;
    }

    public void setMaxCardCount(int maxCardCount) {
        this.maxCardCount = maxCardCount;
    }

    public Time getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Time timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Time getCardDisplayTime() {
        return cardDisplayTime;
    }

    public void setCardDisplayTime(Time cardDisplayTime) {
        this.cardDisplayTime = cardDisplayTime;
    }

    public String getCardTypes() {
        return cardTypes;
    }

    public void setCardTypes(String cardTypes) {
        this.cardTypes = cardTypes;
    }


}
