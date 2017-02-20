package com.seniordesign.wolfpack.quizinator.Database;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @creation 10/11/2016.
 */
public class SettingsTest {

    private Settings s1 = new Settings();

    /*
     * @author kuczynskij (10/12/2016)
     */
    @Before
    public void init(){
        s1.setNumberOfConntections(3);
        s1.setUserName("Jim");//35 seconds-ish
        s1.setId(1);
    }

    /*
     * @author kuczynskij (10/12/2016)
     */
    @Test
    public void normalFlow_Settings() throws Exception{
        String s = "Settings id(1), userName(Jim), numberOfConnections(3).";
        assertEquals("Jim", s1.getUserName());
        assertEquals(3, s1.getNumberOfConntections());
        assertEquals(1, s1.getId());
        assertEquals(s, s1.toString());
    }
}
