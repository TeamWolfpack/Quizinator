package com.seniordesign.wolfpack.quizinator.Database;

import com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES;

public class Card{

    private long id;
    private int cardType;
    private String question;
    private String correctAnswer;
    private String [] possibleAnswers;
    private String moderatorNeeded;
    private int points = 1;

    private int maxPossibleAnswers;   //Can't have over 4 options

    public String toString(){
        return CARD_TYPES.values()[cardType].toString() + " | " + question +
                " | " + points;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCardType() {
        return cardType;
    }

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
                moderatorNeeded = String.valueOf(true);
                break;
            case VERBAL_RESPONSE:
                moderatorNeeded = String.valueOf(true);
                break;
        }
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

    public String getModeratorNeeded() {
        return moderatorNeeded;
    }

    public void setModeratorNeeded(String moderatorNeeded) {
        this.moderatorNeeded = moderatorNeeded;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
