package com.seniordesign.wolfpack.quizinator;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.views.CardIcon;

import static com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES.FREE_RESPONSE;
import static com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES.MULTIPLE_CHOICE;
import static com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES.TRUE_FALSE;
import static com.seniordesign.wolfpack.quizinator.Constants.CARD_TYPES.VERBAL_RESPONSE;

/**
 * The Util class is meant to act as an extension class. All methods
 * must be static and no state should exist here.
 *
 * If this class exceeds four to five hundred lines, we should group
 * methods with similar functionality and create more util classes.
 */
public class Util {

    /**
     * Updates an image view with the image of the card type.
     * @param c the card thats' type needs to be checked
     * @param iv the image view to update
     */
    public static void updateCardTypeIcon(Card c, ImageView iv){
        if (c.getCardType() == TRUE_FALSE.ordinal())
            iv.setImageResource(R.drawable.tf_icon);
        else if (c.getCardType() == MULTIPLE_CHOICE.ordinal())
            iv.setImageResource(R.drawable.mc_icon);
        else if (c.getCardType() == FREE_RESPONSE.ordinal())
            iv.setImageResource(R.drawable.fr_icon);
        else if (c.getCardType() == VERBAL_RESPONSE.ordinal())
            iv.setImageResource(R.drawable.vr_icon);
    }

    //TODO
        //remove imageview and replace with CardIcon view
        //create colors in styles for all card types
        //remove png files
        //check to see if you can remove shape object and just
            //create it dynamically
    public static void updateCardTypeIcon2(Card c, CardIcon cardIcon){
        if (c.getCardType() == TRUE_FALSE.ordinal())
            cardIcon.setIcon("TF", Color.argb(255, 0, 175, 80));
        else if (c.getCardType() == MULTIPLE_CHOICE.ordinal())
            cardIcon.setIcon("MC", Color.argb(255, 0, 112, 191));
        else if (c.getCardType() == FREE_RESPONSE.ordinal())
            cardIcon.setIcon("FR", Color.argb(255, 253, 191, 1));
        else if (c.getCardType() == VERBAL_RESPONSE.ordinal())
            cardIcon.setIcon("VR", Color.argb(255, 255, 0, 250));
    }
}
