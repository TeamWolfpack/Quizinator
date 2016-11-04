package com.seniordesign.wolfpack.quizinator.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_SEND_CARD_ACTIVITY;

public class ManageGameplayActivity extends AppCompatActivity {

    private DeckDataSource deckDataSource;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_gameplay);

        deckDataSource = new DeckDataSource(this);
        deckDataSource.open();
    }

    public void sendCard(View v) {
        Card card = deckDataSource.getAllDecks().get(0).getCards().get(0);
        String json = gson.toJson(card);
        ConnectionService.sendMessage(MSG_SEND_CARD_ACTIVITY, json);
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
