package com.seniordesign.wolfpack.quizinator.database;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the database folder HighScore
 * @creation 10/10/2016
 */
public class HighScoreTest {

    private HighScores h1 = new HighScores();
    private HighScores h2 = new HighScores();
    private HighScores h3 = new HighScores();

    /*
     * @author kuczynskij (10/12/2016)
     */
    @Before
    public void init(){
        h1.setDeckID(0);
        h1.setBestTime(350000);//35 seconds-ish
        h1.setBestScore(650);
        h1.setId(1);

        h2.setDeckID(1);
        h2.setBestTime(350000);//35 seconds-ish
        h2.setBestScore(650);

        h3.setDeckID(0);
        h3.setBestTime(350000);//35 seconds-ish
        h3.setBestScore(-1);
    }

    /*
     * @author kuczynskij (10/12/2016)
     */
    @Test
    public void normalFlow_HighScores() throws Exception{
        String s = "Rules id(1), deckName(Sample), bestTime(350000), " +
                "bestScore(650).";
        assertEquals("Sample", h1.getDeckID());
        assertEquals(350000, h1.getBestTime());
        assertEquals(650, h1.getBestScore());
        assertEquals(1, h1.getId());
        assertEquals(s, h1.toString());
    }
}
