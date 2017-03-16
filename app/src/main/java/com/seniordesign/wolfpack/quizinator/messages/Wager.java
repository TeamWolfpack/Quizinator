package com.seniordesign.wolfpack.quizinator.messages;

/**
 * Created by farrowc on 3/15/2017.
 */

public class Wager {
    private String playerName;
    private String address;
    private int wager;

    public Wager(String deviceName, String address, int wager) {
        this.playerName = deviceName;
        this.address = address;
        this.wager = wager;
    }

    public String getDeviceName() {
        return playerName;
    }

    public int getWager() {
        return wager;
    }

    public String getAddress() { return address; }

}
