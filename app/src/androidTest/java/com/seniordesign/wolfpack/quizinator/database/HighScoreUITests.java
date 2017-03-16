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

import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HighScoreUITests {

    private QuizDataSource dao;
    private QuizSQLiteHelper sql;

    // Needed to run in Travis
    // **********************************************
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
        dao = new QuizDataSource(activity);
        sql = new QuizSQLiteHelper(activity);
    }
    // **********************************************

    @Test
    public void normalFlow_HighScoresDataSource() throws Exception{
        assertEquals(true, dao.open());
        assertEquals(true, dao.getDatabase().isOpen());
        sql.onUpgrade(dao.getDatabase(), 0, 1);
        HighScores hs = dao.createHighScore("Sample", 350000, 650);
        assertEquals(1, dao.getAllHighScores().size());
        assertEquals(4, dao.getHighScoresAllColumns().length);
        assertEquals(true, dao.deleteHighScore(hs));
        assertEquals(true, dao.close());
    }
}
