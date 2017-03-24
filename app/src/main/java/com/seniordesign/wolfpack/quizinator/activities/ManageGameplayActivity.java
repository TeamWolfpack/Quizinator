package com.seniordesign.wolfpack.quizinator.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seniordesign.wolfpack.quizinator.adapters.ActivePlayerAdapter;
import com.seniordesign.wolfpack.quizinator.adapters.NextCardAdapter;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.adapters.WagerPlayerAdapter;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.database.Deck;
import com.seniordesign.wolfpack.quizinator.database.Rules;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.messages.Wager;
import com.seniordesign.wolfpack.quizinator.R;

import com.seniordesign.wolfpack.quizinator.messages.Answer;
import com.seniordesign.wolfpack.quizinator.messages.Confirmation;
import com.seniordesign.wolfpack.quizinator.Util;
import com.seniordesign.wolfpack.quizinator.views.CardIcon;
import com.seniordesign.wolfpack.quizinator.wifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.wifiDirect.WifiDirectApp;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_ANSWER_CONFIRMATION_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_END_OF_GAME_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_SEND_CARD_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_SEND_WAGER_ACTIVITY;

public class ManageGameplayActivity extends AppCompatActivity {

    private WifiDirectApp wifiDirectApp;

    private QuizDataSource dataSource;

    private Rules rules;
    private Card currentCard;
    private int cardLimit;

    private int cardsPlayed;
    private int clientsResponded;

    private Spinner nextCardSpinner;
    private Gson gson = new Gson();

    private CountDownTimer gameplayTimerStatic;
    private CountDownTimer gameplayTimerRunning;
    private long gameplayTimerRemaining;

    private android.app.AlertDialog alertDialog;
    private boolean viewingPlayers;

    public static final String TAG = "ACT_MANAGE";

    private ArrayList<Answer> answers;
    private ArrayList<Answer> selectedAnswers;

    private boolean doubleEdgeActive = false;
    private boolean multipleWinners = false;

    private boolean receivedWagers = false;
    private ArrayList<Wager> wagers;
    private boolean gettingWagers = false;

    private android.app.AlertDialog wagerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_gameplay);

        answers = new ArrayList<>();

        setTitle(Constants.HOSTING_GAME);

        wifiDirectApp = (WifiDirectApp)getApplication();
        wifiDirectApp.mManageActivity = this;

        dataSource = new QuizDataSource(this);
        dataSource.open();
