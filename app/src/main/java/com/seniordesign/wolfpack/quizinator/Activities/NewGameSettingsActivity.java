package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Database.Deck;
import com.seniordesign.wolfpack.quizinator.Database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.Filters.NumberFilter;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner;
import io.apptik.widget.multiselectspinner.MultiSelectSpinner;

import static com.seniordesign.wolfpack.quizinator.Constants.*;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.*;


/*
 * The new game settings activity is...
 * @creation 09/28/2016
 */
public class NewGameSettingsActivity extends AppCompatActivity {

    private static final String TAG = "ACT_NGS";

    private EditText cardCountInput;
    private EditText gameMinutesInput;
    private EditText gameSecondsInput;
    private EditText cardMinutesInput;
    private EditText cardSecondsInput;

    private Spinner deckSpinner;

    private MultiSelectSpinner cardTypeSpinner;
    private List<String> selectedCardTypes;
    private List<String> cardTypeOptions;

    private RulesDataSource rulesSource;

    private QuizDataSource dataSource;

    private Deck deck;

    WifiDirectApp wifiDirectApp;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_settings);
        setTitle(Constants.GAME_SETTINGS);
        wifiDirectApp = (WifiDirectApp)getApplication();
        initializeDB();

        if (rulesSource.getAllRules().size() > 0) {
            deck = dataSource.getDeckWithId(rulesSource.getAllRules()
                    .get(rulesSource.getAllRules().size() - 1)
                    .getId());
        } else if (dataSource.getAllDecks().size()>0){
            deck = dataSource.getAllDecks().get(0);
        }

        final BaseMultiSelectSpinner.MultiSpinnerListener multiSpinnerListener = new BaseMultiSelectSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                selectedCardTypes.clear();
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i])
                        selectedCardTypes.add(cardTypeOptions.get(i));
                }
                Rules tempRules = new Rules();
                tempRules.setCardTypes(gson.toJson(selectedCardTypes));
                Deck filteredDeck = deck.filter(tempRules);

                if (!isInputEmpty(cardCountInput) &&
                        Integer.valueOf(cardCountInput.getText().toString()) > filteredDeck.getCards().size())
//                                cardCountInput.setText(filteredDeck.getCards().size()); //TODO
                    cardCountInput.setText("" + filteredDeck.getCards().size());

                filterCardCount(filteredDeck);
            }
        };

        deckSpinner = (Spinner)findViewById(R.id.deck_spinner);
            List<String> deckNames = new ArrayList<>();
            for (Deck deck: dataSource.getAllDecks()) {
                deckNames.add(deck.getDeckName());
            }
            final ArrayAdapter<String> deckAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, deckNames);
            deckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            deckSpinner.setAdapter(deckAdapter);
            deckSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String name = deckAdapter.getItem(position);
                    for (Deck deck: dataSource.getAllDecks()) {
                        if (deck.getDeckName().equals(name)) {
                            Log.d(TAG, "Deck ID is " + deck.getId());
                            // update the card type spinner with any new card types
                            cardTypeOptions = formatCardTypes(deck);
                            ArrayAdapter<String> cardTypeAdapter = new ArrayAdapter<>(NewGameSettingsActivity.this,
                                    android.R.layout.simple_list_item_multiple_choice, cardTypeOptions);
                            cardTypeSpinner
                                    .setListAdapter(cardTypeAdapter)
                                    .setAllCheckedText(ALL_CARD_TYPES)
                                    .setAllUncheckedText(NO_CARD_TYPES)
                                    .setMinSelectedItems(1)
                                    .setListener(multiSpinnerListener);
                            for (String type: selectedCardTypes) {
                                if (cardTypeOptions.indexOf(type) > -1)
                                    cardTypeSpinner.selectItem(cardTypeOptions.indexOf(type), true);
                            }

                            // update the card count with the new deck size
                            Rules tempRules = new Rules();
                            tempRules.setCardTypes(gson.toJson(selectedCardTypes));
                            Deck filteredDeck = deck.filter(tempRules);
                            filterCardCount(filteredDeck);
                            if (!isInputEmpty(cardCountInput) &&
                                    Integer.valueOf(cardCountInput.getText().toString()) > filteredDeck.getCards().size())
                                cardCountInput.setText("" + filteredDeck.getCards().size());

                            NewGameSettingsActivity.this.deck = deck;
                            break;
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { /* Do nothing */ }
            });
            for (int i = 0; i < deckNames.size(); i++) {
                if (deckNames.get(i).equals(deck.getDeckName())) {
                    deckSpinner.setSelection(i);
                    break;
                }
            }

        cardTypeSpinner = (MultiSelectSpinner) findViewById(R.id.card_type_spinner);
            selectedCardTypes = new ArrayList<>();
            cardTypeOptions = formatCardTypes(deck);
            ArrayAdapter<String> cardTypeAdapter = new ArrayAdapter<>(NewGameSettingsActivity.this,
                    android.R.layout.simple_list_item_multiple_choice, cardTypeOptions);
            cardTypeSpinner
                    .setListAdapter(cardTypeAdapter)
                    .setAllCheckedText(ALL_CARD_TYPES)
                    .setAllUncheckedText(NO_CARD_TYPES)
                    .setMinSelectedItems(1)
                    .setListener(multiSpinnerListener);

        cardCountInput = (EditText)findViewById(R.id.card_count);
            filterCardCount(deck);
