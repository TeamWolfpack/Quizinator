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
    public static final String MULTIPLAYER = "Multiplayer";

    public static final String CARD_TYPE_FILTER = "Card Type Filter";
    public static final String MODERATOR_NEEDED_FILTER = "Moderator Needed Filter";

    public static final String SAVE = "Save";
    public static final String CANCEL = "Cancel";
    public static final String DELETE = "Delete";
    public static final String CLOSE = "Close";
    public static final String NO_WINNER = "No Winner";

    public static final String ACTIVE_PLAYERS = "Active Players";
    public static final String PLAYERS_RESPONSE = "Select a Winner";
    public static final String FINAL_QUESTION = "Final Question";

    public static final String UPDATED_HIGH_SCORE = "Updated High Scores";
    public static final String NO_HIGH_SCORE = "No High Scores";
    public static final String NEW_HIGH_SCORE = "New High Score";

    public static final String GAME_MINUTES_ERROR = "Game Minutes can't be empty";
    public static final String GAME_SECONDS_ERROR = "Game Seconds can't be empty";
    public static final String CARD_MINUTES_ERROR = "Card Minutes can't be empty";
    public static final String CARD_SECONDS_ERROR = "Card Seconds can't be empty";
    public static final String CARD_COUNT_ERROR = "Card Count can't be empty";
    public static final String CARD_COUNT_ZERO = "Card Count can't be 0";
    public static final String CARD_TIME_ZERO = "Card time can't be 0";

    public static final String STRING_0 = "0";
    public static final String STRING_00 = "00";
}
