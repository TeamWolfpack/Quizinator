package com.seniordesign.wolfpack.quizinator.Filters;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static com.seniordesign.wolfpack.quizinator.Constants.STRING_0;
import static com.seniordesign.wolfpack.quizinator.Constants.STRING_00;

/**
 * Filter will make sure the time inputs
 * will be between 1 (0 for seconds) and 60
 */
public class NumberFilter implements View.OnFocusChangeListener {

    private int min = 0;
    private int max = 60;
    private boolean beginWithZero = true;

    public NumberFilter() { }

    public NumberFilter(int min) { this.min = min; }

    public NumberFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public NumberFilter(int min, int max, boolean beginWithZero) {
        this.min = min;
        this.max = max;
        this.beginWithZero = beginWithZero;
    }

    public boolean isInRange(int input) {
        return max > min ? input >= min && input <= max : input >= max && input <= min;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) return;

        EditText editText = (EditText)v;
        Editable text = editText.getText();

        try {
            int input = text.toString().equals("") ? 0 : Integer.parseInt(text.toString());

            if (input > max)
                input = max;
            if (input < min)
                input = min;

            editText.setText(String.valueOf(input));

            if (beginWithZero && input < 10)
                editText.setText(STRING_0 + input);
        } catch (NumberFormatException e) {
            editText.setText(String.valueOf(max));
        }
    }
}
