package com.seniordesign.wolfpack.quizinator.database;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @creation 10/4/2016.
 */
public class Deck implements Shareable{

    private long id;
    private String deckName;
    private String category;
    private String subject;
    private boolean duplicateCards;
    private String owner;
    private List<Card> cards;
    private String uuid;

    //    private boolean moderatorNeeded;

    public Deck(){

    }

    public File toJsonFile(File dir, String fileName){
        File file = new File(dir, fileName);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write((new Gson()).toJson(this));
            fw.close();
        } catch (IOException e) {
            return null;
        }
        return file;
    }

    public Deck fromJson(String jsonCard){
        return (new Gson()).fromJson(jsonCard, Deck.class);
    }

    public Deck fromJsonFilePath(String filePath){
        try {
            return (new Gson()).fromJson(new FileReader(filePath), Deck.class);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public boolean addCard(Card card){
        return cards.add(card);
    }

    public boolean removeCard(Card card){
        return cards.remove(card);
    }

    public boolean removeCard(int index){
        return cards.remove(index)!=null;
    }

    public boolean shuffleDeck(){
        long seed = System.nanoTime();
        Collections.shuffle(cards, new Random(seed));
        return true;
    }

    public List<Constants.CARD_TYPES> getCardTypes() {
        ArrayList<Constants.CARD_TYPES> cardTypes = new ArrayList<>();
        for (Card card: cards) {
            if (!cardTypes.contains(Constants.CARD_TYPES.values()[card.getCardType()])) {
                cardTypes.add(Constants.CARD_TYPES.values()[card.getCardType()]);
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

//    public Deck filter(Rules rules) {
//        Deck filteredDeck = new Deck();
//        filteredDeck.setId(id);
//        filteredDeck.setDeckID(deckName);
//
//        Type listType = new TypeToken<ArrayList<String>>(){}.getType();
//        List<String> validCardTypes = new Gson().fromJson(rules.getCardTypes(), listType);
//
//        List<Card> validCards = new ArrayList<>();
//        for (Card card: cards) {
//            if (validCardTypes.contains(Constants.CARD_TYPES.values()[card.getCardType()].toString()))
//                validCards.add(card);
//        }
//        filteredDeck.setCards(validCards);
//
//        return filteredDeck;
//    }
}
