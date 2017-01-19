package com.seniordesign.wolfpack.quizinator.Database.Deck;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @creation 10/4/2016.
 */
public class Deck {

    private long id;
    private String deckName;
    private String category;
    private String subject;
    private boolean duplicateCards;
    private String owner;
    private List<Card> cards;

//    private boolean moderatorNeeded;

    public Deck(){

    }

    public boolean addCard(Card card){
        return cards.add(card);
    }

    public boolean removeCard(Card card){
        return cards.remove(card);
    }

    public boolean shuffleDeck(){
        long seed = System.nanoTime();
        Collections.shuffle(cards, new Random(seed));
        return true;
    }

    public List<String> getCardTypes() {
        ArrayList<String> cardTypes = new ArrayList<>();
        for (Card card: cards) {
            if (!cardTypes.contains(Constants.CARD_TYPES.values()[card.getCardType()].toString())) {
                cardTypes.add(Constants.CARD_TYPES.values()[card.getCardType()].toString());
            }
        }
        return cardTypes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isDuplicateCards() {
        return duplicateCards;
    }

    public void setDuplicateCards(boolean duplicateCards) {
        this.duplicateCards = duplicateCards;
    }

    public void setDuplicateCards(String duplicateCards) {
        this.duplicateCards = Boolean.parseBoolean(duplicateCards);
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public Deck filter(Rules rules) {
        Deck filteredDeck = new Deck();
        filteredDeck.setId(id);
        filteredDeck.setDeckName(deckName);

        Type listType = new TypeToken<ArrayList<String>>(){}.getType();
        List<String> validCardTypes = new Gson().fromJson(rules.getCardTypes(), listType);

        List<Card> validCards = new ArrayList<>();
        for (Card card: cards) {
            if (validCardTypes.contains(Constants.CARD_TYPES.values()[card.getCardType()].toString()))
                validCards.add(card);
        }
        filteredDeck.setCards(validCards);

        return filteredDeck;
    }
}
