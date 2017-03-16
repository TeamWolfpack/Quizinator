package com.seniordesign.wolfpack.quizinator.database;

import com.seniordesign.wolfpack.quizinator.Constants;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit Test the database for Card,
 * @creation 10/16/2016
 */
public class CardTest {

    private Card tfCard = new Card();
    private Card mcCard = new Card();

    /*
    * @author  chuna (10/15/2016)
    */
    @Before
    public void init(){
        tfCard.setId(1);
        tfCard.setCardType(Constants.CARD_TYPES.TRUE_FALSE);
        tfCard.setQuestion("Test Question");
        tfCard.setCorrectAnswer("True");
        String[] possibleAnswers = new String[]{"True", "False"};
        tfCard.setPossibleAnswers(possibleAnswers);
        tfCard.setModeratorNeeded("False");
        tfCard.setPoints(1);

        mcCard.setId(2);
        mcCard.setCardType(Constants.CARD_TYPES.MULTIPLE_CHOICE);
        mcCard.setQuestion("Test Question MC");
        mcCard.setCorrectAnswer("Yes");
        possibleAnswers = new String[]{"1", "2", "Yes", "No"};
        mcCard.setPossibleAnswers(possibleAnswers);
        mcCard.setModeratorNeeded("False");
        mcCard.setPoints(3);
    }

    /*
     * @author  chuna (10/15/2016)
     */
    @Test
    public void gettersTest(){
        assertEquals(1, tfCard.getId());
        assertEquals("TF", tfCard.getCardType());
        assertEquals("Test Question", tfCard.getQuestion());
        assertEquals("True", tfCard.getCorrectAnswer());
        assertEquals(2, tfCard.getPossibleAnswers().length);
        assertEquals("False", tfCard.getModeratorNeeded());
        assertEquals(1, tfCard.getPoints());

        assertEquals(2, mcCard.getId());
        assertEquals("MC", mcCard.getCardType());
        assertEquals("Test Question MC", mcCard.getQuestion());
        assertEquals("Yes", mcCard.getCorrectAnswer());
        assertEquals(4, mcCard.getPossibleAnswers().length);
        assertEquals("False", mcCard.getModeratorNeeded());
        assertEquals(3, mcCard.getPoints());
    }

    /*
     * @author  chuna (10/15/2016)
     */
    @Test
    public void toStringTest(){
        String tfString = "TF | Test Question | 1";
        String mcString = "MC | Test Question MC | 3";
        assertEquals(tfString, tfCard.toString());
        assertEquals(mcString, mcCard.toString());
    }
}
