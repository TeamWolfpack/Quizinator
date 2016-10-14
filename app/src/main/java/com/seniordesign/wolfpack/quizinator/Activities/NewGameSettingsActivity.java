package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.seniordesign.wolfpack.quizinator.Filters.NumberFilter;
import com.seniordesign.wolfpack.quizinator.R;

/**
 * The new game settings activity is...
 * @creation 09/28/2016
 */
public class NewGameSettingsActivity extends AppCompatActivity {

    private EditText cardCountInput;
    private EditText gameMinutesInput;
    private EditText gameSecondsInput;
    private EditText cardMinutesInput;
    private EditText cardSecondsInput;
    private Spinner cardTypeSpinner;


    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/4/2016)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_settings);
        setTitle("Game Settings");

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
    }

    /*
     * @author kuczynskij (09/28/2016)
     * @author leonardj (10/4/2016)
     */
    public void startGame(View v){
//        Integer gameMinutes = Integer.valueOf(gameMinutesInput.getText().toString());
//        Integer gameSeconds = Integer.valueOf(gameSecondsInput.getText().toString());
//        Integer cardMinutes = Integer.valueOf(cardMinutesInput.getText().toString());
//        Integer cardSeconds = Integer.valueOf(cardSecondsInput.getText().toString());
//        Integer cardCount = Integer.valueOf(cardCountInput.getText().toString());
//        String cardTypes = cardTypeSpinner.getSelectedItem().toString();

        final Intent startGameIntent = new Intent(this, GamePlayActivity.class);
        startActivity(startGameIntent);
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    public boolean loadRules(){
        return false;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    public boolean loadDeck(){
        return false;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    private boolean initializeDB(){
//        datasource = new ItemDataSource(this);
//        datasource.open();
        return true;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onResume(){
        super.onResume();
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    @Override
    protected void onPause(){
        super.onPause();
    }
}
