package com.seniordesign.wolfpack.quizinator.Database.Card;

/**
 * @creation    10/7/2016
 */

public class MCCard extends Card {

    private long id;
    private String cardType;
    private String question;
    private String correctAnswer;
    private String [] possibleAnswers;
    private String moderatorNeeded = "False";
    private int points;

    private final int maxPossibleAnswers = 4;   //Can't have over 4 options

    /*
        * @author  chuna (10-7-2016)
        */
    @Override
    public String toString(){
        return cardType + " | " + question + " | " +
                possibleAnswers[0] + ", " + possibleAnswers[1] +
                possibleAnswers[2] + ", " + possibleAnswers[3] +
                " | " + points;
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
     * @author  chuna (10-13-2016)
     */
    @Override
    public String getCardType() {
        return cardType;
    }

    /*
     * @author  chuna (10-13-2016)
     */
    @Override
    public void setCardType(String cardType) {
        this.cardType = cardType;
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

    @Override
    public void setPossibleAnswers(String[] possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    /*
     * @author  chuna (10-5-2016)
     */
    @Override
    public String getModeratorNeeded() {
        return moderatorNeeded;
    }

    /*
     * @author  chuna (10-5-2016)
     */
    @Override
    public void setModeratorNeeded(String moderatorNeeded) {
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
}
