package com.seniordesign.wolfpack.quizinator.Database.Deck;

import com.seniordesign.wolfpack.quizinator.Database.Card.Card;

import java.sql.Time;

/**
 * @creation 10/4/2016.
 */
public class Deck {

    private long id;
    private String deckName;
    private Card[] cards;

//    private boolean moderatorNeeded;

    /*
     * @author  chuna (10-5-2016)
     */
    public Deck(){

    }

    /*
     * @author  chuna (10-7-2016)
     */
    public boolean addCard(Card card){
        return true;
    }

    /*
     * @author  chuna (10-7-2016)
     */
    public boolean removeCard(Card card){
        return true;
    }

//    /*
//     * @author  chuna (10-7-2016)
//     */
//    private void checkIfModeratorNeeded(){
//
//    }

    /*
     * @author  chuna (10-5-2016)
     */
    public long getId() {
        return id;
    }

    /*
     * @author  chuna (10-5-2016)
     */
    public void setId(long id) {
        this.id = id;
    }

    /*
     * @author  chuna (10-11-2016)
     */
    public String getDeckName() {
        return deckName;
    }

    /*
     * @author  chuna (10-11-2016)
     */
    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    /*
     * @author  chuna (10-11-2016)
     */
    public Card[] getCards() {
        return cards;
    }

    /*
     * @author  chuna (10-11-2016)
     */
    public void setCards(Card[] cards) {
        this.cards = cards;
    }
}
