package com.seniordesign.wolfpack.quizinator.wifiDirect;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.database.Rules;
import com.seniordesign.wolfpack.quizinator.messages.Answer;
import com.seniordesign.wolfpack.quizinator.messages.Confirmation;
import com.seniordesign.wolfpack.quizinator.messages.QuizMessage;
import com.seniordesign.wolfpack.quizinator.messages.Wager;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_ANSWER_CONFIRMATION_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_ANSWER_CONFIRMATION_HANDSHAKE_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_BROKEN_CONN;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_DISCONNECT_FROM_ALL_PEERS;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_END_OF_GAME_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_FINISH_CONNECT;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_NEW_CLIENT;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_NULL;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_PEER_HAS_LEFT;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_PLAYER_READY_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_PULLIN_DATA;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_PUSHOUT_DATA;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_SELECT_ERROR;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_SEND_ANSWER_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_SEND_CARD_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_SEND_RULES_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_SEND_WAGER_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_SEND_WAGER_CONFIRMATION_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_STARTCLIENT;
import static com.seniordesign.wolfpack.quizinator.wifiDirect.MessageCodes.MSG_STARTSERVER;

/**
 * Handles the messages over the WifiDirect connection.
 */
public class MessageHandler extends Handler {

    private static final String TAG = "MsgHnd";

    private ConnectionManager mConnMan;
    private WifiDirectApp wifiDirectApp;

    //Synchronized List
    //Must lock with synchronize block when iterating it
    //http://stackoverflow.com/a/9468307
    private List<String> scoreHandshakes;
    private CountDownTimer handshakeTimeout;

    MessageHandler(Looper looper, ConnectionManager connMan) {
        super(looper);
        mConnMan = connMan;
        wifiDirectApp = WifiDirectApp.getInstance();
        scoreHandshakes = Collections.synchronizedList(new ArrayList<String>());
    }

    @Override
    public void handleMessage(Message msg) {
        Log.d(TAG, "processMessage: message - " + msg.toString());
        switch (msg.what) {
            case MSG_NULL:
                break;
            case MSG_STARTSERVER:
                if (mConnMan.startServerSelector() >= 0)
                    sendMessageToUpDatePeerListFragment();
                break;
            case MSG_STARTCLIENT:
                if (mConnMan.startClientSelector((String) msg.obj) >= 0)
                    sendMessageToUpDatePeerListFragment();
                break;
            case MSG_NEW_CLIENT:
                mConnMan.onNewClient((SocketChannel) msg.obj);
                break;
            case MSG_FINISH_CONNECT:
                mConnMan.onFinishConnect((SocketChannel) msg.obj);
                break;
            case MSG_PULLIN_DATA:
                onPullInData(msg.getData());
                break;
            case MSG_PUSHOUT_DATA:
                mConnMan.pushOutData((String) msg.obj);
                break;
            case MSG_SEND_RULES_ACTIVITY:
                mConnMan.pushOutData(createQuizMessage(MSG_SEND_RULES_ACTIVITY, (String) msg.obj));
                break;
            case MSG_SEND_CARD_ACTIVITY:
                scoreHandshakes.clear();
                mConnMan.pushOutData(createQuizMessage(MSG_SEND_CARD_ACTIVITY, (String) msg.obj));
                break;
            case MSG_PLAYER_READY_ACTIVITY:
                mConnMan.pushOutData(createQuizMessage(MSG_PLAYER_READY_ACTIVITY, (String) msg.obj));
                break;
            case MSG_SEND_ANSWER_ACTIVITY:
                mConnMan.pushOutData(createQuizMessage(MSG_SEND_ANSWER_ACTIVITY, (String) msg.obj));
                break;
            case MSG_ANSWER_CONFIRMATION_ACTIVITY:
                pushConfirmationOut((String) msg.obj);
                break;
            case MSG_ANSWER_CONFIRMATION_HANDSHAKE_ACTIVITY:
                mConnMan.pushOutData(createQuizMessage(MSG_ANSWER_CONFIRMATION_HANDSHAKE_ACTIVITY, (String) msg.obj));
                break;
            case MSG_END_OF_GAME_ACTIVITY:
                mConnMan.pushOutData(createQuizMessage(MSG_END_OF_GAME_ACTIVITY, (String) msg.obj));
                break;
            case MSG_SEND_WAGER_ACTIVITY:
                mConnMan.pushOutData(createQuizMessage(MSG_SEND_WAGER_ACTIVITY, (String) msg.obj));
                break;
            case MSG_SEND_WAGER_CONFIRMATION_ACTIVITY:
                mConnMan.pushOutData(createQuizMessage(MSG_SEND_WAGER_CONFIRMATION_ACTIVITY, (String) msg.obj));
                break;
            case MSG_SELECT_ERROR:
                Log.d(TAG, "received an error related to the connection manager");
                break;
            case MSG_BROKEN_CONN:
                mConnMan.onBrokenConnection((SocketChannel) msg.obj);
                break;
            case MSG_DISCONNECT_FROM_ALL_PEERS:
                mConnMan.pushOutData(createQuizMessage(MSG_DISCONNECT_FROM_ALL_PEERS, (String) msg.obj));
                break;
            case MSG_PEER_HAS_LEFT:
                mConnMan.pushOutData(createQuizMessage(MSG_PEER_HAS_LEFT, (String) msg.obj));
                break;
            default:
                break;
        }
    }

