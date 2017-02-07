package com.seniordesign.wolfpack.quizinator;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;
import com.seniordesign.wolfpack.quizinator.Activities.NewGameSettingsActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.seniordesign.wolfpack.quizinator.Constants.*;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GameSettingTests {

    // Needed to run in Travis
    // **********************************************
    @Rule
    public ActivityTestRule<NewGameSettingsActivity> mActivityRule = new ActivityTestRule<>(NewGameSettingsActivity.class);

    @Before
    public void unlockScreen() {
        final NewGameSettingsActivity activity = mActivityRule.getActivity();
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
    public void validateSpinnerTrueFalse() {
        onData(allOf(is(instanceOf(String.class)), is(ALL_CARD_TYPES))).inAdapterView(withId(R.id.card_type_spinner)).perform(click());
        onData(allOf(is(instanceOf(CARD_TYPES.class)), is(CARD_TYPES.MULTIPLE_CHOICE))).perform(click());
        onView(withText(CARD_TYPES.TRUE_FALSE.toString())).perform(pressBack());
        onView(withId(R.id.card_type_spinner)).check(matches(withSpinnerText(containsString(CARD_TYPES.TRUE_FALSE.toString()))));
    }

    @Test
    public void validateSpinnerMultiChoice() {
        onData(allOf(is(instanceOf(String.class)), is(ALL_CARD_TYPES))).inAdapterView(withId(R.id.card_type_spinner)).perform(click());
        onData(allOf(is(instanceOf(CARD_TYPES.class)), is(CARD_TYPES.TRUE_FALSE))).perform(click());
        onView(withText(CARD_TYPES.MULTIPLE_CHOICE.toString())).perform(pressBack());
        onView(withId(R.id.card_type_spinner)).check(matches(withSpinnerText(containsString(CARD_TYPES.MULTIPLE_CHOICE.toString()))));
    }

    @Test
    public void validateSpinnerAtLeastOneSelected() {
        onData(allOf(is(instanceOf(String.class)), is(ALL_CARD_TYPES))).inAdapterView(withId(R.id.card_type_spinner)).perform(click());
        onData(allOf(is(instanceOf(CARD_TYPES.class)), is(CARD_TYPES.TRUE_FALSE))).perform(click());
        onData(allOf(is(instanceOf(CARD_TYPES.class)), is(CARD_TYPES.MULTIPLE_CHOICE))).perform(click());
        onView(withText(CARD_TYPES.MULTIPLE_CHOICE.toString())).perform(pressBack());
        onView(withId(R.id.card_type_spinner)).check(matches(withSpinnerText(containsString(CARD_TYPES.MULTIPLE_CHOICE.toString()))));
    }

    @Test
    public void validateDeckSpinner() {
        onView(withId(R.id.deck_spinner)).check(matches(withSpinnerText(containsString("Default"))));
    }

    @Test
    public void validateGameMinuteInput_LowerBound() {
        onView(withId(R.id.game_minutes)).check(matches(withText("01")));
        onView(withId(R.id.game_minutes)).perform(clearText(), typeText("-1"));
        onView(withId(R.id.game_minutes)).check(matches(withText("1")));
    }

    @Test
    public void validateGameMinuteInput_UpperBound() {
        onView(withId(R.id.game_minutes)).check(matches(withText("01")));
        onView(withId(R.id.game_minutes)).perform(clearText(), typeText("60"));
        onView(withId(R.id.game_minutes)).check(matches(withText("60")));

        onView(withId(R.id.game_minutes)).perform(clearText(), typeText("61"));
        onView(withId(R.id.game_minutes)).check(matches(withText("61")));
    }

    @Test
    public void validateGameSecondInput_LowerBound() throws InterruptedException {
        onView(withId(R.id.game_seconds)).check(matches(withText("00")));
        onView(withId(R.id.game_seconds)).perform(clearText(), typeText("-1"));
        onView(withId(R.id.game_seconds)).check(matches(withText("1")));
    }

    @Test
    public void validateGameSecondInput_UpperBound() {
        onView(withId(R.id.game_seconds)).check(matches(withText("00")));
        onView(withId(R.id.game_seconds)).perform(clearText(), typeText("60"));
        onView(withId(R.id.game_seconds)).check(matches(withText("60")));

        onView(withId(R.id.game_seconds)).perform(clearText(), typeText("61"));
        onView(withId(R.id.game_seconds)).check(matches(withText("61")));
    }

    @Test
    public void validateCardMinuteInput_LowerBound() {
        onView(withId(R.id.card_minutes)).check(matches(withText("00")));
        onView(withId(R.id.card_minutes)).perform(clearText(), typeText("-1"));
        onView(withId(R.id.card_minutes)).check(matches(withText("1")));
    }

    @Test
    public void validateCardMinuteInput_UpperBound() {
        onView(withId(R.id.card_minutes)).check(matches(withText("00")));
        onView(withId(R.id.card_minutes)).perform(clearText(), typeText("60"));
        onView(withId(R.id.card_minutes)).check(matches(withText("60")));

        onView(withId(R.id.card_minutes)).perform(clearText(), typeText("61"));
        onView(withId(R.id.card_minutes)).check(matches(withText("61")));
    }

    @Test
    public void validateCardSecondInput_LowerBound() throws InterruptedException {
        onView(withId(R.id.card_seconds)).check(matches(withText("10")));
        onView(withId(R.id.card_seconds)).perform(clearText(), typeText("1"));
        onView(withId(R.id.card_seconds)).check(matches(withText("1")));

        onView(withId(R.id.card_seconds)).perform(clearText(), typeText("00"));
        onView(withId(R.id.card_seconds)).check(matches(withText("00")));

        onView(withId(R.id.card_seconds)).perform(clearText(), typeText("-1"));
        onView(withId(R.id.card_seconds)).check(matches(withText("1")));
    }

    @Test
    public void validateCardSecondInput_UpperBound() {
        onView(withId(R.id.card_seconds)).check(matches(withText("10")));
        onView(withId(R.id.card_seconds)).perform(clearText(), typeText("60"));
        onView(withId(R.id.card_seconds)).check(matches(withText("60")));

        onView(withId(R.id.card_seconds)).perform(clearText(), typeText("61"));
        onView(withId(R.id.card_seconds)).check(matches(withText("61")));
    }

    @Test
    public void validateCardCountInput() {
        onView(withId(R.id.card_count)).check(matches(withText("10")));
        onView(withId(R.id.card_count)).perform(clearText(), typeText("1"));
        onView(withId(R.id.card_count)).check(matches(withText("1")));

        onView(withId(R.id.card_count)).perform(clearText(), typeText("0"));
        onView(withId(R.id.card_count)).check(matches(withText("0")));

        onView(withId(R.id.card_count)).perform(clearText(), typeText("-1"));
        onView(withId(R.id.card_count)).check(matches(withText("1")));

        onView(withId(R.id.card_count)).perform(clearText(), typeText("20"));
        onView(withId(R.id.card_count)).check(matches(withText("20")));

        onView(withId(R.id.card_count)).perform(clearText(), typeText("10"));
        onView(withId(R.id.card_count)).check(matches(withText("10")));
    }

    @Test
    public void validateElementsAreDisplayed() {
        onView(withId(R.id.game_label)).check(matches(isDisplayed()));
        onView(withId(R.id.game_minutes)).check(matches(isDisplayed()));
        onView(withId(R.id.game_semicolon)).check(matches(isDisplayed()));
        onView(withId(R.id.game_seconds)).check(matches(isDisplayed()));

        onView(withId(R.id.card_label)).check(matches(isDisplayed()));
        onView(withId(R.id.card_minutes)).check(matches(isDisplayed()));
        onView(withId(R.id.card_semicolon)).check(matches(isDisplayed()));
        onView(withId(R.id.card_seconds)).check(matches(isDisplayed()));

        onView(withId(R.id.card_count_label)).check(matches(isDisplayed()));
        onView(withId(R.id.card_count)).check(matches(isDisplayed()));

        onView(withId(R.id.card_type_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.card_type)).check(matches(isDisplayed()));

        onView(withId(R.id.new_game)).check(matches(isDisplayed()));
    }

    @Test
    public void validateEditTexts() {
        onView(withId(R.id.game_minutes)).check(matches(withText(containsString("01"))));
        onView(withId(R.id.game_seconds)).check(matches(withText(containsString("00"))));
        onView(withId(R.id.card_minutes)).check(matches(withText(containsString("00"))));
        onView(withId(R.id.card_seconds)).check(matches(withText(containsString("10"))));
        onView(withId(R.id.card_count)).check(matches(withText(containsString("0"))));
    }
}