//        rules = dataSource.getAllRules().get(dataSource.getAllRules().size()-1); //TODO need to change
//        rules = wifiDirectApp.mGameplayActivity.properties.getRules();
        String ruleSetName = getIntent().getStringExtra(Constants.RULES);
        rules = dataSource.getRuleSetByName(ruleSetName);

        Type listType = new TypeToken<ArrayList<Constants.CARD_TYPES>>(){}.getType();
        List<Constants.CARD_TYPES> cardTypeList = new Gson().fromJson(rules.getCardTypes(), listType);
        Deck deck = dataSource.getFilteredDeck(rules.getDeckId(), cardTypeList, wifiDirectApp.mIsServer);

        cardLimit = Math.min(deck.getCards().size(),rules.getMaxCardCount());

        nextCardSpinner = (Spinner) findViewById(R.id.next_card_spinner);
        NextCardAdapter nextCardAdapter = new NextCardAdapter(this, deck.getCards());
        nextCardSpinner.setAdapter(nextCardAdapter);
        shuffle(null);

        initializeGameTimer(rules.getTimeLimit());
        gameplayTimerRunning = gameplayTimerStatic.start();

        Button highRiskButton = (Button) findViewById(R.id.double_edge);
        if(rules.isDoubleEdgeSword()){
            highRiskButton.setVisibility(View.VISIBLE);
        }else{
            highRiskButton.setVisibility(View.INVISIBLE);
        }
    }

    public void shuffle(View v) {
        ((NextCardAdapter)nextCardSpinner.getAdapter()).shuffle();
        nextCardSpinner.setSelection(0);
    }

    public void sendCard(View v) {
        if(!(cardsPlayed+1 < cardLimit) && !gettingWagers && rules.isLastCardWager()){
            if(gettingWagers)
                return;
            gettingWagers = true;
            String msg = "final_question";
            ConnectionService.sendMessage(MSG_SEND_WAGER_ACTIVITY, msg);
            showWagersDialog(null);
            return;
        }
        clientsResponded=0;
        if(cardsPlayed < cardLimit) {
            currentCard = (Card)nextCardSpinner.getSelectedItem();
            currentCard.setDoubleEdge(doubleEdgeActive);
            String json = gson.toJson(currentCard);
            ConnectionService.sendMessage(MSG_SEND_CARD_ACTIVITY, json);
            cardsPlayed++;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (nextCardSpinner.getCount() > 1) {
                        ((NextCardAdapter)nextCardSpinner.getAdapter())
                                .removeItem(nextCardSpinner.getSelectedItemPosition());
                        nextCardSpinner.setSelection(0);
                    }
                    updateCurrentCardView(currentCard);
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

    public void endGame(View v) {
        if (v == null) {
            gameplayTimerRunning.cancel();
            autoSelectAnswers();
            String json = gson.toJson(rules.getTimeLimit() - gameplayTimerRemaining);
            ConnectionService.sendMessage(MSG_END_OF_GAME_ACTIVITY, json);
            startActivity(new Intent(ManageGameplayActivity.this, MainMenuActivity.class));
            finish();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("End Game")
                .setMessage("Are you sure you want to end the game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endGame(null);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * This is called in ConnectionSerive.onPullInData(...) when a ready message
     * is received.
     */
    public void deviceIsReady(String deviceName) {
        //TODO start the game when all devices are ready
        //or
        //TODO show that the device is ready
    }

    public void showPlayersDialog(View v) {
        if (alertDialog != null)
            alertDialog.dismiss();

        LayoutInflater li = LayoutInflater.from(this);
        final View promptsView = li.inflate(R.layout.fragment_active_players, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle(Constants.PLAYERS_RESPONSE);

        if (v != null) {
            viewingPlayers = true;
            alertDialogBuilder
                    .setTitle(Constants.ACTIVE_PLAYERS)
                    .setPositiveButton(Constants.CLOSE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                            viewingPlayers = false;
                        }
                    });
        } else {
            alertDialogBuilder
                    .setPositiveButton(Constants.NO_WINNER, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                            noPlayerAnsweredFreeOrVerbalResponseCorrectly();
                            sendCard(null);
                            clientsResponded = 0;
                        }
                    });
        }

        alertDialog = alertDialogBuilder.create();
        handlePlayerDialog(promptsView, v == null);
        alertDialog.show();
    }

    /**
     * This is called in ConnectionSerive.onPullInData(...) when a answer message
     * is received.
     */
    public void validateAnswer(Answer answer) {
        if (answer == null)
            return;

        if (answers!=null) {
            answers.add(answer);
        }
        clientsResponded++;

        Log.d(TAG, "Clients responded: " + clientsResponded);
        Log.d(TAG, "Number of Players: " + wifiDirectApp.getConnectedPeers().size());

        Log.d(TAG, "Current Card: " + currentCard.toString());

        if (Boolean.parseBoolean(currentCard.getModeratorNeeded())) {
            if (alertDialog == null || !alertDialog.isShowing()) {
                showPlayersDialog(null);
            } else if (viewingPlayers) {
                alertDialog.cancel();
                showPlayersDialog(null);
            }
            ListView playersList = (ListView) alertDialog.findViewById(R.id.active_player_dialog_list);
            ActivePlayerAdapter playerAdapter = (ActivePlayerAdapter)(playersList.getAdapter());
            playerAdapter.addItem(answer);
        } else {
            checkClientCount();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
        dataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
        dataSource.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (alertDialog != null)
            alertDialog.dismiss();

        wifiDirectApp.onDestroy(TAG);
    }

    private void updateCurrentCardView(Card card) {
        TextView question = (TextView) findViewById(R.id.current_card_question);
            question.setText(card.getQuestion());

        TextView points = (TextView) findViewById(R.id.current_card_points);
            points.setText(getResources().getText(R.string.points) + ": " + card.getPoints());

        Util.updateCardTypeIcon(card, (CardIcon) findViewById(R.id.current_card_type_icon));

        TextView answerLabel = (TextView) findViewById(R.id.current_card_answer_label);
            answerLabel.setText(R.string.correct_answer_label);

        TextView answer = (TextView) findViewById(R.id.current_card_correct_answer);
            answer.setText(card.getCorrectAnswer());
    }

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

    private void noPlayerAnsweredFreeOrVerbalResponseCorrectly() {
        Log.d(TAG,"Moderator determined no one answered correctly");
        if(!multipleWinners) {

            for(Answer answer : answers){
                if(currentCard.isDoubleEdge() && !answer.getAnswer().isEmpty()) {
                    String confirmation = gson.toJson(new Confirmation(answer.getAddress(), false));
                    ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
                }
                else if(!currentCard.isDoubleEdge()){
                    String confirmation = gson.toJson(new Confirmation(answer.getAddress(), false));
                    ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
                }
            }
        }
        else{
            for (Answer answer : answers) {
                if(!(selectedAnswers.contains(answer))) {
                    if(currentCard.isDoubleEdge() && !answer.getAnswer().isEmpty()) {
                        String confirmation = gson.toJson(new Confirmation(answer.getAddress(), false));
                        ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
                    }
                    else if(!currentCard.isDoubleEdge()){
                        String confirmation = gson.toJson(new Confirmation(answer.getAddress(), false));
                        ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
                    }
                }
            }
        }
        answers = new ArrayList<>();
    }

    private void selectAndRespondToSelectedAnswer(Answer selectedAnswer){
        Log.d(TAG,"selecting Selected Answer");

        // Selected Player gets the points
        String confirmation = gson.toJson(new Confirmation(selectedAnswer.getAddress(), true));
        ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);

        for (WifiP2pDevice device : wifiDirectApp.getConnectedPeers()) {
            if (device.deviceAddress.equals(selectedAnswer.getAddress()))
                continue;

            for(Answer answer : answers){
                if( device.deviceAddress.equals(selectedAnswer.getAddress())){
                    if(currentCard.isDoubleEdge() && !answer.getAnswer().isEmpty()) {
                        confirmation = gson.toJson(new Confirmation(device.deviceAddress, false));
                        ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
                    }
                    else if(!currentCard.isDoubleEdge()){
                        confirmation = gson.toJson(new Confirmation(device.deviceAddress, false));
                        ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
                    }
                }
            }
        }
        answers = new ArrayList<>();
        alertDialog.cancel();
    }

    private long selectAndRespondToFastestAnswer(){
        Log.d(TAG,"selecting Fastest Answer");
        Answer fastestCorrectAnswer = null;

        for(Answer answerI : answers){
            if(currentCard.getCorrectAnswer().equals(answerI.getAnswer())){
                if(fastestCorrectAnswer==null || answerI.getTimeTaken()<fastestCorrectAnswer.getTimeTaken()){
                    if(fastestCorrectAnswer!=null){
                        Log.d(TAG,"Setting Fastest Answer");
                        if(currentCard.isDoubleEdge() && !fastestCorrectAnswer.getAnswer().isEmpty()) {
                            String confirmation = gson.toJson(new Confirmation(fastestCorrectAnswer.getAddress(), false));
                            ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
                        }
                    }
                    fastestCorrectAnswer = answerI;
                }
                else{
                    if(currentCard.isDoubleEdge() && !answerI.getAnswer().isEmpty()) {
                        String confirmation = gson.toJson(new Confirmation(answerI.getAddress(), false));
                        ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
                    }
                }
            }
            else{
                if(currentCard.isDoubleEdge() && !answerI.getAnswer().isEmpty()) {
                    String confirmation = gson.toJson(new Confirmation(answerI.getAddress(), false));
                    ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
                }
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

    private void handlePlayerDialog(View dialogView, boolean mustPickWinner) {
        selectedAnswers = new ArrayList<Answer>();
        multipleWinners = false;
        final ListView playersDialogList = (ListView) dialogView.findViewById(R.id.active_player_dialog_list);

        TextView correctAnswerLabel = (TextView) dialogView.findViewById(R.id.dialog_correct_answer_label);
        TextView correctAnswer = (TextView) dialogView.findViewById(R.id.dialog_correct_answer);
        if (currentCard != null)
            correctAnswer.setText(currentCard.getCorrectAnswer());

        ActivePlayerAdapter playerAdapter = new ActivePlayerAdapter(dialogView.getContext(), new ArrayList<Answer>(), mustPickWinner);
        if (!mustPickWinner) {
            List<Answer> players = new ArrayList<>();
            for (WifiP2pDevice device : wifiDirectApp.getConnectedPeers()) {
                players.add(new Answer(device.deviceName, device.deviceAddress, null, 0));
            }
            playerAdapter = new ActivePlayerAdapter(dialogView.getContext(), players, mustPickWinner);

            correctAnswerLabel.setVisibility(View.GONE);
            correctAnswer.setVisibility(View.GONE);
        }
        playersDialogList.setAdapter(playerAdapter);

        if (mustPickWinner) {

            if(rules.getMultipleWinners())
            {
                //Allow for multiple correct answers
                playersDialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Answer answer = (Answer) playersDialogList.getAdapter().getItem(i);
                        selectAndRespondToMultipleSelectedAnswers(answer);
                    }
                });
            }
            else {
                playersDialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Answer answer = (Answer) playersDialogList.getAdapter().getItem(i);
                        selectAndRespondToSelectedAnswer(answer);
                        sendCard(null);
                        clientsResponded = 0;
                    }
                });
            }




        }
    }

    public int checkClientCount() {
        if (clientsResponded >= wifiDirectApp.getConnectedPeers().size()) {
            autoSelectAnswers();
            sendCard(null);
            clientsResponded = 0;
        }
        return wifiDirectApp.getConnectedPeers().size();
    }

    public void doubleEdgeSelect(View v) {
        if(rules.isDoubleEdgeSword()){
            doubleEdgeActive = !doubleEdgeActive;
            if(doubleEdgeActive){
                v.setBackgroundColor(ContextCompat.getColor(this,R.color.colorDoublePointsActive));
            }else{
                v.setBackgroundColor(ContextCompat.getColor(this,android.R.color.transparent));
            }
        }

    }

    private void selectAndRespondToMultipleSelectedAnswers(Answer selectedAnswer){
        if(selectedAnswers == null){
            selectedAnswers = new ArrayList<Answer>();
        }
        if(selectedAnswers.contains(selectedAnswer))
            return;
        Log.d(TAG,"selecting Selected Answer");

        // Selected Player gets the points
        String confirmation = gson.toJson(new Confirmation(selectedAnswer.getAddress(), true));
        ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("Close");
        multipleWinners = true;
        selectedAnswers.add(selectedAnswer);
    }

    private void selectAndRespondToAnswers(){
        for(Answer answerI : answers){
            if(answerI.getAnswer().equals(currentCard.getCorrectAnswer())){
                String confirmation = gson.toJson(new Confirmation(answerI.getAddress(), true));
                ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
            }
            else if(currentCard.isDoubleEdge() && !answerI.getAnswer().isEmpty()){
                String confirmation = gson.toJson(new Confirmation(answerI.getAddress(), false));
                ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
            }
            else if(!currentCard.isDoubleEdge()) {
                String confirmation = gson.toJson(new Confirmation(answerI.getAddress(), false));
                ConnectionService.sendMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation);
            }

            answers = new ArrayList<>();
        }
    }

    private void autoSelectAnswers(){
        if(rules.getMultipleWinners()){
            selectAndRespondToAnswers();
        }
        else{

            selectAndRespondToFastestAnswer();
        }
    }

    public void receiveWager(Wager wager){
        System.out.println("ReceivedWager");
        if(wagers==null)
            wagers = new ArrayList<Wager>();
        wagers.add(wager);
        ListView wagersList = (ListView) wagerDialog.findViewById(R.id.wager_player_dialog_list);
        if(wagersList!=null){
            WagerPlayerAdapter playerAdapter = (WagerPlayerAdapter) wagersList.getAdapter();
            playerAdapter.addItem(wager);
        }
        if(wagers.size() >= wifiDirectApp.getConnectedPeers().size()){
            receivedWagers = true;
            wagerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
        }

    }

    public void showWagersDialog(View v) {
        if(wagers==null)
            wagers = new ArrayList<Wager>();
        if (wagerDialog != null)
            wagerDialog.dismiss();

        LayoutInflater li = LayoutInflater.from(this);
        final View promptsView = li.inflate(R.layout.fragment_player_wagers, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle(Constants.PLAYER_WAGERS)
                .setPositiveButton(Constants.SEND_CARD, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if(receivedWagers) {
                            dialog.cancel();
                            sendCard(null);
                        }
                    }
                });

        wagerDialog = alertDialogBuilder.create();
        wagerDialog.show();
        wagerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this,R.color.colorGrayedOut));
        ListView wagersList = (ListView) wagerDialog.findViewById(R.id.wager_player_dialog_list);
        WagerPlayerAdapter playerAdapter = new WagerPlayerAdapter(wagerDialog.getContext(), new ArrayList<Wager>());
        wagersList.setAdapter(playerAdapter);
    }
}
