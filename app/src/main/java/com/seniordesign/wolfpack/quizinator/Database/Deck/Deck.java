package com.seniordesign.wolfpack.quizinator.Database.Deck;

import com.seniordesign.wolfpack.quizinator.Database.Card.Card;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @creation 10/4/2016.
 */
public class Deck {

    private long id;
    private String deckName;
    private List<Card> cards;

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
        return cards.add(card);
    }

    /*
     * @author  chuna (10-7-2016)
     */
    public boolean removeCard(Card card){
        return cards.remove(card);
    }

    /*
     * @author  chuna (10-14-2016)
     */
    public boolean shuffleDeck(){
        long seed = System.nanoTime();
        Collections.shuffle(cards, new Random(seed));
        return true;
    }

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
    public List<Card> getCards() {
        return cards;
    }

    /*
     * @author  chuna (10-11-2016)
     */
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
