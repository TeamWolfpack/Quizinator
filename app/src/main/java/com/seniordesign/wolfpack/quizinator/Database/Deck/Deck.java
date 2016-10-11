package com.seniordesign.wolfpack.quizinator.Database.Deck;

import com.seniordesign.wolfpack.quizinator.Database.Card.Card;

import java.sql.Time;

/**
 * @creation 10/4/2016.
 */
public class Deck {

    private long id;

    /*
     * Using String because going to use an Array of Strings to limit what choice
     * of Card Type in that class instead of using an enum. We thought this
     * would be easier to implement than converting an enum to a string
     * or converting and storing blobs.
     */
    private String cardTypes[];

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

    public String[] getCardTypes() {
        return cardTypes;
    }

    public void setCardTypes(String[] cardTypes) {
        this.cardTypes = cardTypes;
    }
}
