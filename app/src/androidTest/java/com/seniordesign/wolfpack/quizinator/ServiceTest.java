package com.seniordesign.wolfpack.quizinator;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.WifiDirect.Answer;
import com.seniordesign.wolfpack.quizinator.WifiDirect.Confirmation;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionManager;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.QuizMessage;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.*;
import static org.junit.Assert.*;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ServiceTest {

    Gson gson = new Gson();
    ConnectionService service;

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    @Before
    public void SetupService() throws TimeoutException {
        Intent serviceIntent = new Intent(
                InstrumentationRegistry.getTargetContext(), ConnectionService.class);

        mServiceRule.bindService(serviceIntent);

        if (service == null)
            service = ConnectionService.getInstance();
    }

    @Test
    public void testSingleMessage() {
        String answer = gson.toJson(new Answer("Test Name", "Test Address", "0", System.nanoTime()));
        String message1 = gson.toJson(new QuizMessage(
                MSG_SEND_ANSWER_ACTIVITY, answer));

        List<QuizMessage> messages = service.parseInData(message1);

        assertTrue("Only one message parsed", messages.size() == 1);

        assertEquals("Player answers with 0", answer, messages.get(0).getMessage());
    }

    @Test
    public void testTwoMessages() {
        String answer = gson.toJson(new Answer("Test Name", "Test Address", "0", System.nanoTime()));
        String confirmation = gson.toJson(new Confirmation("Test Address", true));

        String message1 = gson.toJson(new QuizMessage(
                MSG_SEND_ANSWER_ACTIVITY, answer));
        String message2 = gson.toJson(new QuizMessage(
                MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation));

        List<QuizMessage> messages = service.parseInData(message1 + message2);

        assertTrue("Both messages are parsed", messages.size() == 2);

        assertEquals("Player answers with 0", answer, messages.get(0).getMessage());
        assertEquals("Confirmation is true", confirmation, messages.get(1).getMessage());
    }

    @Test
    public void testMultipleMessages() {
        String answer = gson.toJson(new Answer("Test Name", "Test Address", "0", System.nanoTime()));
        String confirmation = gson.toJson(new Confirmation("Test Address", true));
        String endGame = gson.toJson(0);

        String message1 = gson.toJson(new QuizMessage(
                MSG_SEND_ANSWER_ACTIVITY, answer));
        String message2 = gson.toJson(new QuizMessage(
                MSG_ANSWER_CONFIRMATION_ACTIVITY, confirmation));
        String message3 = gson.toJson(new QuizMessage(
                MSG_END_OF_GAME_ACTIVITY, endGame));

        List<QuizMessage> messages = service.parseInData(message1 + message2 + message3);

        assertTrue("Both messages are parsed", messages.size() == 3);

        assertEquals("Player answers with 0", answer, messages.get(0).getMessage());
        assertEquals("Confirmation is true", confirmation, messages.get(1).getMessage());
        assertEquals("End Game message", endGame, messages.get(2).getMessage());
    }

    @Test
    public void testMessageToSingleClient() throws IOException {
        ConnectionManager manager = new ConnectionManager(service);

        Confirmation confirmation = new Confirmation("0.0.0.0", true);
        String message = gson.toJson(confirmation);

        boolean result = manager.publishDataToSingleClient(
                gson.toJson(new QuizMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, message)),
                confirmation.getClientAddress());

        assertFalse("Phone is not server", result);

        WifiDirectApp wifiDirectApp = (WifiDirectApp)service.getApplication();
        wifiDirectApp.mIsServer = true;

        result = manager.publishDataToSingleClient(
                gson.toJson(new QuizMessage(MSG_ANSWER_CONFIRMATION_ACTIVITY, message)),
                confirmation.getClientAddress());

        assertFalse("Phone is not connected to any client", result);
    }
}
