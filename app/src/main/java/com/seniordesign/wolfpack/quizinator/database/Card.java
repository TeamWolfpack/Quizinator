package com.seniordesign.wolfpack.quizinator.database;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES;

import java.io.File;
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
    private String uuid;
    private long lastModified;

    private int maxPossibleAnswers;   //Can't have over 4 options

    public String toString(){
        return CARD_TYPES.values()[cardType].toString() + " | " + question +
                " | " + points;
    }

    public File toJsonFile(File dir, String fileName){
        File file = new File(dir, fileName);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write((new Gson()).toJson(this));
            fw.close();
        } catch (IOException e) {
            return null;
        }
        return file;
    }

    public Card fromJson(String jsonCard){
        return (new Gson()).fromJson(jsonCard, Card.class);
    }

    public Card fromJsonFilePath(String filePath){
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}
