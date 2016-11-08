package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.Filters.NumberFilter;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import java.util.Arrays;
import java.util.Calendar;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_SEND_CARD_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_SEND_RULES_ACTIVITY;

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
    private Spinner cardTypeSpinner;

    private RulesDataSource rulesSource;
    private DeckDataSource deckDataSource;

    private Deck deck;

    WifiDirectApp wifiDirectApp = null;

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/4/2016)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_settings);
        setTitle("Game Settings");
        wifiDirectApp = (WifiDirectApp)getApplication();
        initializeDB();

        if(deckDataSource.getAllDecks().size()>0){
            deck = deckDataSource.getAllDecks().get(0);
        }else{
            deck = initializeDeck();
        }

        cardTypeSpinner = (Spinner)findViewById(R.id.card_type_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.card_type_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cardTypeSpinner.setAdapter(adapter);

        cardCountInput = (EditText)findViewById(R.id.card_count);
            NumberFilter cardCountFilter = new NumberFilter(1, deck.getCards().size(), false); // Max should be deck count, change when deck is done
            cardCountInput.setFilters(new InputFilter[]{ cardCountFilter });
            cardCountInput.setOnFocusChangeListener(cardCountFilter);
            cardCountInput.setText(""+deck.getCards().size()); // Should be deck count, change when deck is done

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

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/4/2016)
     */
    public boolean startGame(View v){
        Rules r = updateRuleSet();
        if(wifiDirectApp.isHost() == 15){
            return startMultiplayerGamePlay(r);
        }else{
            //single player
            final Intent startGameIntent = new Intent(this,
                    GamePlayActivity.class);
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

        Gson gson = new Gson();

        //send everyone the rules
        String rulesToSend = gson.toJson(r);
        ConnectionService.sendMessage(MSG_SEND_RULES_ACTIVITY, rulesToSend);

        //Send first card
//        String cardToSend = gson.toJson(deck.getCards().get(0));
//        ConnectionService.sendMessage(MSG_SEND_CARD_ACTIVITY, cardToSend);

        runOnUiThread(new Runnable() {
            @Override public void run() {
                Intent i = wifiDirectApp.
                        getLaunchActivityIntent(
                                ManageGameplayActivity.class, null);//
                startActivity(i);
            }
        });
        return true;

    }

    /*
     * @author leonardj (10/31/2016)
     * @author kuczynskij (10/31/2016)
     */
    public Rules updateRuleSet() {
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
        String cardTypes = cardTypeSpinner.getSelectedItem().toString();

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

        int position = 0;
        String type = rule.getCardTypes();
        if (type.equals("True/False")) {
            position = 0;
        } else if (type.equals("Multiple Choice")) {
            position = 1;
        } else if (type.equals("Both")) {
            position = 2;
        }

        cardTypeSpinner.setSelection(position);
        return true;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    public boolean loadDeck(){
        return false;
    }

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/14/2016)
     * @author farrowc (10/31/2016)
     */
    private boolean initializeDB(){
        rulesSource = new RulesDataSource(this);
        deckDataSource = new DeckDataSource(this);
        return rulesSource.open() && deckDataSource.open();
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
        cards[0].setCardType("MC");
        cards[1] = new Card();
        cards[1].setQuestion("1*2 = 0");
        cards[1].setCorrectAnswer("False");
        cards[1].setCardType("TF");
        cards[2] = new Card();
        cards[2].setQuestion("4*5 = 20");
        cards[2].setCorrectAnswer("True");
        cards[2].setCardType("TF");
        cards[3] = new Card();
        cards[3].setQuestion("20*10 = 100");
        cards[3].setCorrectAnswer("False");
        cards[3].setCardType("TF");
        cards[4] = new Card();
        cards[4].setQuestion("10*91 = 901");
        cards[4].setCorrectAnswer("False");
        cards[4].setCardType("TF");
        cards[5] = new Card();
        cards[5].setQuestion("100^2 = 10000");
        cards[5].setCorrectAnswer("True");
        cards[5].setCardType("TF");
        cards[6] = new Card();
        cards[6].setQuestion("10*102 = 1002");
        cards[6].setCorrectAnswer("False");
        cards[6].setCardType("TF");
        cards[7] = new Card();
        cards[7].setQuestion("8/2 = 4");
        cards[7].setCorrectAnswer("True");
        cards[7].setCardType("TF");
        cards[8] = new Card();
        cards[8].setQuestion("120/4 = 30");
        cards[8].setCorrectAnswer("True");
        cards[8].setCardType("TF");
        cards[9] = new Card();
        cards[9].setQuestion("6*7 = 41");
        cards[9].setCorrectAnswer("False");
        cards[9].setCardType("TF");
        newDeck.setCards(Arrays.asList(cards));

        deckDataSource.createDeck("Default", Arrays.asList(cards));

        return newDeck;
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
