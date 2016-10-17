package com.seniordesign.wolfpack.quizinator;

import android.support.test.rule.ActivityTestRule;
import android.widget.EditText;

import com.seniordesign.wolfpack.quizinator.Activities.NewGameSettingsActivity;
import com.seniordesign.wolfpack.quizinator.Filters.NumberFilter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @creation 10/10/2016.
 */
public class FilterFocusTests {

    private EditText editText;

    @Rule
    public ActivityTestRule<NewGameSettingsActivity> mActivityRule =
            new ActivityTestRule<>(NewGameSettingsActivity.class);

    @Before
    public void createEditText() {
        editText = new EditText(mActivityRule.getActivity());
    }

    @Test
    public void validateFilter_ChangeFocusWithZero() {
        NumberFilter filter = new NumberFilter(2, 10);

        filter.onFocusChange(editText, false);
        assertEquals("02", editText.getText().toString());
    }

    @Test
    public void validateFilter_ChangeFocusWithoutZero() {
        NumberFilter filter = new NumberFilter(2, 10, false);

        filter.onFocusChange(editText, false);
        assertEquals("2", editText.getText().toString());
    }

    @Test
    public void validateFilter_SingleDigitWithZero() {
        NumberFilter filter = new NumberFilter(2, 10);
        editText.setText("2");

        filter.onFocusChange(editText, false);
        assertEquals("02", editText.getText().toString());
    }

    @Test
    public void validateFilter_SingleDigitWithoutZero() {
        NumberFilter filter = new NumberFilter(2, 10, false);
        editText.setText("2");

        filter.onFocusChange(editText, false);
        assertEquals("2", editText.getText().toString());
    }

    @Test
    public void validateFilter_ContrainToTwoDigits() {
        NumberFilter filter = new NumberFilter();
        editText.setText("00002");

        filter.onFocusChange(editText, false);
        assertEquals("02", editText.getText().toString());
    }
}
