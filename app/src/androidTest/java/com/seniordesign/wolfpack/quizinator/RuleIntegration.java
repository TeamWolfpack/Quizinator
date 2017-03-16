package com.seniordesign.wolfpack.quizinator;

import android.support.test.rule.ActivityTestRule;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.activities.NewGameSettingsActivity;
import com.seniordesign.wolfpack.quizinator.database.Card;
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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.containsString;

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
        dataSource.createRule(5, 90000, 9000, getCardTypeString(), 1);
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().loadPreviousRules();
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

        Rules rule = dataSource.getAllRules().get(dataSource.getAllRules().size() - 1);
        assertTrue("Game time is " + rule.getTimeLimit(), rule.getTimeLimit() == 150000);
        assertTrue(rule.getCardDisplayTime() == 30000);
        assertTrue(rule.getMaxCardCount() == 1);
        assertTrue(rule.getCardTypes().equals(getCardTypeString()));

        dataSource.close();
    }

    @Test
    public void validateLoadingFromDatabase() {
        QuizDataSource dataSource = new QuizDataSource(mActivityRule.getActivity());
        dataSource.open();

        dataSource.createDeck("Test", "", "", true, "", new ArrayList<Card>());
        dataSource.createRule(5, 603000, 9000, getCardTypeString(), 1);
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().loadPreviousRules();
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
        onView(withId(R.id.card_type_spinner)).check(matches(withSpinnerText(containsString(Constants.CARD_TYPES.TRUE_FALSE.toString()))));
        onView(withId(R.id.card_type_spinner)).check(matches(withSpinnerText(containsString(Constants.CARD_TYPES.MULTIPLE_CHOICE.toString()))));

        dataSource.createRule(5, 90000, 10000, getCardTypeString(), 1);
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().loadPreviousRules();
            }
        });

        onView(withId(R.id.game_minutes)).check(matches(withText(containsString("1"))));
        onView(withId(R.id.game_seconds)).check(matches(withText(containsString("30"))));
        onView(withId(R.id.card_minutes)).check(matches(withText(containsString("0"))));
        onView(withId(R.id.card_seconds)).check(matches(withText(containsString("10"))));
        onView(withId(R.id.card_count)).check(matches(withText(containsString("5"))));
        onView(withId(R.id.card_type_spinner)).check(matches(withSpinnerText(containsString(Constants.CARD_TYPES.TRUE_FALSE.toString()))));
        onView(withId(R.id.card_type_spinner)).check(matches(withSpinnerText(containsString(Constants.CARD_TYPES.MULTIPLE_CHOICE.toString()))));

        for (Rules rule: dataSource.getAllRules()) {
            dataSource.deleteRule(rule);
        }

        dataSource.close();
        dataSource.close();
    }

    @Test
    public void validateCardLimit() {
        QuizDataSource dataSource = new QuizDataSource(mActivityRule.getActivity());
        dataSource.open();
        dataSource.createRule(1, 60000, 5000, getCardTypeString(), 1);
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().loadPreviousRules();
            }
        });
        dataSource.close();

        onView(withId(R.id.new_game)).perform(click());

        onView(withId(R.id.endOfGameScoreText)).check(matches(isDisplayed()));
    }

    private String getCardTypeString() {
        List<Constants.CARD_TYPES> types = new ArrayList<>();
        types.add(Constants.CARD_TYPES.TRUE_FALSE);
        types.add(Constants.CARD_TYPES.MULTIPLE_CHOICE);
        return gson.toJson(types);
    }
}
