package com.seniordesign.wolfpack.quizinator.Database;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Card.MCCard;
import com.seniordesign.wolfpack.quizinator.Database.Card.TFCard;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckSQLiteHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests the Deck database files that need a Context object
 * @creation 10/16/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeckUITests {

    private DeckDataSource datasource;
    private DeckSQLiteHelper dbHelper;

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
        datasource = new DeckDataSource(activity);
        dbHelper = new DeckSQLiteHelper(activity);
    }

    /*
     * @author  chuna (10/16/2016)
     */
    @Test
    public void normalFlow_DeckDataSource() throws Exception{
        assertEquals(true, datasource.open());
        assertEquals(true, datasource.getDatabase().isOpen());
        dbHelper.onUpgrade(datasource.getDatabase(), 0, 1);
        List<Card> cards = new ArrayList<>();
        cards.add(new TFCard());
        cards.add(new MCCard());
        Deck deck = datasource.createDeck("TestDeck", cards);
        assertEquals("deck.db", datasource.getSQLiteHelper().getDatabaseName());
        assertEquals(1, datasource.getAllDecks().size());
        assertEquals(3, datasource.getAllColumns().length);
        deck.setDeckName("TestDeck2");
        assertEquals(1, datasource.updateDeck(deck));
        assertEquals(1, datasource.deleteDeck(deck));
        assertEquals(true, datasource.close());
    }
}
