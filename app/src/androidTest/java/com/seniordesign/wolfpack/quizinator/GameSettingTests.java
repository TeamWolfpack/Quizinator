package com.seniordesign.wolfpack.quizinator;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.seniordesign.wolfpack.quizinator.Activities.GamePlayActivity;
import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Activities.NewGameSettingsActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
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

    @Test
    public void validateSpinnerOptions() {
        String[] cardTypes = new String[] {"True/False", "Multiple Choice", "Both"};
        for (String type: cardTypes) {
            onView(withId(R.id.card_type_spinner)).perform();
            onData(allOf(is(instanceOf(String.class)), is(type))).perform();
            onView(withId(R.id.card_type_spinner)).check(matches(withSpinnerText(containsString(type))));
        }
    }

    @Test
    public void validateMinuteInput_LowerBound() {
        onView(withId(R.id.game_minutes)).check(matches(withText("01")));
        onView(withId(R.id.game_minutes)).perform(clearText(), typeText("-1"));
        onView(withId(R.id.game_minutes)).check(matches(withText("1")));
    }

    @Test
    public void validateMinuteInput_UpperBound() {
        onView(withId(R.id.game_minutes)).check(matches(withText("01")));
        onView(withId(R.id.game_minutes)).perform(clearText(), typeText("60"));
        onView(withId(R.id.game_minutes)).check(matches(withText("60")));

        onView(withId(R.id.game_minutes)).check(matches(withText("60")));
        onView(withId(R.id.game_minutes)).perform(clearText(), typeText("61"));
        onView(withId(R.id.game_minutes)).check(matches(withText("6")));
    }

    @Test
    public void validateSecondInput_LowerBound() throws InterruptedException {
        onView(withId(R.id.game_seconds)).check(matches(withText("00")));
        onView(withId(R.id.game_seconds)).perform(clearText(), typeText("-1"));
        onView(withId(R.id.game_seconds)).check(matches(withText("1")));
    }

    @Test
    public void validateSecondInput_UpperBound() {
        onView(withId(R.id.game_seconds)).check(matches(withText("00")));
        onView(withId(R.id.game_seconds)).perform(clearText(), typeText("60"));
        onView(withId(R.id.game_seconds)).check(matches(withText("60")));

        onView(withId(R.id.game_seconds)).check(matches(withText("60")));
        onView(withId(R.id.game_seconds)).perform(clearText(), typeText("61"));
        onView(withId(R.id.game_seconds)).check(matches(withText("6")));
    }

    @Test
    public void validateStartGame() throws InterruptedException {
        onView(withId(R.id.new_game)).perform(click());
        //Test Intent
    }

    @Test
    public void validateElementsAreDisplayed() {
        onView(withId(R.id.game_label)).check(matches(isDisplayed()));
        onView(withId(R.id.game_minutes)).check(matches(isDisplayed()));
        onView(withId(R.id.semicolon)).check(matches(isDisplayed()));
        onView(withId(R.id.game_seconds)).check(matches(isDisplayed()));

        onView(withId(R.id.card_type_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.card_type)).check(matches(isDisplayed()));

        onView(withId(R.id.new_game)).check(matches(isDisplayed()));
    }

    @Test
    public void validateEditTexts() {
        onView(withId(R.id.game_minutes)).check(matches(withText(containsString("01"))));
        onView(withId(R.id.game_seconds)).check(matches(withText(containsString("00"))));
    }
}
