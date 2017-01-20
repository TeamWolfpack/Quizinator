package com.seniordesign.wolfpack.quizinator.Database;

/**
 * Created by aaron on 1/10/2017.
 */

public class CardDeckRelation {

    private long id;
    private long fkCard;
    private long fkDeck;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFkCard() {
        return fkCard;
    }

    public void setFkCard(long fkCard) {
        this.fkCard = fkCard;
    }

    public long getFkDeck() {
        return fkDeck;
    }

    public void setFkDeck(long fkDeck) {
        this.fkDeck = fkDeck;
    }
}
