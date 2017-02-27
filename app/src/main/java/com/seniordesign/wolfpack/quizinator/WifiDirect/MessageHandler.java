package com.seniordesign.wolfpack.quizinator.WifiDirect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Database.Card;
import com.seniordesign.wolfpack.quizinator.Database.Rules;
import com.seniordesign.wolfpack.quizinator.Messages.Answer;
import com.seniordesign.wolfpack.quizinator.Messages.Confirmation;
import com.seniordesign.wolfpack.quizinator.Messages.QuizMessage;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_ANSWER_CONFIRMATION_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_BROKEN_CONN;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_DISCONNECT_FROM_ALL_PEERS;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_END_OF_GAME_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_FINISH_CONNECT;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_NEW_CLIENT;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_NULL;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_PLAYER_READY_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_PULLIN_DATA;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_PUSHOUT_DATA;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_SELECT_ERROR;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_SEND_ANSWER_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_SEND_CARD_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_SEND_RULES_ACTIVITY;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_STARTCLIENT;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_STARTSERVER;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_REGISTER_ACTIVITY;

/**
 * Handles the messages over the WifiDirect connection.
 */
public class MessageHandler extends Handler {

    private static final String TAG = "MsgHnd";

    private ConnectionManager mConnMan;
    private WifiDirectApp wifiDirectApp;

//    private MainMenuActivity mActivity;

    MessageHandler(Looper looper, ConnectionManager connMan) {
        super(looper);
        mConnMan = connMan;
        wifiDirectApp = WifiDirectApp.getInstance();
    }

    @Override
    public void handleMessage(Message msg) {
        Log.d(TAG, "processMessage: message - " + msg.toString());
        switch (msg.what) {
            case MSG_NULL:
                break;
//            case MSG_REGISTER_ACTIVITY:
//                onActivityRegister((MainMenuActivity) msg.obj, msg.arg1);
//                break;
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
            case MSG_END_OF_GAME_ACTIVITY:
                mConnMan.pushOutData(createQuizMessage(MSG_END_OF_GAME_ACTIVITY, (String) msg.obj));
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
            default:
                break;
        }
    }

//    /**
//     * Register the activity that uses this service.
//     */
//    private void onActivityRegister(MainMenuActivity activity,
//                                    int register) {
//        Log.d(TAG, "onActivityRegister: activity register " +
//                "itself to service : " + register);
//        if (register == 1)
//            mActivity = activity;
//        else
//            mActivity = null;
//    }

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
     * @param b
     * @return
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
                case MSG_END_OF_GAME_ACTIVITY:
                    wifiDirectApp.mGameplayActivity.endGamePlay(
                            Long.parseLong(message));
                    break;
                case MSG_DISCONNECT_FROM_ALL_PEERS:
                    if(wifiDirectApp.mHomeActivity != null){
                        wifiDirectApp.mHomeActivity.disconnect();
                        wifiDirectApp.mHomeActivity.finish();
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
}
