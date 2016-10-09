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
import android.view.WindowManager;
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
import static android.support.test.espresso.action.ViewActions.scrollTo;
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
        onView(withId(R.id.game_minutes)).check(matches(withText("6")));
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
        onView(withId(R.id.game_seconds)).check(matches(withText("6")));
    }

    @Test
    public void validateCardMinuteInput_LowerBound() {
        onView(withId(R.id.card_minutes)).check(matches(withText("00")));
        onView(withId(R.id.card_minutes)).perform(scrollTo(), clearText(), typeText("-1"));
        onView(withId(R.id.card_minutes)).check(matches(withText("1")));
    }

    @Test
    public void validateCardMinuteInput_UpperBound() {
        onView(withId(R.id.card_minutes)).check(matches(withText("00")));
        onView(withId(R.id.card_minutes)).perform(scrollTo(), clearText(), typeText("60"));
        onView(withId(R.id.card_minutes)).check(matches(withText("60")));

        onView(withId(R.id.card_minutes)).perform(scrollTo(), clearText(), typeText("61"));
        onView(withId(R.id.card_minutes)).check(matches(withText("6")));
    }

    @Test
    public void validateCardSecondInput_LowerBound() throws InterruptedException {
        onView(withId(R.id.card_seconds)).check(matches(withText("03")));
        onView(withId(R.id.card_seconds)).perform(scrollTo(), clearText(), typeText("1"));
        onView(withId(R.id.card_seconds)).check(matches(withText("1")));

        onView(withId(R.id.card_seconds)).perform(scrollTo(), clearText(), typeText("00"));
        onView(withId(R.id.card_seconds)).check(matches(withText("")));

        onView(withId(R.id.card_seconds)).perform(scrollTo(), clearText(), typeText("-1"));
        onView(withId(R.id.card_seconds)).check(matches(withText("1")));
    }

    @Test
    public void validateCardSecondInput_UpperBound() {
        onView(withId(R.id.card_seconds)).check(matches(withText("03")));
        onView(withId(R.id.card_seconds)).perform(scrollTo(), clearText(), typeText("60"));
        onView(withId(R.id.card_seconds)).check(matches(withText("60")));

        onView(withId(R.id.card_seconds)).perform(scrollTo(), clearText(), typeText("61"));
        onView(withId(R.id.card_seconds)).check(matches(withText("6")));
    }

    @Test
    public void validateCardCountInput() {
        onView(withId(R.id.card_count)).check(matches(withText("10")));
        onView(withId(R.id.card_count)).perform(scrollTo(), clearText(), typeText("1"));
        onView(withId(R.id.card_count)).check(matches(withText("1")));

        onView(withId(R.id.card_count)).perform(scrollTo(), clearText(), typeText("00"));
        onView(withId(R.id.card_count)).check(matches(withText("")));

        onView(withId(R.id.card_count)).perform(scrollTo(), clearText(), typeText("-1"));
        onView(withId(R.id.card_count)).check(matches(withText("1")));

        onView(withId(R.id.card_count)).perform(scrollTo(), clearText(), typeText("20"));
        onView(withId(R.id.card_count)).check(matches(withText("2")));

        onView(withId(R.id.card_count)).perform(scrollTo(), clearText(), typeText("10"));
        onView(withId(R.id.card_count)).check(matches(withText("10")));
    }

    @Test
    public void validateElementsAreDisplayed() {
        onView(withId(R.id.game_label)).check(matches(isDisplayed()));
        onView(withId(R.id.game_minutes)).check(matches(isDisplayed()));
        onView(withId(R.id.game_semicolon)).check(matches(isDisplayed()));
        onView(withId(R.id.game_seconds)).check(matches(isDisplayed()));

        onView(withId(R.id.card_label)).perform(scrollTo());
        onView(withId(R.id.card_label)).check(matches(isDisplayed()));
        onView(withId(R.id.card_minutes)).check(matches(isDisplayed()));
        onView(withId(R.id.card_semicolon)).check(matches(isDisplayed()));
        onView(withId(R.id.card_seconds)).check(matches(isDisplayed()));

        onView(withId(R.id.card_count_label)).perform(scrollTo());
        onView(withId(R.id.card_count_label)).check(matches(isDisplayed()));
        onView(withId(R.id.card_count)).check(matches(isDisplayed()));

        onView(withId(R.id.card_type_spinner)).perform(scrollTo());
        onView(withId(R.id.card_type_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.card_type)).check(matches(isDisplayed()));

        onView(withId(R.id.new_game)).perform(scrollTo());
        onView(withId(R.id.new_game)).check(matches(isDisplayed()));
    }

    @Test
    public void validateEditTexts() {
        onView(withId(R.id.game_minutes)).check(matches(withText(containsString("01"))));
        onView(withId(R.id.game_seconds)).check(matches(withText(containsString("00"))));
        onView(withId(R.id.card_minutes)).check(matches(withText(containsString("00"))));
        onView(withId(R.id.card_seconds)).check(matches(withText(containsString("03"))));
        onView(withId(R.id.card_count)).check(matches(withText(containsString("10"))));
    }
}
