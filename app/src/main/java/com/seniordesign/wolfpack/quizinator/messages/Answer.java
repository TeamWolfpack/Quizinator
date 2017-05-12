package com.seniordesign.wolfpack.quizinator.messages;

public class Answer {

    private String playerName;
    private String ip;
    private String macAddress;
    private String answer;
    private long timeTaken;

    public Answer(String deviceName, String ipAddress, String address, String answer, long timeTaken) {
        this.playerName = deviceName;
        this.ip = ipAddress;
        this.macAddress = address;
        this.answer = answer;
        this.timeTaken = timeTaken;
    }

    public String getDeviceName() {
        return playerName;
    }

    public String getAnswer() {
        return answer;
    }

    public String getIpAddress() { return ip; }

    public String getMacAddress() { return macAddress; }

    public long getTimeTaken() { return timeTaken; }
}
