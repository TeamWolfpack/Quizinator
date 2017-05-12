package com.seniordesign.wolfpack.quizinator.database;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.activities.MainMenuActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeckUITests {

    private QuizDataSource dataSource;
    private QuizSQLiteHelper dbHelper;

    @Rule
    public ActivityTestRule<MainMenuActivity> mActivityRule =
            new ActivityTestRule<>(MainMenuActivity.class);

    @Before
    public void unlockScreen() {
        final MainMenuActivity activity = mActivityRule.getActivity();
        Runnable wakeUpDevice = new Runnable() {
            public void run() {
                activity.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        activity.runOnUiThread(wakeUpDevice);
        dataSource = new QuizDataSource(activity);
        dbHelper = new QuizSQLiteHelper(activity);
    }

    @Test
    public void normalFlow_DeckDataSource() throws Exception{
        assertEquals(true, dataSource.open());
        assertEquals(true, dataSource.getDatabase().isOpen());
        List<Card> cards = new ArrayList<>();
        cards.add(new Card());
        cards.add(new Card());
        Deck deck = dataSource.createDeck("TestDeck", null, null, true, null, cards);
        assertTrue(dataSource.getAllDecks().size() > 1);
        assertEquals(6, dataSource.getDeckAllColumns().length);
        deck.setDeckName("TestDeck2");
        assertEquals(1, dataSource.updateDeck(deck));
        assertEquals(1, dataSource.deleteDeck(deck));
        assertEquals(true, dataSource.close());
    }
}
