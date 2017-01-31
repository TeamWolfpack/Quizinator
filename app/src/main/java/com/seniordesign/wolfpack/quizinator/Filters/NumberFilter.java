package com.seniordesign.wolfpack.quizinator.Filters;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;

/**
 * Filter will make sure the time inputs
 * will be between 1 (0 for seconds) and 60
 */
public class NumberFilter implements InputFilter, View.OnFocusChangeListener {

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
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(input))
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) return;

        EditText editText = (EditText)v;
        Editable text = editText.getText();
        if (beginWithZero) {
            if (text.toString().equals("")) {
                editText.setText("0" + min);
            }
            if (text.length() == 1) {
                editText.setText("0" + text);
            }
        } else {
            if (text.toString().equals("")) {
                editText.setText("" + min);
            }
        }
        if (text.length() > 2) {
            editText.setText(text.subSequence(text.length() - 2, text.length()));
        }
    }
}
