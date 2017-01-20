package com.seniordesign.wolfpack.quizinator.Database;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Tests the Card database files that need a Context object
 * @creation 10/16/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CardUITests {

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

    /*
     * @author  chuna (10/16/2016)
     */
    @Test
    public void normalFlow_CardDataSource() throws Exception{
        assertEquals(true, dataSource.open());
        assertEquals(true, dataSource.getDatabase().isOpen());
        dbHelper.onUpgrade(dataSource.getDatabase(), 0, 1);

        Card card = dataSource.createCard("TF", "Test TF Question", "True",
                new String[]{"True", "False"}, 1, "False");
        assertEquals(11, dataSource.getAllCards().size());
        assertEquals(7, dataSource.getCardAllColumns().length);
        card.setPoints(3);
        assertEquals(1, dataSource.updateCard(card));
        assertEquals(1, dataSource.deleteCard(card));
        assertEquals(true, dataSource.close());
    }
}
