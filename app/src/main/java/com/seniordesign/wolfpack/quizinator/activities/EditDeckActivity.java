package com.seniordesign.wolfpack.quizinator.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.adapters.CardAdapter;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.database.Deck;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner;
import io.apptik.widget.multiselectspinner.MultiSelectSpinner;

import static com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES.FREE_RESPONSE;
import static com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES.MULTIPLE_CHOICE;
import static com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES.TRUE_FALSE;
import static com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES.VERBAL_RESPONSE;

public class EditDeckActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener{

    Deck deck;
    List<Card> cardsAvailable;
    CardAdapter deckCardAdapter;
    CardAdapter totalCardAdapter;

    private String sortBy;
    private MultiSelectSpinner cardTypeSpinner;
    private List<Constants.CARD_TYPES> selectedCardTypes;
    private List<Constants.CARD_TYPES> cardTypes = new ArrayList<>(
            Arrays.asList(
                    TRUE_FALSE,
                    MULTIPLE_CHOICE,
                    FREE_RESPONSE,
                    VERBAL_RESPONSE
            ));

    QuizDataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);
        String jsonDeck = getIntent().getStringExtra("Deck");
        Gson gson = new Gson();
        deck = gson.fromJson(jsonDeck,Deck.class);
        setTitle("Edit: " + deck.getDeckName());

        cardTypeSpinner = (MultiSelectSpinner) findViewById(R.id.card_type_spinner);
        selectedCardTypes = new ArrayList<>();

        initializeDB();
        initializeCardTypeSpinner();
        initializeSortBySpinner();

        //TODO possibly get rid of in future
        cardsAvailable = dataSource.filterCards(selectedCardTypes, sortBy);
        populateMenus(deck);
    }

    private boolean initializeDB(){
        dataSource = new QuizDataSource(this);
        return dataSource.open() && dataSource.open();
    }

    private Boolean initializeCardTypeSpinner(){
        ArrayAdapter<Constants.CARD_TYPES> cardTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, cardTypes);
        cardTypeSpinner
                .setListAdapter(cardTypeAdapter)
                .setAllCheckedText(Constants.ALL_CARD_TYPES)
                .setSelectAll(true)
                .setMinSelectedItems(1)
                .setListener(new BaseMultiSelectSpinner.MultiSpinnerListener() {
                    @Override
                    public void onItemsSelected(boolean[] selected) {
                        selectedCardTypes.clear();
                        for (int i = 0; i < selected.length; i++) {
                            if (selected[i]) {
                                try {
                                    selectedCardTypes.add(cardTypes.get(i));
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                        ArrayList<Constants.CARD_TYPES> chosenTypes = new ArrayList<>();
                        for (int i = 0; i < selectedCardTypes.size(); i++) {
                            chosenTypes.add(selectedCardTypes.get(i));
                        }
                        cardsAvailable = dataSource.filterCards(selectedCardTypes, sortBy);
                        populateMenus(deck);
                    }
                });
        return true;
    }

    private void initializeSortBySpinner() {
        Spinner sortBySpinner = (Spinner) findViewById(R.id.card_sort_spinner);
        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy = parent.getSelectedItem().toString();
                cardsAvailable = dataSource.filterCards(selectedCardTypes, sortBy);
                populateMenus(deck);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });
    }

    private void populateMenus(Deck deck){
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
            deck.removeCard(position);
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

        if (deck.getId() == 0) {
            dataSource.createDeck(deck);
        } else {
            dataSource.updateDeck(deck);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
        dataSource.open();
    }

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
