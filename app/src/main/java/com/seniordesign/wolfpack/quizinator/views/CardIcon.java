package com.seniordesign.wolfpack.quizinator.views;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.seniordesign.wolfpack.quizinator.R;

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

    private void init(){
        this.setBackgroundResource(R.drawable.shape_circle);
        this.setGravity(Gravity.CENTER);
    }

    public void setIcon(String icon, int color){
        setIcon(icon, color, 125);
    }

    public void setIcon(String icon, int color, int iconSize){
        setColorOfShape(color);
        setText(icon);
        setIconSize(iconSize);
    }

    private void setIconSize(int size){
        ((GradientDrawable)this.getBackground()).setSize(size, size);
    }

    private void setColorOfShape(int color){
        ((GradientDrawable)this.getBackground()).setColor(color);
    }
}
