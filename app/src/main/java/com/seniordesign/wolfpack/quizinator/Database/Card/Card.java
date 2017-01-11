package com.seniordesign.wolfpack.quizinator.Database.Card;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @creation    10/4/2016.
 */

public class Card{

    private long id;
    private String cardType;
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
        return cardType + " | " + question +
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

    public String getCardType() {
        return cardType;
    }

    /*
     * @author  chuna (10-13-2016)
     */

    public void setCardType(String cardType) {
        this.cardType = cardType;
        switch(cardType){
            case("TF"):
                this.maxPossibleAnswers = 2;
                break;
            case("MC"):
                this.maxPossibleAnswers = 4;
                break;
        }
    }

    /*
     * @author  chuna (10-5-2016)
     */

    public String getQuestion() {
        return question;
    }

    /*
     * @author  chuna (10-5-2016)
     */

    public void setQuestion(String question) {
        this.question = question;
    }

    /*
     * @author  chuna (10-7-2016)
     */
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    /*
     * @author  chuna (10-7-2016)
     */
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    /*
     * @author  chuna (10-11-2016)
     */
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
