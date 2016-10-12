package com.seniordesign.wolfpack.quizinator.Database.Card;

/**
 * @creation    10/7/2016
 */

public class TFCard extends Card {

    private long id;
    private boolean moderatorNeeded;
    private int points;
    private String question;
    private String correctAnswer;
    private final String [] possibleAnswers = new String[]{"True", "False"};

    /*
     * @author  chuna (10-7-2016)
     */
    @Override
    public String toString(){
        return null;
    }

    /*
     * @author  chuna (10-5-2016)
     */
    @Override
    public long getId() {
        return id;
    }

    /*
     * @author  chuna (10-5-2016)
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }

    /*
     * @author  chuna (10-5-2016)
     */
    @Override
    public boolean getModeratorNeeded() {
        return moderatorNeeded;
    }

    /*
     * @author  chuna (10-5-2016)
     */
    @Override
    public void setModeratorNeeded(boolean moderatorNeeded) {
        //this.moderatorNeeded = moderatorNeeded;
        /* TODO may need to change because doubt we want them to change the value of
         * this attribute since TF should not need a moderator.
         */
    }

    /*
     * @author  chuna (10-5-2016)
     */
    @Override
    public int getPoints() {
        return points;
    }

    /*
     * @author  chuna (10-5-2016)
     */
    @Override
    public void setPoints(int points) {
        this.points = points;
    }

    /*
     * @author  chuna (10-5-2016)
     */
    @Override
    public String getQuestion() {
        return question;
    }

    /*
     * @author  chuna (10-5-2016)
     */
    @Override
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
}
