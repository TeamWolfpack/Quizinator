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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RulesUITests {

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
    public void normalFlow_RulesDataSource() throws Exception{
        assertEquals(true, dao.open());
        assertEquals(true, dao.getDatabase().isOpen());
        Rules r = dao.createRule(4, 350000, 350000, "['TF', 'MC', 'FR', 'VR']", 1, "NormalFlow", null, null, null);
        assertEquals(4, dao.getAllRules().size());
        assertEquals(10, dao.getRulesAllColumns().length);
        assertTrue(dao.deleteRule(r));
        assertTrue(dao.close());
    }

    /*
     * @author kuczynskij (10/12/2016)
     */
    @Test
    public void normalFlow_UpdateRule() throws Exception{
        assertEquals(true, dao.open());
        assertEquals(true, dao.getDatabase().isOpen());
        Rules r = dao.createRule(4, 350000, 350000, "['TF', 'MC', 'FR', 'VR']", 1, "NormalFlow", null, null, null);
        assertEquals(4, dao.getAllRules().size());
        assertEquals(10, dao.getRulesAllColumns().length);

        r.setMaxCardCount(5);
        r.setTimeLimit(90000);
        r.setCardDisplayTime(10000);
        dao.updateRules(r);
        assertEquals(4, dao.getAllRules().size());

        assertTrue(dao.deleteRule(r));
        assertTrue(dao.close());
    }
}
