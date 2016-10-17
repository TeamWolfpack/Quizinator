package com.seniordesign.wolfpack.quizinator.Database;

import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Card.MCCard;
import com.seniordesign.wolfpack.quizinator.Database.Card.TFCard;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit Test the database for Deck
 * @creation 10/15/2016
 */
public class DeckTest {

    private Deck deck = new Deck();

    /*
     * @author  chuna (10/15/2016)
     */
    @Before
    public void init(){
        deck.setId(1);
        deck.setDeckName("TestDeck");
        List<Card> cards = new ArrayList<>();
        Card c1 = new TFCard();
        Card c2 = new MCCard();
        Card c3 = new MCCard();
        Card c4 = new TFCard();
        Card c5 = new TFCard();
        Card c6 = new MCCard();
        c1.setId(1);
        c2.setId(2);
        c3.setId(3);
        c4.setId(4);
        c5.setId(5);
        c6.setId(6);
        cards.add(c1);
        cards.add(c2);
        cards.add(c3);
        cards.add(c4);
        cards.add(c5);
        cards.add(c6);
        deck.setCards(cards);
    }

    /*
     * @author  chuna (10/15/2016)
     */
    @Test
    public void gettersTest(){
        assertEquals(1, deck.getId());
        assertEquals("TestDeck", deck.getDeckName());
        assertEquals(6, deck.getCards().size());
    }

    /*
     * @author  chuna (10/15/2016)
     */
    @Test
    public void addCardTest(){
        Card c7 = new TFCard();
        c7.setId(7);
        deck.addCard(c7);
        assertEquals(7, deck.getCards().size());
    }

    /*
     * @author  chuna (10/15/2016)
     */
    @Test
    public void removeCardTest(){
        Card cardToRemove = deck.getCards().get(0);
        Card newFirstCard = deck.getCards().get(1);
        deck.removeCard(cardToRemove);
        assertEquals(5, deck.getCards().size());
        assertEquals(newFirstCard, deck.getCards().get(0));
    }

    /*
     * @author  chuna (10/15/2016)
     */
    @Test
    public void shuffleTest(){
        List<Card> startCards = new ArrayList<>(deck.getCards());
        deck.shuffleDeck();
        List<Card> endCards = deck.getCards();
        for (int i = 0; i < endCards.size(); i++) {
            if (endCards.get(i).getId() != startCards.get(i).getId()) {
                assertTrue(true);
                return;
            }
        }
        fail();
    }
}
