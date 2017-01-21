package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Adapters.CardAdapter;
import com.seniordesign.wolfpack.quizinator.Adapters.NextCardAdapter;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Database.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck;
import com.seniordesign.wolfpack.quizinator.Database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.Messages.Answer;
import com.seniordesign.wolfpack.quizinator.Messages.Confirmation;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import java.util.ArrayList;
import java.util.List;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_ANSWER_CONFIRMATION_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_END_OF_GAME_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_SEND_CARD_ACTIVITY;

public class ManageGameplayActivity extends AppCompatActivity {

    private WifiDirectApp wifiDirectApp;

    private QuizDataSource dataSource;
    private RulesDataSource rulesDataSource;

    private Rules rules;
    private Deck deck;
    private Card currentCard;
    private int cardLimit;

    private int cardsPlayed;
    private int clientsResponded;

    private Spinner nextCardSpinner;
    private Gson gson = new Gson();

    private CountDownTimer gameplayTimerStatic;
    private CountDownTimer gameplayTimerRunning;
    private long gameplayTimerRemaining;

    public static final String TAG = "ACT_MANAGE";

    private ArrayList<Answer> answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_gameplay);

        answers = new ArrayList<>();

        setTitle(Constants.HOSTING_GAME);

        wifiDirectApp = (WifiDirectApp)getApplication();
        wifiDirectApp.mManageActivity = this;

        rulesDataSource = new RulesDataSource(this);
        rulesDataSource.open();
        rules = rulesDataSource.getAllRules().get(rulesDataSource.getAllRules().size()-1);

        dataSource = new QuizDataSource(this);
        dataSource.open();
        deck = dataSource.getDeckWithId(rules.getDeckId()).filter(rules);

        cardLimit = Math.min(deck.getCards().size(),rules.getMaxCardCount());

        nextCardSpinner = (Spinner) findViewById(R.id.next_card_spinner);
        NextCardAdapter nextCardAdapter = new NextCardAdapter(this, deck.getCards());
        nextCardSpinner.setAdapter(nextCardAdapter);

        initializeGameTimer(rules.getTimeLimit());
        gameplayTimerRunning = gameplayTimerStatic.start();
    }

    private void shuffle(View v) {
        //TODO
    }

    public void sendCard(View v) {
        clientsResponded=0;
        if(cardsPlayed < cardLimit) {
            currentCard = (Card)nextCardSpinner.getSelectedItem();
            String json = gson.toJson(currentCard);
            ConnectionService.sendMessage(MSG_SEND_CARD_ACTIVITY, json);
            cardsPlayed++;
            ((NextCardAdapter)nextCardSpinner.getAdapter())
                    .removeItem(nextCardSpinner.getSelectedItemPosition());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nextCardSpinner.setSelection(0);
                }
            });
        } else {
            endGame(null);
        }
        if (cardsPlayed == cardLimit) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nextCardSpinner.setEnabled(false);
                    findViewById(R.id.shuffle).setEnabled(false);
                    findViewById(R.id.send_card).setEnabled(false);
                }
            });
        }
    }

    /*
     * @author leonard (11/5/2016)
     */
    public void endGame(View v) {
        gameplayTimerRunning.cancel();
        selectAndRespondToFastestAnswer();
        String json = gson.toJson(rules.getTimeLimit() - gameplayTimerRemaining);
        ConnectionService.sendMessage(MSG_END_OF_GAME_ACTIVITY, json);
        Intent i = new Intent(ManageGameplayActivity.this, MainMenuActivity.class);
        startActivity(i);
        finish();
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
        //boolean correct = currentCard.getCorrectAnswer().equals(answer.getAnswer());

        //String playerName = answer.getDeviceName();
        //String playerAddress = answer.getAddress();

        //String confirmation = gson.toJson(new Confirmation(playerAddress, correct));
        //ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
        if(answers!=null && answer!= null) {
            answers.add(answer);
        }
        clientsResponded++;

        Log.d(TAG, "Clients responded: " + clientsResponded);
        Log.d(TAG, "Number of Peers: " + wifiDirectApp.mPeers.size());

        checkClientCount();
    }

    /*
     * @author leonard (11/4/2016)
     */
    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
        rulesDataSource.open();
    }

    /*
     * @author leonard (11/4/2016)
     */
    @Override
    protected void onPause() {
        super.onPause();
        //gameplayTimerRunning.cancel();
        //gameplayTimerStatic.cancel();
        dataSource.close();
        rulesDataSource.close();
    }

    /*
     * @author leonard (11/4/2016)
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiDirectApp.disconnectFromGroup();
        wifiDirectApp.mManageActivity = null;
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

    private long selectAndRespondToFastestAnswer(){
        Log.d(TAG,"selecting Fastest Answer");
        Answer fastestCorrectAnswer = null;

        for(Answer answerI : answers){
            if(currentCard.getCorrectAnswer().equals(answerI.getAnswer())){
                if(fastestCorrectAnswer==null || answerI.getTimeTaken()<fastestCorrectAnswer.getTimeTaken()){
                    if(fastestCorrectAnswer!=null){
                        Log.d(TAG,"Setting Fastest Answer");
                        String confirmation = gson.toJson(new Confirmation(fastestCorrectAnswer.getAddress(), false));
                        ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
                    }
                    fastestCorrectAnswer = answerI;
                }
                else{
                    String confirmation = gson.toJson(new Confirmation(answerI.getAddress(), false));
                    ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
                }
            }
            else{
                String confirmation = gson.toJson(new Confirmation(answerI.getAddress(), false));
                ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
            }
        }
        Log.d(TAG,"Found Fastest Answer");
        if(fastestCorrectAnswer!=null) {
            Log.d(TAG,"Sending Message To Fastest Answer");
            String confirmation = gson.toJson(new Confirmation(fastestCorrectAnswer.getAddress(), true));
            ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
        }
        answers = new ArrayList<>();
        return fastestCorrectAnswer == null ? -1 : fastestCorrectAnswer.getTimeTaken();
    }

    public int checkClientCount() {
        if (clientsResponded >= wifiDirectApp.getConnectedPeers().size()) {
            selectAndRespondToFastestAnswer();
            sendCard(null);
            clientsResponded = 0;
        }
        return wifiDirectApp.getConnectedPeers().size();
    }
}
