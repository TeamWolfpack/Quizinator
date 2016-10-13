package com.seniordesign.wolfpack.quizinator;

import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScores;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScoresDataSource;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScoresSQLiteHelper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the database folder HighScore
 * @creation 10/10/2016
 */
public class DatabaseHighScoreTest {

    private HighScores h1 = new HighScores();
    private HighScores h2 = new HighScores();
    private HighScores h3 = new HighScores();

    @Before
    private void init(){
        h1.setDeckName("Sample");
        h1.setBestTime(350000);//35 seconds-ish
        h1.setBestScore(650);

        h2.setDeckName("");
        h2.setBestTime(350000);//35 seconds-ish
        h2.setBestScore(650);

        h3.setDeckName("Sample");
        h3.setBestTime(350000);//35 seconds-ish
        h3.setBestScore(-1);
    }

    @Test
    public void normalFlow_HighScores() throws Exception{

    }
}
