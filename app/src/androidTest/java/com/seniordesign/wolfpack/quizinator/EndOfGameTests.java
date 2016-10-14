package com.seniordesign.wolfpack.quizinator;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.view.WindowManager;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Activities.EndOfGameplayActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

/*
 * @author leonardj (10/10/2016)
 */

public class EndOfGameTests {

    // Needed to run in Travis
    // **********************************************
    @Rule
    public ActivityTestRule<EndOfGameplayActivity> mActivityRule = new ActivityTestRule<>(EndOfGameplayActivity.class);

    @Before
    public void unlockScreen() {
        final EndOfGameplayActivity activity = mActivityRule.getActivity();
        Runnable wakeUpDevice = new Runnable() {
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        activity.runOnUiThread(wakeUpDevice);
    }
    // **********************************************

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.seniordesign.wolfpack.quizinator", appContext.getPackageName());
    }

    @Test
    public void validateMainMenuIntent() {
        onView(withId(R.id.newGameButton)).perform(click());
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText("Main Menu")));
    }

    @Test
    public void validateElementsAreDisplayed() {
        onView(withId(R.id.endOfGameText)).check(matches(isDisplayed()));
        onView(withId(R.id.endOfGameScoreText)).check(matches(isDisplayed()));
        onView(withId(R.id.endOfGameTimeText)).check(matches(isDisplayed()));
        onView(withId(R.id.endOfGameTotalCardsText)).check(matches(isDisplayed()));
        onView(withId(R.id.endOfGameHighScoreText)).check(matches(isDisplayed()));
        onView(withId(R.id.newGameButton)).check(matches(isDisplayed()));
    }
}
