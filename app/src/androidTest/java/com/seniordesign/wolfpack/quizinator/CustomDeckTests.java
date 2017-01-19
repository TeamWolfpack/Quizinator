package com.seniordesign.wolfpack.quizinator;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.Activities.DecksActivity;
import com.seniordesign.wolfpack.quizinator.Activities.NewGameSettingsActivity;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckDataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by farrowc on 1/17/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CustomDeckTests {

    DeckDataSource deckDataSource;

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
        deckDataSource = new DeckDataSource(InstrumentationRegistry.getTargetContext());
        deckDataSource.open();
        int numOfDecks = deckDataSource.getAllDecks().size();
        onView(withId(R.id.new_item_button)).perform(click());
        int dif = deckDataSource.getAllDecks().size() - numOfDecks;
        assertTrue(dif>0);
        onView(withId(R.id.deck_delete_button)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        dif = deckDataSource.getAllDecks().size() - numOfDecks;
        assertTrue(dif == 0);
    }

    @Test
    public void validateEditCancelDeck() {
        deckDataSource = new DeckDataSource(InstrumentationRegistry.getTargetContext());
        deckDataSource.open();
        int numOfDecks = deckDataSource.getAllDecks().size();
        onView(withId(R.id.new_item_button)).perform(click());
        int dif = deckDataSource.getAllDecks().size() - numOfDecks;
        assertTrue(dif>0);
        onView(withId(R.id.deck_cancel_button)).perform(click());
        onView(withChild(withText("New Deck"))).perform(click());
        onView(withId(R.id.deck_delete_button)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        dif = deckDataSource.getAllDecks().size() - numOfDecks;
        assertTrue(dif == 0);
    }


    @Test
    public void validateEditSaveDeck() {
        deckDataSource = new DeckDataSource(InstrumentationRegistry.getTargetContext());
        deckDataSource.open();
        int numOfDecks = deckDataSource.getAllDecks().size();
        onView(withId(R.id.new_item_button)).perform(click());
        int dif = deckDataSource.getAllDecks().size() - numOfDecks;
        assertTrue(dif>0);
        onView(withId(R.id.edit_deck_name)).perform(clearText());
        onView(withId(R.id.edit_deck_name)).perform(typeText("FFF111"));
        onView(withId(R.id.deck_save_button)).perform(click());
        onView(withChild(withText("FFF111"))).perform(click());
        onView(withId(R.id.deck_delete_button)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        dif = deckDataSource.getAllDecks().size() - numOfDecks;
        assertTrue(dif == 0);
    }
}
