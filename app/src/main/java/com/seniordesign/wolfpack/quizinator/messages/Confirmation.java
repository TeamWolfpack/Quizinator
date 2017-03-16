package com.seniordesign.wolfpack.quizinator.messages;

/**
 * Created by leonardj on 12/5/2016.
 */

public class Confirmation {

    private String clientAddress;
    private boolean confirmation;

    public Confirmation(String clientAddress, boolean confirmation) {
        this.clientAddress = clientAddress;
        this.confirmation = confirmation;
    }

    public boolean getConfirmation() {
        return confirmation;
    }

    public String getClientAddress() { return clientAddress; }
}
