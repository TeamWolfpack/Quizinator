package com.seniordesign.wolfpack.quizinator.database;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @creation 10/11/2016.
 */
public class RulesTest {

    private Rules r1 = new Rules();

    /*
     * @author kuczynskij (10/12/2016)
     */
    @Before
    public void init(){
        r1.setMaxCardCount(5);
        r1.setTimeLimit(350000);//35 seconds-ish
        r1.setCardDisplayTime(350000);
        r1.setCardTypes("true/false");
        r1.setId(1);
    }

    /*
     * @author kuczynskij (10/12/2016)
     */
    @Test
    public void normalFlow_Rules() throws Exception{
        String s = "Rules id(1), maxCardCount(5), timeLimit(350000), " +
                "cardDisplayTime(350000), cardTypes(true/false).";
        assertEquals(5, r1.getMaxCardCount());
        assertEquals(350000, r1.getTimeLimit());
        assertEquals(350000, r1.getCardDisplayTime());
        assertEquals("true/false", r1.getCardTypes());
        assertEquals(1, r1.getId());
        assertEquals(s, r1.toString());
    }
}
