package com.seniordesign.wolfpack.quizinator.WifiDirect;

/**
 * Created by leonardj on 11/5/2016.
 */

public class Answer {

    private String playerName;
    private String address;
    private String answer;

    public Answer(String deviceName, String address, String answer) {
        this.playerName = deviceName;
        this.address = address;
        this.answer = answer;
    }

    public String getDeviceName() {
        return playerName;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAddress() { return address; }
}
