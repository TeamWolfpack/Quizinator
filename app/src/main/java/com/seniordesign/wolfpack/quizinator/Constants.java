package com.seniordesign.wolfpack.quizinator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Constants {

    public static final String NAQT_RULES_URL = "https://www.naqt.com/rules.html";
    public static final String HELP_SINGLEPLAYER = "https://youtu.be/FbXONr6h9oY";
    public static final String HELP_MULTI_HOST = "https://youtu.be/zGIkaLsUCIc";
    public static final String HELP_MULTI_PLAYER = "https://youtu.be/p-V12KBi6Zs";
    public static final String HELP_CUSTOM = "https://youtu.be/k4tVptQPOLs";
    public static final String HELP_RULES = "https://youtu.be/NG-r5ds-GVM";

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

        public static List<CARD_TYPES> getAllCardTypes(){
           return new ArrayList<>(Arrays.asList(TRUE_FALSE, MULTIPLE_CHOICE, FREE_RESPONSE, VERBAL_RESPONSE));
        }
    }

    public static final String ALL_CARD_TYPES = "All Types";
    public static final String NO_CARD_TYPES = "None Selected";

    public static final String DEFAULT_SINGLE_RULESET = "Default Single";
    public static final String DEFAULT_MULTIPLE_RULESET = "Default";
    public static final String DOUBLE_DOWN_RULESET = "Double Down";

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
    public static final String END_OF_GAMEPLAY = "Game Over";

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
    public static final String ACCEPT = "Accept";
    public static final String PLAYER_WAGERS = "Players' Wagers";
    public static final String SEND_CARD = "Send Card";

    public static final String UPDATED_HIGH_SCORE = "Updated High Scores";
    public static final String NO_HIGH_SCORE = "No High Scores";
    public static final String NEW_HIGH_SCORE = "New High Score";
    public static final String HIGH_SCORE_SCORE = "Score: ";
    public static final String HIGH_SCORE_TIME = "Time: ";

    public static final String GAME_MINUTES_ERROR = "Game Minutes can't be empty";
    public static final String GAME_SECONDS_ERROR = "Game Seconds can't be empty";
    public static final String CARD_MINUTES_ERROR = "Card Minutes can't be empty";
    public static final String CARD_SECONDS_ERROR = "Card Seconds can't be empty";
    public static final String CARD_COUNT_ERROR = "Card Count can't be empty";
    public static final String CARD_COUNT_ZERO = "Card Count can't be 0";
    public static final String CARD_TIME_ZERO = "Card time can't be 0";

    public static final String STRING_0 = "0";
    public static final String STRING_00 = "00";

    public static final String NO_DECK_WARNING = "Need at least one Deck before starting";
    public static final String NO_HIGHSCORES_WARNING = "You do not have any high scores yet. Try playing a game first.";
}
