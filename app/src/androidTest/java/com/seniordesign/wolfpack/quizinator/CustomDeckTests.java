package com.seniordesign.wolfpack.quizinator;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.Activities.CardsActivity;
import com.seniordesign.wolfpack.quizinator.Activities.DecksActivity;
import com.seniordesign.wolfpack.quizinator.Database.Deck;
import com.seniordesign.wolfpack.quizinator.Database.QuizDataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.anything;

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
        pressBack();

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

    @Test
    public void deleteCard_RemovesFromDeck() {
        QuizDataSource dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();

        //Add temp card to be deleted
        dataSource.createCard(Constants.CARD_TYPES.TRUE_FALSE.ordinal(),
                "Card to Delete",
                Boolean.toString(true),
                new String[]{Boolean.toString(true), Boolean.toString(true)},
                1,
                Boolean.toString(false));

        //Create new deck with new card
        onView(withId(R.id.new_deck_button)).perform(click());
        closeSoftKeyboard();
        onData(anything())
                .inAdapterView(withId(R.id.cards_in_database))
                .atPosition(10) // atPosition is 0 indexed
                .perform(click());
        onView(withId(R.id.deck_save_button)).perform(click());

        //Delete the card
        ActivityTestRule<CardsActivity> cardsActivityRule = new ActivityTestRule<>(CardsActivity.class, true, true);
        cardsActivityRule.launchActivity(new Intent());
        onData(anything())
                .inAdapterView(withId(R.id.list_of_cards))
                .atPosition(10) // atPosition is 0 indexed
                .perform(click());
        onView(withText(Constants.DELETE)).perform(click());
        onView(withText(Constants.DELETE)).perform(click());

        //Make sure the card is removed from the deck
        List<Deck> decks = dataSource.getAllDecks();
        Deck deck = dataSource.getDeckWithId(decks.size());
        assertTrue("Deck size (" + deck.getCards() + ") is empty", deck.getCards().isEmpty());

        dataSource.deleteDeck(deck);
        dataSource.close();
    }
}
