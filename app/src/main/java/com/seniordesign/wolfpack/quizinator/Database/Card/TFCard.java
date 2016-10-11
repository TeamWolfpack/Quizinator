package com.seniordesign.wolfpack.quizinator.Database.Card;

/**
 * @creation    10/7/2016
 */

public class TFCard extends Card {

    private long id;
    private boolean moderatorNeeded;
    private int points;
    private String question;
    private boolean answer;

    @Override
    public String toString(){
        return null;
    }

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
        return moderatorNeeded;
    }

    @Override
    public void setModeratorNeeded(boolean moderatorNeeded) {
        this.moderatorNeeded = moderatorNeeded;
        /* TODO may need to change because doubt we want them to change the value of
         * this attribute since TF should not need a moderator.
         */
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

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }
}
