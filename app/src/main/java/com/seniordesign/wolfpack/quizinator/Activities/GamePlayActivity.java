package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.net.Uri;
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
     * @author farrowc (10/11/2016)
     */
    public void setRules(Rules rules){
        this.rules = rules;
    }

    /*
     * @author farrowc (10/11/2016)
     */
    public void setDeck(Deck deck){
        this.deck = deck;
    }

    /*
     * @author farrowc (10/11/2016)
     */
    private void beginGamePlay() {
        //Deck stuff
        deck = new Deck();
        //deckLength = Math.min(deck.getCardTypes().length,rules.getMaxCardCount());
        deckLength = 4;

        //Start Gameplay loop
        switchToNewCard(deck, deckIndex);
    }

    /*
     * @author farrowc (10/11/2016)
     */
    private void switchToNewCard(Deck deck, int deckIndex) {
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
    }

    private void endGamePlay() {
        final Intent intent = new Intent(this, EndOfGameplayActivity.class);

        //TODO Check score against high score

        //TODO If score is higher than high score, replace it

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
        return null;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    private boolean addToGameStatsDB(){
        HighScores h = highScoresDataSource.getAllHighScores().get(0);
        if(score > h.getBestScore()){
            h.setBestScore(score);
        }
        if(){

        }
        return false;
    }

    /*
     * @author kuczynskij (09/28/2016)
     */
    private boolean initializeDB(){
        int positiveDBConnections = 0;
        rulesDataSource = new RulesDataSource(this);
        if(rulesDataSource.open()){
            positiveDBConnections++;
        }
        rules = rulesDataSource.getAllItems().get(0);

        highScoresDataSource = new HighScoresDataSource(this);
        highScoresDataSource.open();

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

    public void answerClicked(View v){
        Button clickedButton = (Button)v;
        String answer = clickedButton.getText().toString();
        this.onFragmentInteraction(answer);
        switchToNewCard(deck,deckIndex);
    }

    @Override
    public void onFragmentInteraction(String answer) {
        if(answer.equals("True")){// == currentCard.isAnswer()) {
            Toast.makeText(this, "Beautiful!", Toast.LENGTH_SHORT).show();
            score++;
        }
        else{
            Toast.makeText(this, "You Suck!", Toast.LENGTH_SHORT).show();
        }
    }
}