    private void sendMessageToUpDatePeerListFragment() {
        if (wifiDirectApp.mHomeActivity != null)
            wifiDirectApp.mHomeActivity.onConnectionInfoAvailable(wifiDirectApp.mP2pInfo);
    }

    private void pushConfirmationOut(String data) {
        Log.d(TAG, "pushConfirmationOut: " + data);
        Confirmation confirmation = new Gson().fromJson(data,
                Confirmation.class);
        mConnMan.publishDataToSingleClient(
                createQuizMessage(
                        MSG_ANSWER_CONFIRMATION_ACTIVITY,
                        String.valueOf(confirmation.getConfirmation())),
                confirmation.getClientAddress());
        scoreHandshakes.add(confirmation.getClientAddress());
        resetHandshakeTimout();
    }

    private void resetHandshakeTimout() {
        if (handshakeTimeout != null)
            handshakeTimeout.cancel();

        handshakeTimeout = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long l) {
                if (handshakesAreDone())
                    cancel();
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Handshake Timeout");
                if (!handshakesAreDone()) {
                    return;
                }
                scoreHandshakes.clear();
                wifiDirectApp.mManageActivity.handshakesComplete();
            }
        }.start();
    }

    /**
     * Creates a quiz message from passed in opcode and message.
     * @param code opcode of quiz message
     * @param message appended message
     * @return gsonified quiz message
     */
    private String createQuizMessage(int code, String message) {
        return new Gson().toJson(new QuizMessage(code, message));
    }

    /**
     * Handles the message type: MSG_PULLIN_DATA
     */
    private String onPullInData(Bundle b){
        String data = b.getString("DATA");
        Log.d(TAG, "onPullInData: received message - " + data);
        Gson gson = new Gson();
        List<QuizMessage> messages = parseInData(data);
        for (QuizMessage msg: messages) {
            String message = msg.getMessage();
            switch(msg.getCode()){
                case MSG_SEND_RULES_ACTIVITY:
                    wifiDirectApp.mHomeActivity.startMultiplayerGamePlay(
                            gson.fromJson(message, Rules.class));
                    break;
                case MSG_SEND_CARD_ACTIVITY:
                    wifiDirectApp.mGameplayActivity.receivedNextCard(
                            gson.fromJson(message, Card.class));
                    break;
                case MSG_PLAYER_READY_ACTIVITY:
                    if (wifiDirectApp.mManageActivity != null)
                        wifiDirectApp.mManageActivity.deviceIsReady(message);
                    break;
                case MSG_SEND_ANSWER_ACTIVITY:
                    wifiDirectApp.mManageActivity.validateAnswer(
                            gson.fromJson(message, Answer.class));
                    break;
                case MSG_ANSWER_CONFIRMATION_ACTIVITY:
                    wifiDirectApp.mGameplayActivity.answerConfirmed(
                            Boolean.parseBoolean(message));
                    break;
                case MSG_ANSWER_CONFIRMATION_HANDSHAKE_ACTIVITY:
                    if (scoreHandshakes.remove(message) && handshakesAreDone()) {
                        wifiDirectApp.mManageActivity.handshakesComplete();
                    }
                    break;
                case MSG_END_OF_GAME_ACTIVITY:
                    wifiDirectApp.mGameplayActivity.endGamePlay(
                            Long.parseLong(message));
                    break;
                case MSG_SEND_WAGER_ACTIVITY:
                    wifiDirectApp.mGameplayActivity.createWager();
                    break;
                case MSG_SEND_WAGER_CONFIRMATION_ACTIVITY:
                    wifiDirectApp.mManageActivity.receiveWager(gson.fromJson(message, Wager.class));
                    break;
                case MSG_DISCONNECT_FROM_ALL_PEERS:
                    if(wifiDirectApp.mHomeActivity != null){
                        wifiDirectApp.mHomeActivity.disconnect();
                        wifiDirectApp.mHomeActivity.finish();
                    }
                    break;
                case MSG_PEER_HAS_LEFT:
                    if(wifiDirectApp.mGameplayActivity != null){
                        Toolbar toolbar = (Toolbar) wifiDirectApp.mGameplayActivity.findViewById(R.id.toolbar);
                        Toast t = Toast.makeText(wifiDirectApp.mGameplayActivity, message, Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP, 0, toolbar.getHeight());
                        t.show();
                    }
                    break;
            }
        }
        return data;
    }

    /**
     * Helps onPullInData parse through the data message(s).
     */
    public List<QuizMessage> parseInData(String data) {
        Log.d(TAG, "parseInData: " + data);
        Gson gson = new Gson();
        ArrayList<QuizMessage> messages = new ArrayList<>();
        String[] chunks = data.split("\\}\\{");
        for (int i = 0; i < chunks.length; i++) {
            String chunk = chunks[i];
            if (i < chunks.length - 1)
                chunk += "}";
            if (i > 0)
                chunk = "{" + chunk;
            messages.add(gson.fromJson(chunk, QuizMessage.class));
        }
        return messages;
    }

    private boolean handshakesAreDone() {
        return scoreHandshakes.isEmpty() &&
                    (wifiDirectApp.mManageActivity == null ||
                            wifiDirectApp.mManageActivity.allConfirmationsSent());
    }
}
