package com.seniordesign.wolfpack.quizinator.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.R;

/**
 * Creates a custom TextView that is encompassed by a circle.
 * Makes a TextView look like an icon.
 */
public class CardIcon extends TextView {

    public CardIcon(Context context) {
        super(context);
        init();
    }

    public CardIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CardIcon(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        this.setBackgroundResource(R.drawable.shape_circle);
        this.setGravity(Gravity.CENTER);
    }

    /**
     * Sets the text, color, and size of the icon.
     * @param text of the TextView
     * @param color of the circle shape
     */
    public void setIcon(String text, int color){
        setIcon(text, color, 125);
    }

    /**
     * Sets the text, color, and size of the icon.
     * @param text of the TextView
     * @param color of the circle shape
     * @param iconSize sets the sie of the circle shape
     */
    public void setIcon(String text, int color, int iconSize){
        setColorOfShape(color);
        setText(text);
        setIconSize(iconSize);
        this.setTextColor(Color.WHITE);
        this.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    private void setIconSize(int size){
        ((GradientDrawable)this.getBackground()).setSize(size, size);

    }

    private void setColorOfShape(int color){
        ((GradientDrawable)this.getBackground()).setColor(color);
    }
}
