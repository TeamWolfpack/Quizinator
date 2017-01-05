package com.seniordesign.wolfpack.quizinator.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.seniordesign.wolfpack.quizinator.Adapters.CardAdapter;
import com.seniordesign.wolfpack.quizinator.Adapters.DeckAdapter;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Card.CardDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.ArrayList;
import java.util.List;

public class DecksActivity extends AppCompatActivity {

    DeckDataSource deckDataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);

        initializeDB();

        List<Deck> decks = deckDataSource.getAllDecks();
        fillListOfDecks(decks);
    }

    private boolean initializeDB(){
        deckDataSource = new DeckDataSource(this);
        return deckDataSource.open();
    }

    private void fillListOfDecks(List<Deck> values){
        final ListView listView = (ListView)findViewById(R.id.list_of_decks);
        DeckAdapter adapter = new DeckAdapter(this,
                android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);
    }
}
