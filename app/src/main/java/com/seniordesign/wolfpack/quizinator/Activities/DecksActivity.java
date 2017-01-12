package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Adapters.CardAdapter;
import com.seniordesign.wolfpack.quizinator.Adapters.DeckAdapter;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Card.CardDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DecksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

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
        listView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Intent intent = new Intent(this, EditDeckActivity.class);
        Gson gson = new Gson();
        String jsonDeck = gson.toJson(deckDataSource.getAllDecks().get(position));
        intent.putExtra("Deck",jsonDeck);
        startActivity(intent);
    }

    public void newDeckClick(View view){
        Intent intent = new Intent(this, EditDeckActivity.class);
        Gson gson = new Gson();
        Deck deck = deckDataSource.createDeck("New Deck",new ArrayList<Card>());
        String jsonDeck = gson.toJson(deck);
        intent.putExtra("Deck",jsonDeck);
        startActivity(intent);
    }

    protected void onResume() {
        super.onResume();
        deckDataSource.open();
        List<Deck> decks = deckDataSource.getAllDecks();
        fillListOfDecks(decks);
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    @Override
    protected void onPause() {
        super.onPause();
        deckDataSource.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deckDataSource.close();
    }

}
