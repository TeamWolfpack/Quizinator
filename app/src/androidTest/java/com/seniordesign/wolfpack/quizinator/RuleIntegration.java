package com.seniordesign.wolfpack.quizinator;

import android.support.test.rule.ActivityTestRule;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.activities.NewGameSettingsActivity;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.database.HighScores;
import com.seniordesign.wolfpack.quizinator.database.Rules;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public class RuleIntegration {

    private Gson gson = new Gson();

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
    public void validateUpdatingRuleOnGameStart() {
        QuizDataSource dataSource = new QuizDataSource(mActivityRule.getActivity());
        dataSource.open();
        Rules oldRule = dataSource.getAllRules().get(1);
        oldRule.setMaxCardCount(5);
        oldRule.setTimeLimit(90000);
        oldRule.setCardDisplayTime(9000);
        oldRule.setCardTypes(getCardTypeString());
        dataSource.updateRules(oldRule);
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().loadPreviousRules("Default");
            }
        });

        onView(withId(R.id.card_count)).perform(clearText(), typeText("1"));
        onView(withId(R.id.card_minutes)).perform(clearText(), typeText("0"));
        onView(withId(R.id.card_seconds)).perform(clearText(), typeText("30"));
        onView(withId(R.id.game_minutes)).perform(clearText(), typeText("2"));
        onView(withId(R.id.game_seconds)).perform(clearText(), typeText("30"));

        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().updateRuleSet();
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Rules rule = dataSource.getAllRules().get(1);
        assertTrue("Game time is " + rule.getTimeLimit(), rule.getTimeLimit() == 150000);
        assertTrue(rule.getCardDisplayTime() == 30000);
        assertTrue(rule.getMaxCardCount() == 1);
        assertTrue(rule.getCardTypes(), rule.getCardTypes().equals(getCardTypeString()));

        dataSource.close();
    }

    @Test
    public void validateLoadingFromDatabase() {
        QuizDataSource dataSource = new QuizDataSource(mActivityRule.getActivity());
        dataSource.open();

        Rules rule = dataSource.getAllRules().get(1);
        rule.setMaxCardCount(5);
        rule.setTimeLimit(63000);
        rule.setCardDisplayTime(9000);
        rule.setCardTypes(getCardTypeString());
        dataSource.updateRules(rule);
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().loadPreviousRules("Default");
            }
        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.game_minutes)).check(matches(withText(containsString("1"))));
        onView(withId(R.id.game_seconds)).check(matches(withText(containsString("03"))));
        onView(withId(R.id.card_minutes)).check(matches(withText(containsString("0"))));
        onView(withId(R.id.card_seconds)).check(matches(withText(containsString("09"))));
        onView(withId(R.id.card_count)).check(matches(withText(containsString("5"))));

        rule = dataSource.getAllRules().get(1);
        rule.setMaxCardCount(5);
        rule.setTimeLimit(90000);
        rule.setCardDisplayTime(10000);
        rule.setCardTypes(getCardTypeString());
        dataSource.updateRules(rule);
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().loadPreviousRules("Default");
            }
        });

        onView(withId(R.id.game_minutes)).check(matches(withText(containsString("1"))));
        onView(withId(R.id.game_seconds)).check(matches(withText(containsString("30"))));
        onView(withId(R.id.card_minutes)).check(matches(withText(containsString("0"))));
        onView(withId(R.id.card_seconds)).check(matches(withText(containsString("10"))));
        onView(withId(R.id.card_count)).check(matches(withText(containsString("5"))));
        dataSource.close();
    }

    @Test
    public void validateCardLimit() {
        QuizDataSource dataSource = new QuizDataSource(mActivityRule.getActivity());
        dataSource.open();

        Rules rule = dataSource.getAllRules().get(0);
        rule.setMaxCardCount(1);
        rule.setTimeLimit(60000);
        rule.setCardDisplayTime(5000);
        dataSource.updateRules(rule);
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().loadPreviousRules("Default");
            }
        });
        dataSource.close();

        onView(withId(R.id.new_game)).perform(click());
        //Wait for game to end
        onView(withId(R.id.endOfGameScoreText)).check(matches(isDisplayed()));
    }

    @Test
    public void validateRuleSwitching() {
        onView(withId(R.id.ruleset_spinner)).perform(click());
        onView(withText("Double Down")).perform(click());

        onView(withId(R.id.allow_multiple_winners)).check(matches(isChecked()));
        onView(withId(R.id.include_double_edge_questions)).check(matches(isChecked()));
        onView(withId(R.id.include_final_wager_question)).check(matches(isChecked()));
        onView(withId(R.id.allow_multiple_winners)).check(matches(isDisplayed()));
        onView(withId(R.id.include_double_edge_questions)).check(matches(isDisplayed()));
        onView(withId(R.id.include_final_wager_question)).check(matches(isDisplayed()));
    }

    @Test
    public void validateRuleSwitching_EndWithDefault() {
        onView(withId(R.id.ruleset_spinner)).perform(click());
        onView(withText("Double Down")).perform(click());

        onView(withId(R.id.allow_multiple_winners)).check(matches(isChecked()));
        onView(withId(R.id.include_double_edge_questions)).check(matches(isChecked()));
        onView(withId(R.id.include_final_wager_question)).check(matches(isChecked()));
        onView(withId(R.id.allow_multiple_winners)).check(matches(isDisplayed()));
        onView(withId(R.id.include_double_edge_questions)).check(matches(isDisplayed()));
        onView(withId(R.id.include_final_wager_question)).check(matches(isDisplayed()));

        onView(withId(R.id.ruleset_spinner)).perform(click());
        onView(withText("Default")).perform(click());

        onView(withId(R.id.allow_multiple_winners)).check(matches(not(isDisplayed())));
        onView(withId(R.id.include_double_edge_questions)).check(matches(not(isDisplayed())));
        onView(withId(R.id.include_final_wager_question)).check(matches(not(isDisplayed())));
    }

    private String getCardTypeString() {
        return "[\"TRUE_FALSE\",\"MULTIPLE_CHOICE\"]";
    }
}
