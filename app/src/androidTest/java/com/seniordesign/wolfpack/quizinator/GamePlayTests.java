package com.seniordesign.wolfpack.quizinator;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.Activities.GamePlayActivity;
import com.seniordesign.wolfpack.quizinator.Activities.NewGameSettingsActivity;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/*
 * Tests GamePlay Activity
 * @creation 10/17/2016.
 */
public class GamePlayTests {

    private RulesDataSource rulesource;

    // Needed to run in Travis
    // **********************************************
    @Rule
    public ActivityTestRule<NewGameSettingsActivity> mActivityRule =
            new ActivityTestRule<>(NewGameSettingsActivity.class);

    @Before
    public void unlockScreen() {
        final NewGameSettingsActivity activity = mActivityRule.getActivity();
        Runnable wakeUpDevice = new Runnable() {
            public void run() {
                activity.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        activity.runOnUiThread(wakeUpDevice);
    }
    // **********************************************

    /*
     * @author kuczynskij (10/17/2016)
     * @author farrowc (10/17/2016)
     */
    @Test
    public void normalFlow_GamePlay() throws Exception{
        setUpRules();
        //code coverage for onCreate is done inherently

    }

    private boolean setUpRules(){
        rulesource = new RulesDataSource(mActivityRule.getActivity());
        rulesource.open();
        rulesource.createRule(1, 60000, 5000, "Both");
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().loadPreviousRules();
            }
        });
        onView(withId(R.id.new_game)).perform(click());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @After
    public void cleanUp(){
        rulesource.deleteRule(rulesource.getAllRules().get(0));
    }
}
