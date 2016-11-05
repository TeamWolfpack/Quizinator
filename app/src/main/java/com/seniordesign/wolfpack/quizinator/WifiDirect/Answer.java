package com.seniordesign.wolfpack.quizinator.WifiDirect;

/**
 * Created by leonardj on 11/5/2016.
 */

public class Answer {

    private String deviceName;
    private String answer;

    public Answer(String deviceName, String answer) {
        this.deviceName = deviceName;
        this.answer = answer;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getAnswer() {
        return answer;
    }
}
