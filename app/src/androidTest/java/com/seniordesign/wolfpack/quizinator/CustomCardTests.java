package com.seniordesign.wolfpack.quizinator;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.Activities.CardsActivity;
import com.seniordesign.wolfpack.quizinator.Database.QuizDataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.anything;

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
                .atPosition(10)
                .perform(click());
        onView(withText(Constants.DELETE)).perform(click());
        onView(withText(Constants.DELETE)).perform(click());
        assertTrue(dataSource.getAllCards().size() == numOfCards);
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
