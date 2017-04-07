package com.seniordesign.wolfpack.quizinator;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.DataInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;
import android.widget.ListView;

import com.seniordesign.wolfpack.quizinator.activities.HighscoresActivity;
import com.seniordesign.wolfpack.quizinator.adapters.HighScoreAdapter;
import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.database.Deck;
import com.seniordesign.wolfpack.quizinator.database.HighScores;
import com.seniordesign.wolfpack.quizinator.database.QuizDataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
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
        QuizDataSource dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        deleteAllHighscores(dataSource);
        dataSource.createHighScore(1, 1000, 1);
        updateListView(dataSource);

        onView(withId(R.id.list_of_highscores)).check(matches(isDisplayed()));

        deleteAllHighscores(dataSource);
        dataSource.close();
    }

    @Test
    public void populateHighscoreList() {
        QuizDataSource dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        deleteAllHighscores(dataSource);
        dataSource.createHighScore(1, 1000, 1);
        updateListView(dataSource);

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

    @Test
    public void displayTimeCorrectly() {
        QuizDataSource dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        deleteAllHighscores(dataSource);
        dataSource.createHighScore(1, 603000, 1);
        updateListView(dataSource);

        DataInteraction highsoreView = onData(anything())
                .inAdapterView(withId(R.id.list_of_highscores))
                .atPosition(0); // atPosition is 0 indexed
        highsoreView.onChildView(withId(R.id.array_adapter_hs_time)).check(matches(withText(containsString("10:03"))));

        deleteAllHighscores(dataSource);
        dataSource.close();
    }

    @Test
    public void displayMultipleHighscores() {
        QuizDataSource dataSource = new QuizDataSource(InstrumentationRegistry.getTargetContext());
        dataSource.open();
        deleteAllHighscores(dataSource);
        dataSource.createDeck("Highscore", "", "", true, "Test", new ArrayList<Card>());
        dataSource.createHighScore(1, 3000, 1);
        dataSource.createHighScore(2, 3000, 1);
        updateListView(dataSource);

        Deck deck1 = dataSource.getDeckWithId(1);
        Deck deck2 = dataSource.getDeckWithId(2);
        DataInteraction defaultView = onData(anything())
                .inAdapterView(withId(R.id.list_of_highscores))
                .atPosition(0); // atPosition is 0 indexed
        DataInteraction secondView = onData(anything())
                .inAdapterView(withId(R.id.list_of_highscores))
                .atPosition(1); // atPosition is 0 indexed
        defaultView.onChildView(withId(R.id.array_adapter_hs_score)).check(matches(withText(containsString("1"))));
        defaultView.onChildView(withId(R.id.array_adapter_hs_time)).check(matches(withText(containsString("0:03"))));
        defaultView.onChildView(withId(R.id.array_adapter_hs_deck)).check(matches(withText(containsString(deck1.getDeckName()))));
        secondView.onChildView(withId(R.id.array_adapter_hs_score)).check(matches(withText(containsString("1"))));
        secondView.onChildView(withId(R.id.array_adapter_hs_time)).check(matches(withText(containsString("0:03"))));
        secondView.onChildView(withId(R.id.array_adapter_hs_deck)).check(matches(withText(containsString(deck2.getDeckName()))));

        deleteAllHighscores(dataSource);
        dataSource.close();
    }

    private void deleteAllHighscores(QuizDataSource dataSource) {
        List<HighScores> hsList = dataSource.getAllHighScores();
        for (HighScores hs: hsList)
            dataSource.deleteHighScore(hs);
    }

    private void updateListView(final QuizDataSource dataSource) {
        try {
            mActivityRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ListView list = (ListView) mActivityRule.getActivity().findViewById(R.id.list_of_highscores);
                    HighScoreAdapter adapter = (HighScoreAdapter) list.getAdapter();
                    adapter.clear();
                    adapter.addAll(dataSource.getAllHighScores());
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
