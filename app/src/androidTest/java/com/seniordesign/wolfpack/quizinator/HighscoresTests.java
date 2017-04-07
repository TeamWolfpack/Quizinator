package com.seniordesign.wolfpack.quizinator;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.DataInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.seniordesign.wolfpack.quizinator.activities.HighscoresActivity;
import com.seniordesign.wolfpack.quizinator.database.Deck;
import com.seniordesign.wolfpack.quizinator.database.HighScores;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class HighscoresTests {

    // Needed to run in Travis
    // **********************************************
    @Rule
    public ActivityTestRule<HighscoresActivity> mActivityRule = new ActivityTestRule<>(HighscoresActivity.class);

    @Before
    public void unlockScreen() {
        final HighscoresActivity activity = mActivityRule.getActivity();
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
    public void checkHighscoreList() {
        onView(withId(R.id.list_of_highscores)).check(matches(isDisplayed()));
    }

    @Test
    public void populateHighscoreList() {
        QuizDataSource dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        deleteAllHighscores(dataSource);
        dataSource.createHighScore(1, 1000, 1);

        Deck deck = dataSource.getDeckWithId(1);
        DataInteraction highsoreView = onData(anything())
                .inAdapterView(withId(R.id.list_of_highscores))
                .atPosition(0); // atPosition is 0 indexed

        highsoreView.onChildView(withId(R.id.array_adapter_hs_score)).check(matches(withText(containsString("1"))));
        highsoreView.onChildView(withId(R.id.array_adapter_hs_time)).check(matches(withText(containsString("0:01"))));
        highsoreView.onChildView(withId(R.id.array_adapter_hs_deck)).check(matches(withText(containsString(deck.getDeckName()))));

        deleteAllHighscores(dataSource);
        dataSource.close();
    }

    private void deleteAllHighscores(QuizDataSource dataSource) {
        List<HighScores> hsList = dataSource.getAllHighScores();
        for (HighScores hs: hsList)
            dataSource.deleteHighScore(hs);
    }
}
