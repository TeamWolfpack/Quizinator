package com.seniordesign.wolfpack.quizinator.Database.Card;

/**
 * @creation    10/4/2016.
 */

public abstract class Card {

    protected long id;
    protected boolean moderatorNeeded;
    protected int points;
    protected String question;
    protected String correctAnswer;
    protected String [] possibleAnswers;

    /*
     * @author  chuna (10-5-2016)
     */
    public abstract long getId();

    /*
     * @author  chuna (10-5-2016)
     */
    public abstract void setId(long id);

    /*
     * @author  chuna (10-7-2016)
     */
    public abstract boolean getModeratorNeeded();

    /*
     * @author  chuna (10-7-2016)
     */
    public abstract void setModeratorNeeded(boolean moderatorNeeded);

    public abstract int getPoints();

    public abstract void setPoints(int points);

    public abstract String getQuestion();

    public abstract void setQuestion(String question);

    /*
     * @author  chuna (10-11-2016)
     */
    public boolean isModeratorNeeded() {
        return moderatorNeeded;
    }

    /*
     * @author  chuna (10-11-2016)
     */
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    /*
     * @author  chuna (10-11-2016)
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

    /*
     * @author  chuna (10-11-2016)
     */
    public void setPossibleAnswers(String[] possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }
}
