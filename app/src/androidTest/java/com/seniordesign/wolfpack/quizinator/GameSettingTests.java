package com.seniordesign.wolfpack.quizinator;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Activities.NewGameSettingsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/*
 * @author leonardj (10/2/2016)
 */

@RunWith(AndroidJUnit4.class)
public class GameSettingTests {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(NewGameSettingsActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.seniordesign.wolfpack.quizinator", appContext.getPackageName());
    }

    @Test
    public void validateEditTexts() {
        //Test checks the toolbar title
        //onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar_main_menu))))
        //        .check(matches(withText("Quizinator")));
    }

    @Test
    public void validateSpinner() {
        onView(withId(R.id.card_type_spinner)).perform(scrollTo(), click());
        onData(allOf(is(instanceOf(String.class)), is("True/False"))).perform(click());
        onView(withId(R.id.card_type_spinner)).check(matches(withSpinnerText(containsString("True/False"))));
    }
}
