package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Adapters.CardAdapter;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.List;

public class EditDeckActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener{

    Deck deck;
    List<Card> cardsAvailable;
    CardAdapter deckCardAdapter;
    CardAdapter totalCardAdapter;

    QuizDataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);
        String jsonDeck = getIntent().getStringExtra("Deck");
        Gson gson = new Gson();
        deck = gson.fromJson(jsonDeck,Deck.class);

        initializeDB();
        cardsAvailable = dataSource.getAllCards();

        populateMenus(deck, cardsAvailable);
    }

    private boolean initializeDB(){
        dataSource = new QuizDataSource(this);
        return dataSource.open() && dataSource.open();
    }

    private void populateMenus(Deck deck, List<Card> cardsAvailable){
        EditText deckName = (EditText) findViewById(R.id.edit_deck_name);
        deckName.setText(deck.getDeckName());
        final ListView deckCardList = (ListView)findViewById(R.id.cards_in_deck);
        deckCardAdapter = new CardAdapter(this,
                android.R.layout.simple_list_item_1, deck.getCards());
        deckCardList.setAdapter(deckCardAdapter);
        deckCardList.setOnItemClickListener(this);
        final ListView totalCardList = (ListView)findViewById(R.id.cards_in_database);
        totalCardAdapter = new CardAdapter(this,
                android.R.layout.simple_list_item_1, cardsAvailable);
        totalCardList.setAdapter(totalCardAdapter);
        totalCardList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.cards_in_deck){
            //Remove card from deck
            deck.removeCard(deck.getCards().get(position));
        }
        else{
            //Remove card from deck
            deck.addCard(cardsAvailable.get(position));

        }
        deckCardAdapter.notifyDataSetChanged();
    }

    public void onSaveClick(View view){
        String deckName = ((EditText)findViewById(R.id.edit_deck_name)).getText().toString();
        deck.setDeckName(deckName);
        dataSource.updateDeck(deck);
        finish();
    }

    public void onCancelClick(View view){
        finish();
    }

    public void onDeleteClick(View view){
        new AlertDialog.Builder(this)
                .setTitle("Deleting Deck")
                .setMessage("Are you sure you want to delete this deck?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataSource.deleteDeck(deck);
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
        dataSource.open();
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
        dataSource.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
        dataSource.close();
    }
}
