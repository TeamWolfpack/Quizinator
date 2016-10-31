package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.Filters.NumberFilter;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import java.util.Calendar;

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

        cardTypeSpinner = (Spinner)findViewById(R.id.card_type_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.card_type_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cardTypeSpinner.setAdapter(adapter);

        cardCountInput = (EditText)findViewById(R.id.card_count);
            NumberFilter cardCountFilter = new NumberFilter(1, 10, false); // Max should be deck count, change when deck is done
            cardCountInput.setFilters(new InputFilter[]{ cardCountFilter });
            cardCountInput.setOnFocusChangeListener(cardCountFilter);
            cardCountInput.setText("10"); // Should be deck count, change when deck is done

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

        //send everyone the rules

        //somehow send r to everyone

        runOnUiThread(new Runnable() {
            @Override public void run() {
                Intent i = wifiDirectApp.
                        getLauchActivityIntent(
                                HostGameActivity.class, null);
                //tell everyone to start the game

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

        cardCountInput.setText("" + rule.getMaxCardCount());
        cardCountInput.setText("" + rule.getMaxCardCount());

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
     */
    private boolean initializeDB(){
        rulesSource = new RulesDataSource(this);
        return rulesSource.open();
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onResume(){
        super.onResume();
        rulesSource.open();
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onPause(){
        super.onPause();
        rulesSource.close();
    }
}
