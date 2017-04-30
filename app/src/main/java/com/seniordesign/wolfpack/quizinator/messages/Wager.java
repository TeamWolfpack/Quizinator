package com.seniordesign.wolfpack.quizinator.messages;

public class Wager {
    private String playerName;
    private String ip;
    private String macAddress;
    private int wager;

    public Wager(String deviceName, String ipAddress, String macAddress, int wager) {
        this.playerName = deviceName;
        this.ip = ipAddress;
        this.macAddress = macAddress;
        this.wager = wager;
    }

    public String getDeviceName() {
        return playerName;
    }

    public int getWager() {
        return wager;
    }

    public String getIpAddress() { return ip; }

    public String getMacAddress() { return macAddress; }

}
