package com.seniordesign.wolfpack.quizinator;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class MainMenuTests {

    // Needed to run in Travis
    // **********************************************
    @Rule
    public ActivityTestRule<MainMenuActivity> mActivityRule = new ActivityTestRule<>(MainMenuActivity.class);

    @Before
    public void unlockScreen() {
        final MainMenuActivity activity = mActivityRule.getActivity();
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
    public void titleTest() {
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText("Main Menu")));
    }

    @Test
    public void validateNewGameIntent() {
        onView(withId(R.id.newGameButton)).perform(click());
        onView(withId(R.id.game_label)).check(matches(isDisplayed()));
    }

    @Test
    public void validateDrawerBehavior() {
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.START)));
        onView(withId(R.id.drawer_layout)).perform(open());

        pressBack();

        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.START)));
    }

    @Test
    public void validateDrawerItems() {
        onView(withId(R.id.drawer_layout)).perform(open());

        String expectedTitle = InstrumentationRegistry.getTargetContext().getString(R.string.app_name);
        onView(withId(R.id.header_title)).check(matches(withText(expectedTitle)));

        String expectedItem = InstrumentationRegistry.getTargetContext().getString(R.string.quiz_bowl_rules);
        onView(withText(expectedItem)).check(matches(isDisplayed()));
        onView(withText(expectedItem)).perform(click());
    }
}
