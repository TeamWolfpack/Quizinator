package com.seniordesign.wolfpack.quizinator;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Activities.NewGameSettingsActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/*
 * @author leonardj (10/2/2016)
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GameSettingTests {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(NewGameSettingsActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.seniordesign.wolfpack.quizinator", appContext.getPackageName());
    }

    public void validateElementsAreDisplayed() {
        onView(withId(R.id.game_label)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.game_minutes)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.semicolon)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.game_seconds)).check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.card_type_spinner)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.card_type)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void validateEditTexts() {
        onView(withId(R.id.game_minutes)).check(matches(withText(containsString("00"))));
        onView(withId(R.id.game_seconds)).check(matches(withText(containsString("00"))));
    }

    @Test
    public void validateSpinner() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.card_type_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("True/False"))).perform(click());
        onView(withId(R.id.card_type_spinner)).check(matches(withSpinnerText(containsString("True/False"))));
    }
}
