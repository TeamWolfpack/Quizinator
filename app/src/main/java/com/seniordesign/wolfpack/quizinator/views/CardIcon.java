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
        this.setBackgroundResource(R.drawable.shape_circle);
        this.setGravity(Gravity.CENTER);
    }

    public CardIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setBackgroundResource(R.drawable.shape_circle);
        this.setGravity(Gravity.CENTER);
    }

    public CardIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setBackgroundResource(R.drawable.shape_circle);
        this.setGravity(Gravity.CENTER);
    }

    public void setIcon(String icon, int color){
        setColorOfShape(color);
        setText(icon);
        setShapeSize(85);
    }

    public void setShapeSize(int size){
        ((GradientDrawable)this.getBackground()).setSize(size, size);
    }

    public void setColorOfShape(int color){
        ((GradientDrawable)this.getBackground()).setColor(color);
    }
}