//            cardCountInput.setText(deck.getCards().size()); //TODO
            cardCountInput.setText("" + deck.getCards().size());
//            cardCountInput.setText(deck.getCards().size()); // Should be deck count, change when deck is done

        gameMinutesInput = (EditText)findViewById(R.id.game_minutes);
            NumberFilter gameMinuteFilter = new NumberFilter(1);
            gameMinutesInput.setFilters(new InputFilter[]{ gameMinuteFilter });
            gameMinutesInput.setOnFocusChangeListener(gameMinuteFilter);

        gameSecondsInput = (EditText)findViewById(R.id.game_seconds);
            NumberFilter gameSecondsFilter = new NumberFilter();
            gameSecondsInput.setFilters(new InputFilter[]{ gameSecondsFilter });
            gameSecondsInput.setOnFocusChangeListener(gameSecondsFilter);

        cardMinutesInput = (EditText)findViewById(R.id.card_minutes);
            NumberFilter cardMinuteFilter = new NumberFilter();
            cardMinutesInput.setFilters(new InputFilter[]{ cardMinuteFilter });
            cardMinutesInput.setOnFocusChangeListener(cardMinuteFilter);

        cardSecondsInput = (EditText)findViewById(R.id.card_seconds);
            NumberFilter cardSecondsFilter = new NumberFilter(1);
            cardSecondsInput.setFilters(new InputFilter[]{ cardSecondsFilter });
            cardSecondsInput.setOnFocusChangeListener(cardSecondsFilter);

        loadPreviousRules();
    }

    private void filterCardCount(Deck deck) {
        NumberFilter cardCountFilter = new NumberFilter(1, deck.getCards().size(), false);
        cardCountInput.setFilters(new InputFilter[]{ cardCountFilter });
        cardCountInput.setOnFocusChangeListener(cardCountFilter);
    }

    public boolean startGame(View v){
        if (selectedCardTypes.size() < 1) {
            Toast.makeText(this, "Must select card type", Toast.LENGTH_SHORT).show();
            return false;
        }

        Rules r = updateRuleSet();
        if (r == null)
            return false;

        if(wifiDirectApp.isHost() == 15){
            return startMultiplayerGamePlay(r);
        }else{
            //single player
            final Intent startGameIntent = new Intent(this,
                    GamePlayActivity.class);
            startGameIntent.putExtra(Constants.GAME_MODE,true);
            startActivity(startGameIntent);
            finish();
            return true;
        }
    }

    private boolean startMultiplayerGamePlay(Rules r){
        //multi-player
        if(!wifiDirectApp.mP2pConnected ){
            Log.d(TAG, "startChatActivity : p2p connection is " +
                    "missing, do nothing...");
            return false;
        }

        //send everyone the rules
        String rulesToSend = gson.toJson(r);
        ConnectionService.sendMessage(MSG_SEND_RULES_ACTIVITY, rulesToSend);

        runOnUiThread(new Runnable() {
            @Override public void run() {
                Intent i = wifiDirectApp.
                        getLaunchActivityIntent(
                                ManageGameplayActivity.class, null);//
                startActivity(i);
                finish();
            }
        });
        return true;
    }

    public Rules updateRuleSet() {
        if (isInputEmpty(gameMinutesInput)) {
            Toast.makeText(this, GAME_MINUTES_ERROR, Toast.LENGTH_SHORT).show();
            return null;
        }
        if (isInputEmpty(gameSecondsInput)) {
            Toast.makeText(this, GAME_SECONDS_ERROR, Toast.LENGTH_SHORT).show();
            return null;
        }
        if (isInputEmpty(cardMinutesInput)) {
            Toast.makeText(this, CARD_MINUTES_ERROR, Toast.LENGTH_SHORT).show();
            return null;
        }
        if (isInputEmpty(cardSecondsInput)) {
            Toast.makeText(this, CARD_SECONDS_ERROR, Toast.LENGTH_SHORT).show();
            return null;
        }
        if (isInputEmpty(cardCountInput)) {
            Toast.makeText(this, CARD_COUNT_ERROR, Toast.LENGTH_SHORT).show();
            return null;
        } else if (cardCountInput.getText().toString().equals("0")) {
            Toast.makeText(this, CARD_COUNT_ZERO, Toast.LENGTH_SHORT).show();
            return null;
        }

        long gameMinutesInMilli = Integer.valueOf(
                gameMinutesInput.getText().toString()) * 60000;
        long gameSecondsInMilli = Integer.valueOf(
                gameSecondsInput.getText().toString()) * 1000;
        long cardMinutesInMilli = Integer.valueOf(
                cardMinutesInput.getText().toString()) * 60000;
        long cardSecondsInMilli = Integer.valueOf(
                cardSecondsInput.getText().toString()) * 1000;
        int cardCount = Integer.valueOf(
                cardCountInput.getText().toString());

        String cardTypes = gson.toJson(selectedCardTypes);

        if (rulesSource.getAllRules().size() < 1) {
            Log.d(TAG, "Deck id is saved as " + (int)deck.getId());
            return rulesSource.createRule(
                    cardCount,
                    gameMinutesInMilli + gameSecondsInMilli,
                    cardMinutesInMilli + cardSecondsInMilli,
                    cardTypes,
                    deck.getId());
        }

        Log.d(TAG, "Deck id is updated as " + (int)deck.getId());

        Rules rule = rulesSource.getAllRules().get(rulesSource.getAllRules().size() - 1);
        rulesSource.deleteRule(rule);

        if (rule.getTimeLimit() != gameMinutesInMilli + gameSecondsInMilli) {
            rule.setTimeLimit(gameMinutesInMilli + gameSecondsInMilli);
        }
        if (rule.getCardDisplayTime() != cardMinutesInMilli + cardSecondsInMilli) {
            rule.setCardDisplayTime(cardMinutesInMilli + cardSecondsInMilli);
        }
        if (rule.getMaxCardCount() != cardCount) {
            rule.setMaxCardCount(cardCount);
        }
        if (!rule.getCardTypes().equals(cardTypes)) {
            rule.setCardTypes(cardTypes);
        }

        return rulesSource.createRule(rule.getMaxCardCount(),
                rule.getTimeLimit(), rule.getCardDisplayTime(),
                rule.getCardTypes(), (int)deck.getId());
    }

    public boolean loadPreviousRules(){
        if (rulesSource.getAllRules().size() < 1) return false;

        Rules rule = rulesSource.getAllRules().get(rulesSource.getAllRules().size() - 1);

        Calendar gameCal = Calendar.getInstance();
        gameCal.setTimeInMillis(rule.getTimeLimit());

        Calendar cardCal = Calendar.getInstance();
        cardCal.setTimeInMillis(rule.getCardDisplayTime());

        gameMinutesInput.setText("" + gameCal.get(Calendar.MINUTE));
        gameSecondsInput.setText(gameCal.get(Calendar.SECOND) < 10 ?
                                    "0" + gameCal.get(Calendar.SECOND) :
                                    "" + gameCal.get(Calendar.SECOND));
        cardMinutesInput.setText("" + cardCal.get(Calendar.MINUTE));
        cardSecondsInput.setText(cardCal.get(Calendar.SECOND) < 10 ?
                                    "0" + cardCal.get(Calendar.SECOND) :
                                    "" + cardCal.get(Calendar.SECOND));

        if (rule.getMaxCardCount() > deck.filter(rule).getCards().size())
            cardCountInput.setText("" + deck.filter(rule).getCards().size());
        else
            cardCountInput.setText("" + rule.getMaxCardCount());

        Log.d(TAG, "Selected card types: " + rule.getCardTypes());
        Type listType = new TypeToken<ArrayList<String>>(){}.getType();
        selectedCardTypes = gson.fromJson(rule.getCardTypes(), listType);
        for (String type: selectedCardTypes) {
            if (cardTypeOptions.indexOf(type) > -1)
                cardTypeSpinner.selectItem(cardTypeOptions.indexOf(type), true);
        }
        return true;
    }

    public boolean loadDeck(){
        return false;
    }

    private boolean isInputEmpty(EditText input) {
        return input.getText().toString().equals("");
    }

    private boolean initializeDB(){
        rulesSource = new RulesDataSource(this);
        dataSource = new QuizDataSource(this);
        return rulesSource.open() && dataSource.open() && dataSource.open();
    }

    public List<String> formatCardTypes(Deck deck) {
        if (deck == null)
            return new ArrayList<>();
        return deck.getCardTypes();
    }

    @Override
    protected void onResume(){
        super.onResume();
        rulesSource.open();
        dataSource.open();
    }

    @Override
    protected void onPause(){
        super.onPause();
        rulesSource.close();
        dataSource.close();
    }
}
