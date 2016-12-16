package com.seniordesign.wolfpack.quizinator.Messages;

public class QuizMessage {

    private int code;
    private String message;

    public QuizMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
