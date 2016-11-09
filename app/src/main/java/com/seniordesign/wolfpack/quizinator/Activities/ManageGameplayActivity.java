package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.Answer;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_ANSWER_CONFIRMATION_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_END_OF_GAME_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_SEND_CARD_ACTIVITY;

public class ManageGameplayActivity extends AppCompatActivity {

    private WifiDirectApp wifiDirectApp;

    private DeckDataSource deckDataSource;
    private RulesDataSource rulesDataSource;

    private Deck deck;
    private Card currentCard;
    private int currentCardPosition;
    private int cardLimit;

    private int clientsResponded;

    private Rules rules;

    private Gson gson = new Gson();

    private CountDownTimer gameplayTimerStatic;
    private CountDownTimer gameplayTimerRunning;
    private long gameplayTimerRemaining;

    public static final String TAG = "ACT_MANAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_gameplay);

        setTitle("Hosting Game");

        wifiDirectApp = (WifiDirectApp)getApplication();
        wifiDirectApp.mManageActivity = this;

        deckDataSource = new DeckDataSource(this);
        deckDataSource.open();
        deck = deckDataSource.getAllDecks().get(0);

        rulesDataSource = new RulesDataSource(this);
        rulesDataSource.open();
        rules = rulesDataSource.getAllRules().get(rulesDataSource.getAllRules().size()-1);

        cardLimit = Math.min(deck.getCards().size(),rules.getMaxCardCount());

        initializeGameTimer(rules.getTimeLimit());
        gameplayTimerRunning = gameplayTimerStatic.start();
    }

    /*
     * @author leonard (11/5/2016)
     */
    public void sendCard(View v) {
        if(currentCardPosition<cardLimit) {
            currentCard = deck.getCards().get(currentCardPosition);
            currentCardPosition++;
            String json = gson.toJson(currentCard);
            ConnectionService.sendMessage(MSG_SEND_CARD_ACTIVITY, json);
        }
        else {
            endGame(null);
        }
    }

    /*
     * @author leonard (11/5/2016)
     */
    public void endGame(View v) {
        gameplayTimerRunning.cancel();
        String json = gson.toJson(rules.getTimeLimit() - gameplayTimerRemaining);
        ConnectionService.sendMessageNotDelayed(MSG_END_OF_GAME_ACTIVITY, json);
        Intent i = new Intent(ManageGameplayActivity.this, MainMenuActivity.class);
        startActivity(i);
    }

    /**
     * This is called in ConnectionSerive.onPullInData(...) when a ready message
     * is received.
     */
    /*
     * @author leonard (11/5/2016)
     */
    public void deviceIsReady(String deviceName) {
        //TODO show that the device is ready
    }

    /**
     * This is called in ConnectionSerive.onPullInData(...) when a answer message
     * is received.
     */
    /*
     * @author leonard (11/5/2016)
     */
    public void validateAnswer(Answer answer) {
        boolean correct = currentCard.getCorrectAnswer().equals(answer.getAnswer());

        String playerName = answer.getDeviceName();
        //TODO send confirmation to the specific player

        ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, String.valueOf(correct));
        clientsResponded++;
        if(clientsResponded==wifiDirectApp.mPeers.size()){
            sendCard(null);
            clientsResponded=0;
        }

    }

    /*
     * @author leonard (11/4/2016)
     */
    @Override
    protected void onResume() {
        super.onResume();
        deckDataSource.open();
        rulesDataSource.open();
    }

    /*
     * @author leonard (11/4/2016)
     */
    @Override
    protected void onPause() {
        gameplayTimerRunning.cancel();
        super.onPause();
        deckDataSource.close();
        rulesDataSource.close();
    }

    /*
     * @author farrowc 10/14/2016
     */
    private boolean initializeGameTimer(long time) {
        System.out.println("Time: "+time);
        gameplayTimerStatic = new CountDownTimer(time, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.gamePlayTimeHostText)).setText(
                        "Game Time: " + millisUntilFinished / 60000 + ":" + millisUntilFinished / 1000 % 60
                );
                gameplayTimerRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                endGame(null);
            }
        };
        return true;
    }
}
