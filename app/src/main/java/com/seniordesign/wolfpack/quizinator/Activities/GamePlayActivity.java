package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Card.TFCard;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScores;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScoresDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.Fragments.TrueFalseChoiceAnswerFragment;
import com.seniordesign.wolfpack.quizinator.R;

/**
 * The game play activity is...
 * @creation 09/28/2016
 */
public class GamePlayActivity
        extends AppCompatActivity
        implements TrueFalseChoiceAnswerFragment.OnFragmentInteractionListener {

    private Rules rules;
    private Deck deck;
    private TFCard currentCard;

    private RulesDataSource rulesDataSource;
    private HighScoresDataSource highScoresDataSource;

    int deckIndex;
    int deckLength;
    int score;

    /*
     * @author kuczynskij (09/28/2016)
     * @author farrowc (10/11/2016)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        initializeDB();
        beginGamePlay();
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    @Override
    protected void onResume(){
        super.onResume();
        rulesDataSource.open();
        highScoresDataSource.open();
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    @Override
    protected void onPause(){
        super.onPause();
        rulesDataSource.close();
        highScoresDataSource.close();
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    private void cleanUpOnExit(){
        rulesDataSource.close();
        highScoresDataSource.close();
        this.finish();
        //remove if left unused due to creation of new intent
    }

    /*
     * @author farrowc (??/??/2016)
     */
    @Override
    public void onFragmentInteraction(String answer) {
        if(answer == currentCard.getCorrectAnswer()) {
            quickToast("Beautiful!");
            score++;
        }
        else{
            quickToast("You Suck!");
        }
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    private boolean quickToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return true;
    }

    /*
     * @author farrowc (10/11/2016)
     * @author kuczynskij (10/13/2016)
     */
    public boolean setRules(Rules rules){
        this.rules = rules;
        return true;
    }

    /*
     * @author farrowc (10/11/2016)
     * @author kuczynskij (10/13/2016)
     */
    public boolean setDeck(Deck deck){
        this.deck = deck;
        return true;
    }

    /*
     * @author farrowc (10/11/2016)
     * @author kuczynskij (10/13/2016)
     */
    private void beginGamePlay() {
        //Deck stuff
        deck = new Deck();
        //deckLength = Math.min(deck.getCardTypes().length,rules.getMaxCardCount());
        deckLength = 4;

        //TODO -> initialize gameplay timer


        //Start Gameplay loop
        switchToNewCard(deck, deckIndex);
    }

    /*
     * @author farrowc (10/11/2016)
     */
    private String switchToNewCard(Deck deck, int deckIndex) {
        if(deckLength>deckIndex) {
            ((TextView) findViewById(R.id.scoreText)).setText("Score: " + score);

            //TODO Here set card to the card at the position of deckIndex

            currentCard = new TFCard();
            currentCard.setQuestion("Hello World");
            currentCard.setCorrectAnswer("True");


            showCard(currentCard);
            this.deckIndex++;
        }
        else{
            endGamePlay();
        }
        return "";
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    private void endGamePlay() {
        final Intent intent =
                new Intent(this, EndOfGameplayActivity.class);
        checkGameStatsAgainstHighScoresDB();
        startActivity(intent);
    }

    /*
     * @author farrowc (10/11/2016)
     */
    private void showCard(Card card) {
        getSupportFragmentManager().
                beginTransaction()
                .replace(R.id.answerArea, new TrueFalseChoiceAnswerFragment())
                .commit();
        ((TextView)findViewById(R.id.questionTextArea))
                .setText(card.getQuestion());
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    public String onButtonClick(View view){
        //TODO button handling seems to be in the fragment, remove me if left unused

        return null;
    }

    /*
     * @author farrowc (??/??/2016)
     */
    public void answerClicked(View v){
        Button clickedButton = (Button)v;
        String answer = clickedButton.getText().toString();
        this.onFragmentInteraction(answer);
        switchToNewCard(deck,deckIndex);
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    private boolean checkGameStatsAgainstHighScoresDB(){
        HighScores h = highScoresDataSource
                .getAllHighScores().get(0);
        //TODO -> there is a lot more we need to check for and add
        if(score > h.getBestScore()){
            h.setDeckName(deck.getDeckName());
            h.setBestScore(score);
        }
        return false;
    }

    /*
     * @author kuczynskij (10/13/2016)
     */
    private boolean initializeDB(){
        int positiveDBConnections = 0;
        rulesDataSource = new RulesDataSource(this);
        if(rulesDataSource.open()){
            positiveDBConnections++;
            rules = rulesDataSource.getAllRules().get(0);
        }
        highScoresDataSource = new HighScoresDataSource(this);
        if(highScoresDataSource.open()){
            positiveDBConnections++;
        }
        return (positiveDBConnections == 2);
    }
}
