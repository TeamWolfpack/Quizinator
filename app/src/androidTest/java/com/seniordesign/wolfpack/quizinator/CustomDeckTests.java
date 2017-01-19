package com.seniordesign.wolfpack.quizinator;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.Activities.DecksActivity;
import com.seniordesign.wolfpack.quizinator.Database.QuizDataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;

import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

/**
 * Created by farrowc on 1/17/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CustomDeckTests {

    QuizDataSource dataSource;

    @Rule
    public ActivityTestRule<DecksActivity> mActivityRule =
            new ActivityTestRule<>(DecksActivity.class);

    @Before
    public void unlockScreen() {
        final DecksActivity activity = mActivityRule.getActivity();
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
    public void validateCreateAndDeleteDeck() {
        dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        int numOfDecks = dataSource.getAllDecks().size();
        onView(withId(R.id.new_item_button)).perform(click());
        int dif = dataSource.getAllDecks().size() - numOfDecks;
        assertTrue(dif>0);
        onView(withId(R.id.deck_delete_button)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        dif = dataSource.getAllDecks().size() - numOfDecks;
        assertTrue(dif == 0);
    }

    @Test
    public void validateEditCancelDeck() {
        dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        int numOfDecks = dataSource.getAllDecks().size();
        onView(withId(R.id.new_item_button)).perform(click());
        int dif = dataSource.getAllDecks().size() - numOfDecks;
        assertTrue(dif>0);
        onView(withId(R.id.deck_cancel_button)).perform(click());
        onView(withChild(withText("New Deck"))).perform(click());
        onView(withId(R.id.deck_delete_button)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        dif = dataSource.getAllDecks().size() - numOfDecks;
        assertTrue(dif == 0);
    }


    @Test
    public void validateEditSaveDeck() {
        dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        int numOfDecks = dataSource.getAllDecks().size();
        onView(withId(R.id.new_item_button)).perform(click());
        int dif = dataSource.getAllDecks().size() - numOfDecks;
        assertTrue(dif>0);
        onView(withId(R.id.edit_deck_name)).perform(clearText());
        onView(withId(R.id.edit_deck_name)).perform(typeText("FFF111"));
        onView(withId(R.id.deck_save_button)).perform(click());
        onView(withChild(withText("FFF111"))).perform(click());
        onView(withId(R.id.deck_delete_button)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        dif = dataSource.getAllDecks().size() - numOfDecks;
        assertTrue(dif == 0);
    }
}
