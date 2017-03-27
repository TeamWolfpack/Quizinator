package com.seniordesign.wolfpack.quizinator.database;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Card implements Shareable{

    private long id;
    private int cardType;
    private String question;
    private String correctAnswer;
    private String [] possibleAnswers;
    private String moderatorNeeded;
    private int points = 1;
    private boolean doubleEdge;

    private int maxPossibleAnswers;   //Can't have over 4 options

    public String toString(){
        return CARD_TYPES.values()[cardType].toString() + " | " + question +
                " | " + points;
    }

    public boolean toJsonFile(String filePath){
        try {
            FileWriter fw = new FileWriter(filePath + "/" + id);
            fw.write((new Gson()).toJson(this));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Card fromJson(String jsonCard){
        return (new Gson()).fromJson(jsonCard, Card.class);
    }

    public Card fromJsonFile(String filePath){
        try {
            return (new Gson()).fromJson(new FileReader(filePath), Card.class);
        } catch (FileNotFoundException e) {
            return null;
        }
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
                setModeratorNeeded(String.valueOf(false));
                break;
            case MULTIPLE_CHOICE:
                this.maxPossibleAnswers = 4;
                setModeratorNeeded(String.valueOf(false));
                break;
            case FREE_RESPONSE:
                setModeratorNeeded(String.valueOf(true));
                break;
            case VERBAL_RESPONSE:
                setModeratorNeeded(String.valueOf(true));
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

    public void setDoubleEdge(boolean doubleEdge){
        this.doubleEdge = doubleEdge;
    }

    public boolean isDoubleEdge(){
        return doubleEdge;
    }
}
