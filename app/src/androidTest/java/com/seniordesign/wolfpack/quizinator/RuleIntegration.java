package com.seniordesign.wolfpack.quizinator;

import android.support.test.rule.ActivityTestRule;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Activities.NewGameSettingsActivity;
import com.seniordesign.wolfpack.quizinator.Database.Card;
import com.seniordesign.wolfpack.quizinator.Database.QuizDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
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
        RulesDataSource rulesource = new RulesDataSource(mActivityRule.getActivity());
        rulesource.open();
        rulesource.createRule(5, 90000, 9000, getCardTypeString(), 1);
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

        Rules rule = rulesource.getAllRules().get(rulesource.getAllRules().size() - 1);
        assertTrue("Game time is " + rule.getTimeLimit(), rule.getTimeLimit() == 150000);
        assertTrue(rule.getCardDisplayTime() == 30000);
        assertTrue(rule.getMaxCardCount() == 1);
        assertTrue(rule.getCardTypes().equals(getCardTypeString()));

        rulesource.close();
    }

    @Test
    public void validateLoadingFromDatabase() {
        RulesDataSource rulesource = new RulesDataSource(mActivityRule.getActivity());
        rulesource.open();

        QuizDataSource dataSource = new QuizDataSource(mActivityRule.getActivity());
        dataSource.open();

        dataSource.createDeck("Test", "", "", true, "", new ArrayList<Card>());
        rulesource.createRule(5, 603000, 9000, getCardTypeString(), 1);
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
        onView(withId(R.id.card_type_spinner)).check(matches(withSpinnerText(containsString("All Types"))));

        rulesource.createRule(5, 90000, 10000, getCardTypeString(), 1);
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
        onView(withId(R.id.card_type_spinner)).check(matches(withSpinnerText(containsString("All Types"))));

        for (Rules rule: rulesource.getAllRules()) {
            rulesource.deleteRule(rule);
        }

        rulesource.close();
        dataSource.close();
    }

    @Test
    public void validateCardLimit() {
        RulesDataSource rulesource = new RulesDataSource(mActivityRule.getActivity());
        rulesource.open();
        rulesource.createRule(1, 60000, 5000, getCardTypeString(), 1);
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().loadPreviousRules();
            }
        });
        rulesource.close();

        onView(withId(R.id.new_game)).perform(scrollTo(), click());

        onView(withId(R.id.endOfGameScoreText)).check(matches(isDisplayed()));
    }

    private String getCardTypeString() {
        List<String> types = new ArrayList<>();
        types.add(Constants.CARD_TYPES.TRUE_FALSE.toString());
        types.add(Constants.CARD_TYPES.MULTIPLE_CHOICE.toString());
        return gson.toJson(types);
    }
}
