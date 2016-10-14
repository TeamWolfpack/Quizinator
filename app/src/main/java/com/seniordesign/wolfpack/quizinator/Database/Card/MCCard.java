package com.seniordesign.wolfpack.quizinator.Database.Card;

/**
 * @creation    10/7/2016
 */

public class MCCard extends Card {

    private long id;
    private boolean moderatorNeeded;
    private int points;
    private String question;
    private String correctAnswer;
    private String[] possibleAnswers;

    private final int maxPossibleAnswers = 4;   //Can't have over 4 options

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean getModeratorNeeded() {
        return false;
    }

    @Override
    public void setModeratorNeeded(boolean moderatorNeeded) {
        this.moderatorNeeded = moderatorNeeded;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(String[] possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
