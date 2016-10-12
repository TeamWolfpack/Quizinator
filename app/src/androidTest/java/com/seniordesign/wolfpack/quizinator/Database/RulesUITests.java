package com.seniordesign.wolfpack.quizinator.Database;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesSQLiteHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * @creation 10/11/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RulesUITests {

    private RulesDataSource dao;
    private RulesSQLiteHelper sql;

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
        dao = new RulesDataSource(activity);
        sql = new RulesSQLiteHelper(activity);
    }
    // **********************************************

    /*
     * @author kuczynskij (10/12/2016)
     */
    @Test
    public void normalFlow_RulesDataSource() throws Exception{
        assertEquals(true, dao.open());
        assertEquals(true, dao.getDatabase().isOpen());

        dao.createRule(4, 350000, 350000, "true/false");

        //HighScores hs = dao.createHighScore("Sample", 350000, 650);
//
//        System.out.println(dao.getSQLiteHelper().getDatabaseName());
//        assertEquals("Sample", dao.getSQLiteHelper().getDatabaseName());


        assertEquals(true, dao.close());
    }
}
