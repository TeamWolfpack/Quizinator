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
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Card.CardDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.Filters.NumberFilter;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
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
    private DeckDataSource deckDataSource;
    private CardDataSource cardDataSource;

    private Deck deck;

    WifiDirectApp wifiDirectApp;

    Gson gson = new Gson();

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/4/2016)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_settings);
        setTitle(Constants.GAME_SETTINGS);
        wifiDirectApp = (WifiDirectApp)getApplication();
        initializeDB();

        if(deckDataSource.getAllDecks().size()>0){
            deck = deckDataSource.getAllDecks().get(0);
        }else{
            deck = initializeDeck();
        }

        deckSpinner = (Spinner)findViewById(R.id.deck_spinner);
            List<String> deckNames = new ArrayList<>();
            for (Deck deck: deckDataSource.getAllDecks()) {
                deckNames.add(deck.getDeckName());
            }
            final ArrayAdapter<String> deckAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, deckNames);
            deckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            deckSpinner.setAdapter(deckAdapter);
            deckSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "Item Selected");
                    String name = deckAdapter.getItem(position);
                    for (Deck deck: deckDataSource.getAllDecks()) {
                        if (deck.getDeckName().equals(name)) {
                            initializeCardInput(deck);
                            break;
                        }
                    }
                    // TODO update the card count and card type spinner with the newly selected deck
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.d(TAG, "No Change in deck selection");
                    // Do nothing
                }
            });

        initializeCardInput(deck);
        initializeTimeInput();

        loadPreviousRules();
    }

    /*
     * @author leonardj 1/6/17
     */
    private void initializeCardInput(final Deck deck) {
        cardTypeSpinner = (MultiSelectSpinner) findViewById(R.id.card_type_spinner);
            selectedCardTypes = new ArrayList<>();
            cardTypeOptions = formatCardTypes(deck);
            ArrayAdapter<String> cardTypeAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_multiple_choice, cardTypeOptions);
            cardTypeSpinner
                    .setListAdapter(cardTypeAdapter)
                    .setAllCheckedText(ALL_CARD_TYPES)
                    .setAllUncheckedText(NO_CARD_TYPES)
                    .setMinSelectedItems(1)
                    .setListener(new BaseMultiSelectSpinner.MultiSpinnerListener() {
                        @Override
                        public void onItemsSelected(boolean[] selected) {
                            selectedCardTypes.clear();
                            for (int i = 0; i < selected.length; i++) {
                                if (selected[i])
                                    selectedCardTypes.add(shortFormCardType(cardTypeOptions.get(i)));
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
                    });

        cardCountInput = (EditText)findViewById(R.id.card_count);
            filterCardCount(deck);
//                cardCountInput.setText(deck.getCards().size()); //TODO
            cardCountInput.setText("" + deck.getCards().size());
    //            cardCountInput.setText(deck.getCards().size()); // Should be deck count, change when deck is done
    }

    /*
     * @author leonardj 1/6/17
     */
    private void initializeTimeInput() {
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
    }

    /*
     * @author leoanrdj 12/19/16
     */
    private void filterCardCount(Deck deck) {
        NumberFilter cardCountFilter = new NumberFilter(1, deck.getCards().size(), false);
        cardCountInput.setFilters(new InputFilter[]{ cardCountFilter });
        cardCountInput.setOnFocusChangeListener(cardCountFilter);
    }

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/4/2016)
     */
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
            return true;
        }
    }

    /*
     * @author kuczynskij (10/31/2016)
     * @author leonardj (10/31/2016)
     */
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

    /*
     * @author leonardj (10/31/2016)
     * @author kuczynskij (10/31/2016)
     */
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
            return rulesSource.createRule(cardCount,
                    gameMinutesInMilli + gameSecondsInMilli,
                    cardMinutesInMilli + cardSecondsInMilli, cardTypes);
        }

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
                rule.getCardTypes());
    }

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/14/2016)
     */
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
            cardTypeSpinner.selectItem(cardTypeOptions.indexOf(longFormCardType(type)), true);
        }
        return true;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    public boolean loadDeck(){
        return false;
    }

    /*
     * @author leonardj 12/23/16
     */
    private boolean isInputEmpty(EditText input) {
        return input.getText().toString().equals("");
    }

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/14/2016)
     * @author farrowc (10/31/2016)
     */
    private boolean initializeDB(){
        rulesSource = new RulesDataSource(this);
        deckDataSource = new DeckDataSource(this);
        cardDataSource = new CardDataSource(this);
        return rulesSource.open() && deckDataSource.open() && cardDataSource.open();
    }

    /*
     * @author farrowc (10/31/2016)
     * TEMP returns a sample deck for testing
     */
    private Deck initializeDeck() {
        Deck newDeck = new Deck();
        Card[] cards = new Card[10];
        cards[0] = new Card();
        cards[0].setQuestion("1+1 = ?");
        cards[0].setCorrectAnswer("2");
        String[] answerArea = {"1","2","3","4"};
        cards[0].setPossibleAnswers(answerArea);
        cards[0].setCardType(Constants.SHORT_MULTIPLE_CHOICE);
        cards[0].setPoints(1);
        cards[0].setModeratorNeeded("False");
        cards[1] = new Card();
        cards[1].setQuestion("1*2 = 0");
        cards[1].setCorrectAnswer("False");
        cards[1].setCardType(Constants.SHORT_TRUE_FALSE);
        String[] answerAreaTF = {"True", "False"};
        cards[1].setPossibleAnswers(answerAreaTF);
        cards[1].setPoints(1);
        cards[1].setModeratorNeeded("False");
        cards[2] = new Card();
        cards[2].setQuestion("4*5 = 20");
        cards[2].setCorrectAnswer("True");
        cards[2].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[2].setPossibleAnswers(answerAreaTF);
        cards[2].setPoints(1);
        cards[2].setModeratorNeeded("False");
        cards[3] = new Card();
        cards[3].setQuestion("20*10 = 100");
        cards[3].setCorrectAnswer("False");
        cards[3].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[3].setPossibleAnswers(answerAreaTF);
        cards[3].setPoints(1);
        cards[3].setModeratorNeeded("False");
        cards[4] = new Card();
        cards[4].setQuestion("10*91 = 901");
        cards[4].setCorrectAnswer("False");
        cards[4].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[4].setPossibleAnswers(answerAreaTF);
        cards[4].setPoints(1);
        cards[4].setModeratorNeeded("False");
        cards[5] = new Card();
        cards[5].setQuestion("100^2 = 10000");
        cards[5].setCorrectAnswer("True");
        cards[5].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[5].setPossibleAnswers(answerAreaTF);
        cards[5].setPoints(1);
        cards[5].setModeratorNeeded("False");
        cards[6] = new Card();
        cards[6].setQuestion("10*102 = 1002");
        cards[6].setCorrectAnswer("False");
        cards[6].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[6].setPossibleAnswers(answerAreaTF);
        cards[6].setPoints(1);
        cards[6].setModeratorNeeded("False");
        cards[7] = new Card();
        cards[7].setQuestion("8/2 = 4");
        cards[7].setCorrectAnswer("True");
        cards[7].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[7].setPossibleAnswers(answerAreaTF);
        cards[7].setPoints(1);
        cards[7].setModeratorNeeded("False");
        cards[8] = new Card();
        cards[8].setQuestion("120/4 = 30");
        cards[8].setCorrectAnswer("True");
        cards[8].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[8].setPossibleAnswers(answerAreaTF);
        cards[8].setPoints(1);
        cards[8].setModeratorNeeded("False");
        cards[9] = new Card();
        cards[9].setQuestion("6*7 = 41");
        cards[9].setCorrectAnswer("False");
        cards[9].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[9].setPossibleAnswers(answerAreaTF);
        cards[9].setPoints(1);
        cards[9].setModeratorNeeded("False");
        newDeck.setCards(Arrays.asList(cards));
        deckDataSource.createDeck("Default", Arrays.asList(cards));

        return newDeck;
    }

    /*
     * @author leonardj (12/16/16)
     */
    public List<String> formatCardTypes(Deck deck) {
        ArrayList<String> types = new ArrayList<>();

        if (deck == null) {
            return types;
        }

        for (String type: deck.getCardTypes()) {
            String longType = longFormCardType(type);
            if (longType != null)
                types.add(longType);
        }
        return types;
    }

    /*
     * @author leonardj (12/16/16)
     */
    public String shortFormCardType(String type) {
        String shortForm = null;

        if (type.equals(LONG_TRUE_FALSE))
            shortForm = SHORT_TRUE_FALSE;
        else if (type.equals(LONG_MULTIPLE_CHOICE))
            shortForm = SHORT_MULTIPLE_CHOICE;
        else if (type.equals(LONG_FREE_RESPONSE))
            shortForm = SHORT_FREE_RESPONSE;
        else if (type.equals(LONG_VERBAL_RESPONSE))
            shortForm = SHORT_VERBAL_RESPONSE;

        return shortForm;
    }

    /*
     * @author leonardj (12/16/16)
     */
    public String longFormCardType(String type) {
        String longForm = null;

        if (type.equals(SHORT_TRUE_FALSE))
            longForm = LONG_TRUE_FALSE;
        else if (type.equals(SHORT_MULTIPLE_CHOICE))
            longForm = LONG_MULTIPLE_CHOICE;
        else if (type.equals(SHORT_FREE_RESPONSE))
            longForm = LONG_FREE_RESPONSE;
        else if (type.equals(SHORT_VERBAL_RESPONSE))
            longForm = LONG_VERBAL_RESPONSE;

        return longForm;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onResume(){
        super.onResume();
        rulesSource.open();
        deckDataSource.open();
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onPause(){
        super.onPause();
        rulesSource.close();
        deckDataSource.close();
    }
}
