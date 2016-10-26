package com.seniordesign.wolfpack.quizinator.Database;

import com.seniordesign.wolfpack.quizinator.Database.Card.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit Test the database for Card, TFCard, and MCCard
 * @creation 10/16/2016
 */
public class CardTest {

    private Card tfCard = new TFCard();
    private Card mcCard = new MCCard();

    /*
    * @author  chuna (10/15/2016)
    */
    @Before
    public void init(){
        tfCard.setId(1);
        tfCard.setCardType("TF");
        tfCard.setQuestion("Test Question");
        tfCard.setCorrectAnswer("True");
        String[] possibleAnswers = new String[]{"True", "False"};
        tfCard.setPossibleAnswers(possibleAnswers);
        tfCard.setModeratorNeeded("False");
        tfCard.setPoints(1);

        mcCard.setId(2);
        mcCard.setCardType("MC");
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
        String tfString = "TF | Test Question | True, False | 1";
        String mcString = "MC | Test Question MC | 1, 2, Yes, No | 3";
        assertEquals(tfString, tfCard.toString());
        assertEquals(mcString, mcCard.toString());
    }
}