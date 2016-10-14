package com.seniordesign.wolfpack.quizinator.Database.Card;

/**
 * @creation    10/4/2016.
 */

public abstract class Card {

    protected long id;
    protected String cardType;
    protected String question;
    protected String correctAnswer;
    protected String [] possibleAnswers;
    protected String moderatorNeeded;
    protected int points;

    /*
     * @author  chuna (10-5-2016)
     */
    public abstract long getId();

    /*
     * @author  chuna (10-5-2016)
     */
    public abstract void setId(long id);

    /*
     * @author  chuna (10-13-2016)
     */
    public abstract String getCardType();

    /*
     * @author  chuna (10-13-2016)
     */
    public abstract void setCardType(String cardType);

    /*
     * @author  chuna (10-5-2016)
     */
    public abstract String getQuestion();

    /*
     * @author  chuna (10-5-2016)
     */
    public abstract void setQuestion(String question);

    /*
     * @author  chuna (10-11-2016)
     */
    public abstract String getCorrectAnswer();

    /*
     * @author  chuna (10-11-2016)
     */
    public abstract void setCorrectAnswer(String correctAnswer);

    /*
     * @author  chuna (10-11-2016)
     */
    public abstract String[] getPossibleAnswers();

    /*
     * @author  chuna (10-11-2016)
     */
    public abstract void setPossibleAnswers(String[] possibleAnswers);

    /*
     * @author  chuna (10-5-2016)
     */
    public abstract int getPoints();

    /*
     * @author  chuna (10-5-2016)
     */
    public abstract void setPoints(int points);

    /*
     * @author  chuna (10-5-2016)
     */
    public abstract String getModeratorNeeded();

    /*
     * @author  chuna (10-5-2016)
     */
    public abstract void setModeratorNeeded(String moderatorNeeded);
}
