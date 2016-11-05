package com.seniordesign.wolfpack.quizinator.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.Answer;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_ANSWER_CONFIRMATION_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_SEND_CARD_ACTIVITY;

public class ManageGameplayActivity extends AppCompatActivity {

    private WifiDirectApp wifiDirectApp;

    private DeckDataSource deckDataSource;

    private Card currentCard;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_gameplay);

        wifiDirectApp = (WifiDirectApp)getApplication();
        wifiDirectApp.mManageActivity = this;

        deckDataSource = new DeckDataSource(this);
        deckDataSource.open();
    }

    public void sendCard(View v) {
        currentCard = deckDataSource.getAllDecks().get(0).getCards().get(0);
        String json = gson.toJson(currentCard);
        ConnectionService.sendMessage(MSG_SEND_CARD_ACTIVITY, json);
    }

    public void deviceIsReady(String deviceName) {
        //TODO show that the device is ready
    }

    public void validateAnswer(Answer answer) {
        boolean correct = currentCard.getCorrectAnswer().equals(answer.getAnswer());
        ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, String.valueOf(correct));
    }

    /*
     * @author leonard (11/4/2016)
     */
    @Override
    protected void onResume() {
        super.onResume();
        deckDataSource.open();
    }

    /*
     * @author leonard (11/4/2016)
     */
    @Override
    protected void onPause() {
        super.onPause();
        deckDataSource.close();
    }
}
