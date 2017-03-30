package com.seniordesign.wolfpack.quizinator;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.activities.CardsActivity;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CustomCardTests {

    @Rule
    public ActivityTestRule<CardsActivity> mActivityRule =
            new ActivityTestRule<>(CardsActivity.class);

    @Before
    public void unlockScreen() {
        final CardsActivity activity = mActivityRule.getActivity();
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
    public void validatedCardCreationAndDeletion() {
        QuizDataSource dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        int numOfCards = dataSource.getAllCards().size();
        onView(withId(R.id.new_card_button)).perform(click());
        onView(withText(Constants.SAVE)).perform(click());
        assertTrue(dataSource.getAllCards().size() > numOfCards);

        onData(anything())
                .inAdapterView(withId(R.id.list_of_cards))
                .atPosition(25)
                .perform(click());
        onView(withText(Constants.DELETE)).perform(click());
        onView(withText(Constants.DELETE)).perform(click());
        assertTrue(dataSource.getAllCards().size() == numOfCards);
        dataSource.close();
    }

    @Test
    public void switchToMultipleChoiceCard() {
        QuizDataSource dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        onView(withId(R.id.new_card_button)).perform(click());
        onView(withId(R.id.edit_card_card_type_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(Constants.CARD_TYPES.MULTIPLE_CHOICE.toString())))
                .inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.answer_row_2_1)).check(matches(isDisplayed()));
        onView(withId(R.id.answer_row_2_2)).check(matches(isDisplayed()));
        onView(withId(R.id.answer_row_2_3)).check(matches(isDisplayed()));

        onView(withText(Constants.SAVE)).perform(click());
        List<Card> cards = dataSource.getAllCards();
        Card card = cards.get(cards.size() - 1);
        assertTrue(card.getCardType() == Constants.CARD_TYPES.MULTIPLE_CHOICE.ordinal());

        dataSource.deleteCard(card);
        dataSource.close();
    }

    @Test
    public void cancelNewCard_DoesNotSaveCard() {
        QuizDataSource dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        int numOfCards = dataSource.getAllCards().size();
        onView(withId(R.id.new_card_button)).perform(click());
        onView(withText(Constants.CANCEL)).perform(click());
        assertTrue(dataSource.getAllCards().size() == numOfCards);
        dataSource.close();
    }
}
