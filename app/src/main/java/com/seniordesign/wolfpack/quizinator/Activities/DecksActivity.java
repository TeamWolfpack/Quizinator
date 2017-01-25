package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Adapters.DeckAdapter;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Database.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck;
import com.seniordesign.wolfpack.quizinator.Database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.ArrayList;
import java.util.List;

public class DecksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    QuizDataSource dataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);
        setTitle(Constants.DECKS);

        initializeDB();

        List<Deck> decks = dataSource.getAllDecks();
        fillListOfDecks(decks);
    }

    private boolean initializeDB(){
        dataSource = new QuizDataSource(this);
        return dataSource.open();
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
        String jsonDeck = gson.toJson(dataSource.getAllDecks().get(position));
        intent.putExtra("Deck",jsonDeck);
        startActivity(intent);
    }

    public void newDeckClick(View view){
        Intent intent = new Intent(this, EditDeckActivity.class);
        Gson gson = new Gson();
        Deck deck = dataSource.createDeck("New Deck", null, null, true, null, new ArrayList<Card>());
        String jsonDeck = gson.toJson(deck);
        intent.putExtra("Deck",jsonDeck);
        startActivity(intent);
    }

    protected void onResume() {
        super.onResume();
        dataSource.open();
        List<Deck> decks = dataSource.getAllDecks();
        fillListOfDecks(decks);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

}
