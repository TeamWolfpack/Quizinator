package com.seniordesign.wolfpack.quizinator.Database.Settings;

/**
 * Represents applications settings for the game.
 * @creation 10/4/2016.
 */
public class Settings {

    private int numberOfConntections;
    private String userName;

    /*
     * @author kuczynskij (10/4/2016)
     */
    public int getNumberOfConntections() {
        return numberOfConntections;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public void setNumberOfConntections(int numberOfConntections) {
        this.numberOfConntections = numberOfConntections;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public String getUserName() {
        return userName;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
