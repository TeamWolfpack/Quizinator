package com.seniordesign.wolfpack.quizinator;

/**
 * Created by aaron on 12/28/2016.
 */

public final class Constants {

    public static final String NAQT_RULES_URL = "https://www.naqt.com/rules.html";

    public enum CARD_TYPES {
        TRUE_FALSE("True/False"),
        MULTIPLE_CHOICE("Multiple Choice"),
        FREE_RESPONSE("Free Response"),
        VERBAL_RESPONSE("Verbal Response");

        private String typeStr;

        CARD_TYPES(String typesStr) {
            this.typeStr = typesStr;
        }

        @Override
        public String toString(){
            return typeStr;
        }
    }

//    public static final String SHORT_TRUE_FALSE = "TF";
//    public static final String SHORT_MULTIPLE_CHOICE = "MC";
//    public static final String SHORT_FREE_RESPONSE = "FR";
//    public static final String SHORT_VERBAL_RESPONSE = "VR";
//
//    public static final String TRUE_FALSE = "True/False";
//    public static final String MULTIPLE_CHOICE = "Multiple Choice";
//    public static final String FREE_RESPONSE = "Free Response";
//    public static final String VERBAL_RESPONSE = "Verbal Response";
//
//    public static final String LONG_TRUE_FALSE = "True/False";
//    public static final String LONG_MULTIPLE_CHOICE = "Multiple Choice";
//    public static final String LONG_FREE_RESPONSE = "Free Response";
//    public static final String LONG_VERBAL_RESPONSE = "Verbal Response";

    public static final String ALL_CARD_TYPES = "All Types";
    public static final String NO_CARD_TYPES = "None Selected";

    public static final String MAIN_MENU = "Main Menu";
    public static final String HOST_GAME = "Host Game";
    public static final String HOSTING_GAME = "Hosting Game";
    public static final String JOIN_GAME = "Join Game";
    public static final String CARDS = "Cards";
    public static final String GAME_SETTINGS = "Game Settings";
    public static final String RULES = "Rules";
    public static final String GAME_MODE = "GameMode";

    public static final String GAME_MINUTES_ERROR = "Game Minutes can't be empty";
    public static final String GAME_SECONDS_ERROR = "Game Seconds can't be empty";
    public static final String CARD_MINUTES_ERROR = "Card Minutes can't be empty";
    public static final String CARD_SECONDS_ERROR = "Card Seconds can't be empty";
    public static final String CARD_COUNT_ERROR = "Card Count can't be empty";
    public static final String CARD_COUNT_ZERO = "Card Count can't be 0";
}
