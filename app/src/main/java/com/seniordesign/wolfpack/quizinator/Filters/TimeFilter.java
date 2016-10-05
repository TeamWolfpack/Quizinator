package com.seniordesign.wolfpack.quizinator.Filters;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Filter will make sure the time inputs
 * will be between 1 (0 for seconds) and 60
 */
public class TimeFilter implements InputFilter, View.OnFocusChangeListener {

    private int min = 0;
    private int max = 60;

    public TimeFilter() { }

    public TimeFilter(int min) { this.min = min; }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) return;

        EditText editText = (EditText)v;
        Editable text = editText.getText();
        if (text.toString().equals("")) {
            editText.setText("0" + min);
        }
        if (text.length() == 1) {
            editText.setText("0" + text);
        }
    }
}
