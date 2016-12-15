package com.seniordesign.wolfpack.quizinator.WifiDirect;

/**
 * Created by leonardj on 11/5/2016.
 */

public class Answer {

    private String playerName;
    private String address;
    private String answer;
    private long timeTaken;

    public Answer(String deviceName, String address, String answer, long timeTaken) {
        this.playerName = deviceName;
        this.address = address;
        this.answer = answer;
        this.timeTaken = timeTaken;
    }

    public String getDeviceName() {
        return playerName;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAddress() { return address; }

    public long getTimeTaken() { return timeTaken; }
}
