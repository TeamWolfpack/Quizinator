package com.seniordesign.wolfpack.quizinator.Database;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsUITests {

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

    /*
     * @author kuczynskij (10/12/2016)
     */
    @Test
    public void normalFlow_SettingsDataSource() throws Exception{
        assertEquals(true, dao.open());
        assertEquals(true, dao.getDatabase().isOpen());
        sql.onUpgrade(dao.getDatabase(), 0, 1);
        Settings s = dao.createSettings(3, "Jim");
        assertEquals(1, dao.getAllSettings().size());
        assertEquals(3, dao.getSettingsAllColumns().length);
        assertEquals(true, dao.deleteSetting(s));
        assertEquals(true, dao.close());
    }
}
