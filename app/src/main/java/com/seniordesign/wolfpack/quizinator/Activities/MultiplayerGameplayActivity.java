package com.seniordesign.wolfpack.quizinator.Activities;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.Database.HighScore.HighScoresDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.Fragments.MultipleChoiceAnswerFragment;
import com.seniordesign.wolfpack.quizinator.Fragments.TrueFalseChoiceAnswerFragment;
import com.seniordesign.wolfpack.quizinator.R;

public class MultiplayerGameplayActivity extends AppCompatActivity implements TrueFalseChoiceAnswerFragment.OnFragmentInteractionListener,
        MultipleChoiceAnswerFragment.OnFragmentInteractionListener{

    private Rules rules;
    private Card currentCard;

    private HighScoresDataSource highScoresDataSource;
git 
    private int deckIndex;
    private int deckLength;
    private int score;

    private CountDownTimer gamePlayTimerStatic;
    private CountDownTimer gamePlayTimerRunning;
    private long gamePlayTimerRemaining;

    private CountDownTimer cardTimerStatic;
    private CountDownTimer cardTimerRunning;

    private CountDownTimer cardTimerAreaBackgroundStatic;
    private CountDownTimer cardTimerAreaBackgroundRunning;
    private int r;
    private int g;
    private int b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
    }
}
