package com.seniordesign.wolfpack.quizinator.database;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Constants;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

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

    @Test
    public void normalFlow_CardDataSource() throws Exception{
        assertEquals(true, dataSource.open());
        assertEquals(true, dataSource.getDatabase().isOpen());
        Card card = dataSource.createCard(Constants.CARD_TYPES.TRUE_FALSE.ordinal(), "Test TF Question", String.valueOf(true),
                new String[]{String.valueOf(true), String.valueOf(false)}, 1, String.valueOf(false));
        assertEquals(26, dataSource.getAllCards().size());
        assertEquals(7, dataSource.getCardAllColumns().length);
        card.setPoints(3);
        assertEquals(1, dataSource.updateCard(card));
        assertEquals(1, dataSource.deleteCard(card));
        assertEquals(true, dataSource.close());
    }
}
