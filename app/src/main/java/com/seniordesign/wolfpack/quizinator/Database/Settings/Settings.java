package com.seniordesign.wolfpack.quizinator.Database.Settings;

/**
 * Represents applications settings for the game.
 * @creation 10/4/2016.
 */
public class Settings {

    private int numberOfConntections;
    private String userName;
    private long id;

    /*
     * @author kuczynskij (10/5/2016)
     */
    @Override
    public String toString(){
        return "Settings id(" + id + "), userName(" + userName +
                "), numberOfConnections(" + numberOfConntections + ").";
    }

    /*
     * @author kuczynskij (10/5/2016)
     */
    public long getId() {
        return id;
    }

    /*
     * @author kuczynskij (10/5/2016)
     */
    public void setId(long id) {
        this.id = id;
    }

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
