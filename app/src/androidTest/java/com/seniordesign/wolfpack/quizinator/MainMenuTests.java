package com.seniordesign.wolfpack.quizinator;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Activities.NewGameSettingsActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

/*
 * @author leonardj (10/2/2016)
 */

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
    public void TitleTest() {
        //Test checks the toolbar title
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText("Main Menu")));
    }
}
