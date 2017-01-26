package com.seniordesign.wolfpack.quizinator;

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

    public static final String ALL_CARD_TYPES = "All Types";
    public static final String NO_CARD_TYPES = "None Selected";

    public static final String MAIN_MENU = "Main Menu";
    public static final String HOST_GAME = "Host Game";
    public static final String HOSTING_GAME = "Hosting Game";
    public static final String JOIN_GAME = "Join Game";
    public static final String CARDS = "Cards";
    public static final String DECKS = "Decks";
    public static final String GAME_SETTINGS = "Game Settings";
    public static final String RULES = "Rules";
    public static final String GAME_MODE = "GameMode";

    public static final String UPDATED_HIGH_SCORE = "Updated High Scores";
    public static final String NO_HIGH_SCORE = "No High Scores";
    public static final String NEW_HIGH_SCORE = "New High Score";

    public static final String GAME_MINUTES_ERROR = "Game Minutes can't be empty";
    public static final String GAME_SECONDS_ERROR = "Game Seconds can't be empty";
    public static final String CARD_MINUTES_ERROR = "Card Minutes can't be empty";
    public static final String CARD_SECONDS_ERROR = "Card Seconds can't be empty";
    public static final String CARD_COUNT_ERROR = "Card Count can't be empty";
    public static final String CARD_COUNT_ZERO = "Card Count can't be 0";
}
