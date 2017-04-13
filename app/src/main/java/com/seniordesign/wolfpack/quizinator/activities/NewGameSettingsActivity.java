package com.seniordesign.wolfpack.quizinator.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.database.Deck;
import com.seniordesign.wolfpack.quizinator.database.Rules;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.filters.NumberFilter;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.wifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.wifiDirect.WifiDirectApp;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner;
import io.apptik.widget.multiselectspinner.MultiSelectSpinner;

import static com.seniordesign.wolfpack.quizinator.Constants.*;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.*;

public class NewGameSettingsActivity extends AppCompatActivity {

    private static final String TAG = "ACT_NGS";

    private EditText cardCountInput;
    private EditText gameMinutesInput;
    private EditText gameSecondsInput;
    private EditText cardMinutesInput;
    private EditText cardSecondsInput;

    private MultiSelectSpinner cardTypeSpinner;

    private List<CARD_TYPES> selectedCardTypes;
    private List<CARD_TYPES> cardTypeOptions;

    private QuizDataSource dataSource;

    private Deck deck;
    Boolean isMultiplayer;

    WifiDirectApp wifiDirectApp;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_settings);
        setTitle(Constants.GAME_SETTINGS);
        wifiDirectApp = (WifiDirectApp)getApplication();
        isMultiplayer = getIntent().getBooleanExtra(Constants.MULTIPLAYER, true);
        String defaultRuleSet;
        initializeDB();

        TableRow ruleSetRow = (TableRow) findViewById(R.id.ruleset_row);
        if (isMultiplayer) {
            initializeRulesetSpinner();
            defaultRuleSet = Constants.DEFAULT_MULTIPLE_RULESET;
            ruleSetRow.setVisibility(View.VISIBLE);
        } else {
            defaultRuleSet = Constants.DEFAULT_SINGLE_RULESET;
            ruleSetRow.setVisibility(View.GONE);
        }

        if (dataSource.getAllRules().size() > 0) {
            deck = dataSource.getDeckFromRuleSetName(defaultRuleSet);
        } else if (dataSource.getAllDecks().size()>0){
            deck = dataSource.getAllDecks().get(0);
        }

        if (wifiDirectApp.mIsServer) {
            TextView warning = (TextView)findViewById(R.id.card_count_solo_warning);
            warning.setVisibility(View.GONE);
        }

        final BaseMultiSelectSpinner.MultiSpinnerListener
                multiSpinnerListener = initializeMultiSpinnerListener();
        initializeDeckSpinner(multiSpinnerListener);
        initializeCardSpinner(multiSpinnerListener);

        initializeTextFieldListeners();

        loadPreviousRules(defaultRuleSet);
    }

    private void initializeTextFieldListeners(){
        cardCountInput = (EditText)findViewById(R.id.card_count);
            filterCardCount(deck);
            cardCountInput.setText(String.valueOf(deck.getCards().size()));

        gameMinutesInput = (EditText)findViewById(R.id.game_minutes);
            NumberFilter gameMinuteFilter = new NumberFilter(1);
            gameMinutesInput.setOnFocusChangeListener(gameMinuteFilter);

        gameSecondsInput = (EditText)findViewById(R.id.game_seconds);
            NumberFilter gameSecondsFilter = new NumberFilter(0, 59, true);
            gameSecondsInput.setOnFocusChangeListener(gameSecondsFilter);

        cardMinutesInput = (EditText)findViewById(R.id.card_minutes);
            NumberFilter cardMinuteFilter = new NumberFilter();
            cardMinutesInput.setOnFocusChangeListener(cardMinuteFilter);

        cardSecondsInput = (EditText)findViewById(R.id.card_seconds);
            NumberFilter cardSecondsFilter = new NumberFilter(0, 59);
            cardSecondsInput.setOnFocusChangeListener(cardSecondsFilter);
    }

    private BaseMultiSelectSpinner.MultiSpinnerListener initializeMultiSpinnerListener(){
        return new BaseMultiSelectSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                selectedCardTypes.clear();
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i])
                        selectedCardTypes.add(cardTypeOptions.get(i));
                }
                Deck filteredDeck = dataSource.getFilteredDeck(deck.getId(),
                        selectedCardTypes, wifiDirectApp.mIsServer);

                if (isInputZero(cardCountInput) ||
                        isInputEmpty(cardCountInput) ||
                        Integer.valueOf(cardCountInput.getText().toString()) > filteredDeck.getCards().size())
                    cardCountInput.setText(String.valueOf(filteredDeck.getCards().size()));
                filterCardCount(filteredDeck);
            }
        };
    }

    private void initializeRulesetSpinner(){
        Spinner rulesetSpinner = (Spinner) findViewById(R.id.ruleset_spinner);
        List<String> rulesetNames = dataSource.getMultiplayerRulesetsNames();
        final ArrayAdapter<String> rulesetAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, rulesetNames);
        rulesetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rulesetSpinner.setAdapter(rulesetAdapter);
        rulesetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String select = parent.getSelectedItem().toString();
                if (select.equals(Constants.DEFAULT_MULTIPLE_RULESET)) {
                    findViewById(R.id.allow_multiple_winners_row).setVisibility(View.GONE);
                    findViewById(R.id.double_edge_questions_row).setVisibility(View.GONE);
                    findViewById(R.id.final_wager_question_row).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.allow_multiple_winners_row).setVisibility(View.VISIBLE);
                    findViewById(R.id.double_edge_questions_row).setVisibility(View.VISIBLE);
                    findViewById(R.id.final_wager_question_row).setVisibility(View.VISIBLE);
                }
                loadPreviousRules(select);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });
    }

    private void initializeCardSpinner(final BaseMultiSelectSpinner.MultiSpinnerListener multiSpinnerListener){
        cardTypeSpinner = (MultiSelectSpinner) findViewById(R.id.card_type_spinner);
        cardTypeOptions = formatCardTypes(deck);
        if(!isMultiplayer){
            cardTypeOptions.remove(CARD_TYPES.FREE_RESPONSE);
            cardTypeOptions.remove(CARD_TYPES.VERBAL_RESPONSE);
        }
        selectedCardTypes = new ArrayList<>(cardTypeOptions);
        ArrayAdapter<CARD_TYPES> cardTypeAdapter = new ArrayAdapter<>(NewGameSettingsActivity.this,
                android.R.layout.simple_list_item_multiple_choice, cardTypeOptions);
        cardTypeSpinner
                .setListAdapter(cardTypeAdapter)
                .setAllCheckedText(ALL_CARD_TYPES)
                .setAllUncheckedText(NO_CARD_TYPES)
                .setMinSelectedItems(1)
                .setListener(multiSpinnerListener);
    }

    private void initializeDeckSpinner(final BaseMultiSelectSpinner.MultiSpinnerListener multiSpinnerListener){
        Spinner deckSpinner = (Spinner) findViewById(R.id.deck_spinner);
        List<String> deckNames = new ArrayList<>();
        for (Deck deck: dataSource.getAllDecks()) {
            if (deck.getCards().size() > 0)
                deckNames.add(deck.getDeckName());
        }
        final ArrayAdapter<String> deckAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, deckNames);
        deckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deckSpinner.setAdapter(deckAdapter);
        deckSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deckItemListener(position, deckAdapter, multiSpinnerListener);
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
    }

    private void deckItemListener(int position, ArrayAdapter<String> deckAdapter,
                  BaseMultiSelectSpinner.MultiSpinnerListener multiSpinnerListener){
        String name = deckAdapter.getItem(position);
        for (Deck deck: dataSource.getAllDecks()) {
            if (deck.getDeckName().equals(name)) {
                Log.d(TAG, "Deck ID is " + deck.getId());
                // update the card type spinner with any new card types
                cardTypeOptions = formatCardTypes(deck);
                if(!isMultiplayer){
                    cardTypeOptions.remove(CARD_TYPES.FREE_RESPONSE);
                    cardTypeOptions.remove(CARD_TYPES.VERBAL_RESPONSE);
                }
                ArrayAdapter<CARD_TYPES> cardTypeAdapter = new ArrayAdapter<>(NewGameSettingsActivity.this,
                        android.R.layout.simple_list_item_multiple_choice, cardTypeOptions);
                cardTypeSpinner
                        .setListAdapter(cardTypeAdapter)
                        .setAllCheckedText(ALL_CARD_TYPES)
                        .setAllUncheckedText(NO_CARD_TYPES)
                        .setMinSelectedItems(1)
                        .setListener(multiSpinnerListener);
                for (CARD_TYPES type: selectedCardTypes) {
                    if (cardTypeOptions.indexOf(type) > -1)
                        cardTypeSpinner.selectItem(cardTypeOptions.indexOf(type), true);
                }
                Deck filteredDeck = dataSource.getFilteredDeck(deck.getId(), selectedCardTypes, wifiDirectApp.mIsServer);

                if (isInputZero(cardCountInput) ||
                        isInputEmpty(cardCountInput) ||
                        Integer.valueOf(cardCountInput.getText().toString()) > filteredDeck.getCards().size())
                    cardCountInput.setText(String.valueOf(filteredDeck.getCards().size()));
                filterCardCount(filteredDeck);

                NewGameSettingsActivity.this.deck = filteredDeck;
                break;
            }
        }
    }

    private void filterCardCount(Deck deck) {
        NumberFilter cardCountFilter = new NumberFilter(1, deck.getCards().size(), false);
        cardCountInput.setOnFocusChangeListener(cardCountFilter);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void startGame(View v){
        if (selectedCardTypes.size() < 1) {
            Toast.makeText(this, "Must select card type", Toast.LENGTH_SHORT).show();
//            return false; //TODO changing return to void
        }

        Rules r = updateRuleSet();
        if (r == null)
//            return false;

        if(wifiDirectApp.isHost() == 15){
//            return startMultiplayerGamePlay(r);
            startMultiplayerGamePlay(r);
        }else{
            //single player
            final Intent startGameIntent = new Intent(this,
                    GamePlayActivity.class);
            startGameIntent.putExtra(Constants.GAME_MODE, true);
            startGameIntent.putExtra(Constants.RULES, Constants.DEFAULT_SINGLE_RULESET);
            startActivity(startGameIntent);
            finish();
//            return true;
        }
    }

    private boolean startMultiplayerGamePlay(final Rules r){
        if(!wifiDirectApp.mP2pConnected ){
            return false;
        }
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Intent intent = wifiDirectApp.getLaunchActivityIntent(ManageGameplayActivity.class, null);
                intent.putExtra(Constants.RULES, r.getRuleSetName());
                startActivity(intent);
                ConnectionService.sendMessage(MSG_SEND_RULES_ACTIVITY, gson.toJson(r));
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
        if (isInputZero(cardSecondsInput) && isInputZero(cardMinutesInput)) {
            Toast.makeText(this, CARD_TIME_ZERO, Toast.LENGTH_SHORT).show();
            return null;
        }
        if (isInputEmpty(cardCountInput)) {
            Toast.makeText(this, CARD_COUNT_ERROR, Toast.LENGTH_SHORT).show();
            return null;
        } else if (isInputZero(cardCountInput)) {
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

        Rules rule = new Rules();
        if(isMultiplayer) {
            Spinner ruleSetSpinner = (Spinner) findViewById(R.id.ruleset_spinner);
            rule = dataSource.getRuleSetByName(ruleSetSpinner.getSelectedItem().toString());
        } else {
            rule = dataSource.getRuleSetByName(Constants.DEFAULT_SINGLE_RULESET);
        }

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

        rule.setDeckId(deck.getId());
        if (rule.getRuleSetName().equals("Default")) {
            rule.setDoubleEdgeSword(null);
            rule.setLastCardWager(null);
            rule.setMultipleWinners(null);
        } else {
            CheckBox doubleEdge = (CheckBox) findViewById(R.id.include_double_edge_questions);
            CheckBox finalWager = (CheckBox) findViewById(R.id.include_final_wager_question);
            CheckBox multiWinners = (CheckBox) findViewById(R.id.allow_multiple_winners);
            rule.setDoubleEdgeSword(doubleEdge.isChecked());
            rule.setLastCardWager(finalWager.isChecked());
            rule.setMultipleWinners(multiWinners.isChecked());
        }
        dataSource.updateRules(rule);
        return rule;
    }

    public boolean loadPreviousRules(String ruleSetName){
        Rules rule = dataSource.getRuleSetByName(ruleSetName);

        Calendar gameCal = Calendar.getInstance();
        gameCal.setTimeInMillis(rule.getTimeLimit());

        Calendar cardCal = Calendar.getInstance();
        cardCal.setTimeInMillis(rule.getCardDisplayTime());

        gameMinutesInput.setText(gameCal.get(Calendar.MINUTE) < 10 ?
                                    "0" + gameCal.get(Calendar.MINUTE) :
                                    "" + gameCal.get(Calendar.MINUTE));
        gameSecondsInput.setText(gameCal.get(Calendar.SECOND) < 10 ?
                                    "0" + gameCal.get(Calendar.SECOND) :
                                    "" + gameCal.get(Calendar.SECOND));
        cardMinutesInput.setText(cardCal.get(Calendar.MINUTE) < 10 ?
                                    "0" + cardCal.get(Calendar.MINUTE) :
                                    "" + cardCal.get(Calendar.MINUTE));
        cardSecondsInput.setText(cardCal.get(Calendar.SECOND) < 10 ?
                                    "0" + cardCal.get(Calendar.SECOND) :
                                    "" + cardCal.get(Calendar.SECOND));

        if (rule.getMaxCardCount() > deck.getCards().size())
            cardCountInput.setText(String.valueOf(deck.getCards().size()));
        else if (isInputZero(cardCountInput) || isInputEmpty(cardCountInput))
            cardCountInput.setText(String.valueOf(deck.getCards().size()));
        else
            cardCountInput.setText(String.valueOf(rule.getMaxCardCount()));

        Log.d(TAG, "Selected card types: " + rule.getCardTypes());
        Type listType = new TypeToken<ArrayList<Constants.CARD_TYPES>>(){}.getType();
        selectedCardTypes = gson.fromJson(rule.getCardTypes(), listType);
        for (CARD_TYPES type: selectedCardTypes) {
            if (cardTypeOptions.indexOf(type) > -1)
                cardTypeSpinner.selectItem(cardTypeOptions.indexOf(type), true);
        }

        if (ruleSetName.equals(Constants.DOUBLE_DOWN_RULESET)) {
            CheckBox multiWinners = (CheckBox) findViewById(R.id.allow_multiple_winners);
            CheckBox doubleEdge = (CheckBox) findViewById(R.id.include_double_edge_questions);
            CheckBox finalWager = (CheckBox) findViewById(R.id.include_final_wager_question);

            Log.d(TAG, "multiWinner: " + rule.getMultipleWinners());

            multiWinners.setChecked(
                    rule.getMultipleWinners() == null ? false : rule.getMultipleWinners());
            doubleEdge.setChecked(
                    rule.isDoubleEdgeSword() == null ? false : rule.isDoubleEdgeSword());
            finalWager.setChecked(
                    rule.isLastCardWager() == null ? false : rule.isLastCardWager());
        }

        return true;
    }

    private boolean isInputEmpty(EditText input) {
        return input.getText().toString().equals("");
    }

    private boolean isInputZero(EditText input) {
        return input.getText().toString().equals(STRING_0) ||
                input.getText().toString().equals(STRING_00);
    }

    private boolean initializeDB(){
        dataSource = new QuizDataSource(this);
        return dataSource.open();
    }

    public List<CARD_TYPES> formatCardTypes(Deck deck) {
        if (deck == null)
            return new ArrayList<>();
        return deck.getCardTypes();
    }

    @Override
    protected void onResume(){
        super.onResume();
        dataSource.open();
    }

    @Override
    protected void onPause(){
        super.onPause();
        dataSource.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
