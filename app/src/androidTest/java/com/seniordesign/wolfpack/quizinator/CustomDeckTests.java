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

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;

import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CustomDeckTests {

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
        QuizDataSource dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        int numOfDecks = dataSource.getAllDecks().size();
        onView(withId(R.id.new_deck_button)).perform(click());
        assertTrue(dataSource.getAllDecks().size() == numOfDecks);
        closeSoftKeyboard();
        onView(withId(R.id.deck_delete_button)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        assertTrue(dataSource.getAllDecks().size() == numOfDecks);
        dataSource.close();
    }

    @Test
    public void validateEditCancelDeck() {
        QuizDataSource dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        int numOfDecks = dataSource.getAllDecks().size();
        onView(withId(R.id.new_deck_button)).perform(click());
        assertTrue(dataSource.getAllDecks().size() == numOfDecks);
        closeSoftKeyboard();
        onView(withId(R.id.deck_cancel_button)).perform(click());

        onView(withId(R.id.new_deck_button)).perform(click());
        closeSoftKeyboard();
        onView(withId(R.id.deck_delete_button)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        assertTrue(dataSource.getAllDecks().size() == numOfDecks);
        dataSource.close();
    }

    @Test
    public void validateEditSaveDeck() {
        QuizDataSource dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        int numOfDecks = dataSource.getAllDecks().size();
        onView(withId(R.id.new_deck_button)).perform(click());
        onView(withId(R.id.edit_deck_name)).perform(clearText(), typeText("FFF111"));
        onView(withId(R.id.deck_save_button)).perform(click());
        onView(withChild(withText("FFF111"))).perform(click());
        closeSoftKeyboard();
        onView(withId(R.id.deck_delete_button)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        assertTrue(dataSource.getAllDecks().size() == numOfDecks);
        dataSource.close();
    }
}
