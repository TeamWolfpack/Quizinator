package com.seniordesign.wolfpack.quizinator;

import android.graphics.Color;
import android.os.Environment;

import com.seniordesign.wolfpack.quizinator.database.Card;
import com.seniordesign.wolfpack.quizinator.views.CardIcon;

import java.io.File;

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
     * @param c the card which type needs to be checked
     * @param cardIcon CardIcon to update
     */
    public static void updateCardTypeIcon(Card c, CardIcon cardIcon){
        updateCardTypeIcon(c, cardIcon, 125);
    }

    /**
     * Updates an image view with the image of the card type.
     * @param c the card which type needs to be checked
     * @param cardIcon CardIcon to update
     * @param iconSize size of CardIcon
     */
    public static void updateCardTypeIcon(Card c, CardIcon cardIcon,
                                          int iconSize){
        if (c.getCardType() == TRUE_FALSE.ordinal())
            setCardIconToTF(cardIcon, iconSize);
        else if (c.getCardType() == MULTIPLE_CHOICE.ordinal())
            setCardIconToMC(cardIcon, iconSize);
        else if (c.getCardType() == FREE_RESPONSE.ordinal())
            setCardIconToFR(cardIcon, iconSize);
        else if (c.getCardType() == VERBAL_RESPONSE.ordinal())
            setCardIconToVR(cardIcon, iconSize);
    }

    /**
     * Makes the CardIcon into a true/false card.
     * @param cardIcon to be set
     * @param iconSize size to set icon
     */
    public static void setCardIconToTF(CardIcon cardIcon, int iconSize){
        cardIcon.setIcon("TF", Color.argb(255, 0, 175, 80), iconSize);
    }

    /**
     * Makes the CardIcon into a multiple choice card.
     * @param cardIcon to be set
     * @param iconSize size to set icon
     */
    public static void setCardIconToMC(CardIcon cardIcon, int iconSize){
        cardIcon.setIcon("MC", Color.argb(255, 0, 112, 191), iconSize);
    }

    /**
     * Makes the CardIcon into a free response card.
     * @param cardIcon to be set
     * @param iconSize size to set icon
     */
    public static void setCardIconToFR(CardIcon cardIcon, int iconSize){
        cardIcon.setIcon("FR", Color.argb(255, 253, 191, 1), iconSize);
    }

    /**
     * Makes the CardIcon into a verbal response card.
     * @param cardIcon to be set
     * @param iconSize size to set icon
     */
    public static void setCardIconToVR(CardIcon cardIcon, int iconSize){
        cardIcon.setIcon("VR", Color.argb(255, 255, 0, 250), iconSize);
    }

    /**
     *  Checks if external storage is available for read and write
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     *  Checks if external storage is available to at least read
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File defaultDirectory(){
        File dir = new File(
                Environment.getExternalStorageDirectory().getPath(),
                "Quizinator"); //stores to /storage/emulated/0
        if(!dir.exists())
            dir.mkdirs();
        return dir;
    }
}
