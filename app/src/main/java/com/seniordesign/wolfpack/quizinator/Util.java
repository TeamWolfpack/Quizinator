package com.seniordesign.wolfpack.quizinator;

import android.widget.ImageView;

import com.seniordesign.wolfpack.quizinator.Database.Card;

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
}
