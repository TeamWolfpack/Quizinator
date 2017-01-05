package com.seniordesign.wolfpack.quizinator.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Adapters.CardAdapter;
import com.seniordesign.wolfpack.quizinator.Adapters.DeckAdapter;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Card.CardDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.List;

public class EditDeckActivity extends AppCompatActivity {

    Deck deck;
    List<Card> cardsAvailable;
    CardAdapter deckCardAdapter;
    CardAdapter totalCardAdapter;
    CardDataSource cardDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);
        String jsonDeck = getIntent().getStringExtra("Deck");
        Gson gson = new Gson();
        deck = gson.fromJson(jsonDeck,Deck.class);

        initializeDB();
        cardsAvailable = cardDataSource.getAllCards();

        populateMenus(deck, cardsAvailable);
    }

    private boolean initializeDB(){
        cardDataSource = new CardDataSource(this);
        return cardDataSource.open();
    }

    private void populateMenus(Deck deck, List<Card> cardsAvailable){
        EditText deckName = (EditText) findViewById(R.id.edit_deck_name);
        deckName.setText(deck.getDeckName());
        final ListView deckCardList = (ListView)findViewById(R.id.cards_in_deck);
        deckCardAdapter = new CardAdapter(this,
                android.R.layout.simple_list_item_1, deck.getCards());
        deckCardList.setAdapter(deckCardAdapter);
        final ListView totalCardList = (ListView)findViewById(R.id.cards_in_database);
        totalCardAdapter = new CardAdapter(this,
                android.R.layout.simple_list_item_1, cardsAvailable);
        totalCardList.setAdapter(totalCardAdapter);
    }
}
