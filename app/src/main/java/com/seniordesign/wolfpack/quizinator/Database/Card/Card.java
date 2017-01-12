package com.seniordesign.wolfpack.quizinator.Database.Card;

import com.google.gson.annotations.SerializedName;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES;

import java.io.Serializable;

/**
 * @creation    10/4/2016.
 */

public class Card{

    private long id;
    private int cardType;
    private String question;
    private String correctAnswer;
    private String [] possibleAnswers;
    private String moderatorNeeded;
    private int points;

    private int maxPossibleAnswers;   //Can't have over 4 options

    /*
        * @author  chuna (10-7-2016)
        */

    public String toString(){
        return CARD_TYPES.values()[cardType].toString() + " | " + question +
                " | " + points;
    }

    /*
     * @author  chuna (10-5-2016)
     */

    public long getId() {
        return id;
    }

    /*
     * @author  chuna (10-5-2016)
     */

    public void setId(long id) {
        this.id = id;
    }

    /*
     * @author  chuna (10-13-2016)
     */

    public int getCardType() {
        return cardType;
    }

    /*
     * @author  chuna (10-13-2016)
     */
    public void setCardType(CARD_TYPES cardType) {
        this.cardType = cardType.ordinal();
        switch(cardType){
            case TRUE_FALSE:
                this.maxPossibleAnswers = 2;
                break;
            case MULTIPLE_CHOICE:
                this.maxPossibleAnswers = 4;
                break;
            case FREE_RESPONSE:
                // TODO implement later
                break;
            case VERBAL_RESPONSE:
                // TODO implement later
                break;
        }
    }

    void setCardType(int cardType) {
        setCardType(CARD_TYPES.values()[cardType]);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String[] getPossibleAnswers() {
        return possibleAnswers;
    }


    public void setPossibleAnswers(String[] possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    /*
     * @author  chuna (10-5-2016)
     */

    public String getModeratorNeeded() {
        return moderatorNeeded;
    }

    /*
     * @author  chuna (10-5-2016)
     */

    public void setModeratorNeeded(String moderatorNeeded) {
        this.moderatorNeeded = moderatorNeeded;
    }

    /*
     * @author  chuna (10-5-2016)
     */

    public int getPoints() {
        return points;
    }

    /*
     * @author  chuna (10-5-2016)
     */

    public void setPoints(int points) {
        this.points = points;
    }
}
