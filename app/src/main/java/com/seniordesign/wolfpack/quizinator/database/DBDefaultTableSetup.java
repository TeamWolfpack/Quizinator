package com.seniordesign.wolfpack.quizinator.database;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Constants;

class DBDefaultTableSetup {

    static void setupDefaultTables(SQLiteDatabase db){
        setDefaultCardSet(db);
        setDefaultDeckSet(db);
        setDefaultCdRelationSet(db);
        setDefaultRuleSet(db);
    }

    private static void setDefaultCardSet(SQLiteDatabase db) {
        Gson gson = new Gson();

        final String[] TRUE_FALSE_ANSWERS = new String[]{String.valueOf(true), String.valueOf(false)};

        String query = ("insert into " + QuizSQLiteHelper.TABLE_CARDS) +

                //Multiple Choice
                " SELECT \'" + "1" + "\' AS \'" + QuizSQLiteHelper.CARD_COLUMN_ID + "\'," +
                    "\'" + Constants.CARD_TYPES.MULTIPLE_CHOICE.ordinal() + "\' AS \'" + QuizSQLiteHelper.CARD_COLUMN_CARDTYPE + "\', " +
                    "\'" + "1+1 = ?" + "\' AS \'" + QuizSQLiteHelper.CARD_COLUMN_QUESTION + "\'," +
                    "\'" + "2" + "\' AS \'" + QuizSQLiteHelper.CARD_COLUMN_CORRECTANSWER + "\'," +
                    "\'" + gson.toJson(new String[]{"2", "1", "4", "3"}) + "\' AS \'" + QuizSQLiteHelper.CARD_COLUMN_POSSIBLEANSWERS + "\'," +
                    "\'" + "1" + "\' AS \'" + QuizSQLiteHelper.CARD_COLUMN_POINTS + "\'," +
                    "\'" + false + "\' AS \'" + QuizSQLiteHelper.CARD_COLUMN_MODERATORNEEDED + "\' " +
                "UNION ALL SELECT \'" + "2" + "\'," +
                    "\'" + Constants.CARD_TYPES.MULTIPLE_CHOICE.ordinal() + "\', " +
                    "\'" + "What is the symbol for Iron?" + "\', " +
                    "\'" + "Fe" + "\', " +
                    "\'" + gson.toJson(new String[]{"Fe", "Ir", "I", "C"}) + "\', " +
                    "\'" + "1" + "\', " +
                    "\'" + false + "\' " +
                "UNION ALL SELECT \'" + "3" + "\'," +
                    "\'" + Constants.CARD_TYPES.MULTIPLE_CHOICE.ordinal() + "\'," +
                    "\'" + "Who was the 2nd president of the US?" + "\'," +
                    "\'" + "John Adams" + "\'," +
                    "\'" + gson.toJson(new String[]{"George Washington", "Thomas Jefferson", "Abraham Lincoln", "John Adams"}) + "\'," +
                    "\'" + "1" + "\'," +
                    "\'" + false + "\' " +
                "UNION ALL SELECT \'" + "4" + "\'," +
                    "\'" + Constants.CARD_TYPES.MULTIPLE_CHOICE.ordinal() + "\'," +
                    "\'" + "What restaurant chain has the Golden Arches?" + "\'," +
                    "\'" + "McDonalds" + "\'," +
                    "\'" + gson.toJson(new String[]{"McDonalds", "Wendys", "Subway", "Burger King"}) + "\'," +
                    "\'" + "1" + "\'," +
                    "\'" + false + "\' " +
                "UNION ALL SELECT \'" + "5" + "\'," +
                    "\'" + Constants.CARD_TYPES.MULTIPLE_CHOICE.ordinal() + "\'," +
                    "\'" + "Which of the following is a coastal state?" + "\'," +
                    "\'" + "Maine" + "\'," +
                    "\'" + gson.toJson(new String[]{"Maine", "Wisconsin", "Idaho", "Tennessee"}) + "\'," +
                    "\'" + "1" + "\'," +
                    "\'" + false + "\' " +

                //True False
                "UNION ALL SELECT \'" + "6" + "\', " + "\'" +
                    Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
                    "\'1*2 = 0\', " +
                    "\'" + false + "\', " +
                    "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
                    "\'1\', " +
                    "\'" + false + "\' " +
                "UNION ALL SELECT \'" + "7" + "\', " + "\'" +
                    Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
                    "\'4*5 = 20\', " +
                    "\'" + true + "\', " +
                    "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
                    "\'1\', " +
                    "\'" + false + "\' " +
                "UNION ALL SELECT \'" + "8" + "\', " + "\'" +
                    Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
                    "\'20*10 = 100\', " +
                    "\'" + false + "\', " +
                    "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
                    "\'1\', " +
                    "\'" + false + "\' " +
                "UNION ALL SELECT \'" + "9" + "\', " + "\'" +
                    Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
                    "\'10*91 = 901\', " +
                    "\'" + false + "\', " +
                    "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
                    "\'1\', " +
                    "\'" + false + "\' " +
                "UNION ALL SELECT \'" + "10" + "\', " + "\'" +
                    Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
                    "\'100^2 = 10000\', " +
                    "\'" + true + "\', " +
                    "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
                    "\'1\', " +
                    "\'" + false + "\' " +
                "UNION ALL SELECT \'" + "11" + "\', " + "\'" +
                    Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
                    "\'10*102 = 1002\', " +
                    "\'" + false + "\', " +
                    "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
                    "\'1\', " +
                    "\'" + false + "\' " +
                "UNION ALL SELECT \'" + "12" + "\', " + "\'" +
                    Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
                    "\'8/2 = 4\', " +
                    "\'" + true + "\', " +
                    "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
                    "\'1\', " +
                    "\'" + false + "\' " +
                "UNION ALL SELECT \'" + "13" + "\', " + "\'" +
                    Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
                    "\'120/4 = 30\', " +
                    "\'" + true + "\', " +
                    "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
                    "\'1\', " +
                    "\'" + false + "\' " +
                "UNION ALL SELECT \'" + "14" + "\', " + "\'" +
                    Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
                    "\'6*7 = 41\', " +
                    "\'" + false + "\', " +
                    "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
                    "\'1\', " +
                    "\'" + false + "\' " +
                "UNION ALL SELECT \'" + "15" + "\', " + "\'" +
                    Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
                    "\'Does MSOE stand for Milwaukee School of Engineering?\', " +
                    "\'" + true + "\', " +
                    "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
                    "\'1\', " +
                    "\'" + false + "\' " +

                //Free Response
                "UNION ALL SELECT \'" + "16" + "\', " + "\'" +
                    Constants.CARD_TYPES.FREE_RESPONSE.ordinal() + "\', " +
                    "\'Mass * Acceleration = ?\', " +
                    "\'" + "Force" + "\', " +
                    "\'" + "" + /* Look at Answer field */"\', " +
                    "\'1\', " +
                    "\'" + true + "\' " +
                "UNION ALL SELECT \'" + "17" + "\', " + "\'" +
                    Constants.CARD_TYPES.FREE_RESPONSE.ordinal() + "\', " +
                    "\'What mathematical property says (a + b) + c = a + (b + c)?\', " +
                    "\'" + "Associative Property" + "\', " +
                    "\'" + "" + /* Look at Answer field */"\', " +
                    "\'1\', " +
                    "\'" + true + "\' " +
                "UNION ALL SELECT \'" + "18" + "\', " + "\'" +
                    Constants.CARD_TYPES.FREE_RESPONSE.ordinal() + "\', " +
                    "\'What is the abbreviation for Tyrannosaurus?\', " +
                    "\'" + "T-Rex" + "\', " +
                    "\'" + "" + /* Look at Answer field */"\', " +
                    "\'1\', " +
                    "\'" + true + "\' " +
                "UNION ALL SELECT \'" + "19" + "\', " + "\'" +
                    Constants.CARD_TYPES.FREE_RESPONSE.ordinal() + "\', " +
                    "\'Which color has the longest wavelength in the color spectrum?\', " +
                    "\'" + "Red" + "\', " +
                    "\'" + "" + /* Look at Answer field */"\', " +
                    "\'1\', " +
                    "\'" + true + "\' " +
                "UNION ALL SELECT \'" + "20" + "\', " + "\'" +
                    Constants.CARD_TYPES.FREE_RESPONSE.ordinal() + "\', " +
                    "\'How many members are in the Senate?\', " +
                    "\'" + "100" + "\', " +
                    "\'" + "" + /* Look at Answer field */"\', " +
                    "\'1\', " +
                    "\'" + true + "\' " +

                //Verbal Response
                "UNION ALL SELECT \'" + "21" + "\', " + "\'" +
                    Constants.CARD_TYPES.VERBAL_RESPONSE.ordinal() + "\', " +
                    "\'What is the first right in the Bill of Rights?\', " +
                    "\'" + "Freedom of Speech, Freedom of Religion, Freedom of Assembly" + "\', " +
                    "\'" + "" + /* Look at Answer field */"\', " +
                    "\'1\', " +
                    "\'" + true + "\' " +
                "UNION ALL SELECT \'" + "22" + "\', " + "\'" +
                    Constants.CARD_TYPES.VERBAL_RESPONSE.ordinal() + "\', " +
                    "\'Name one of the three astronauts that were on Apollo 11?\', " +
                    "\'" + "Neil Armstrong, Edwin \"Buzz\" Aldrin, or Michael Collins" + "\', " +
                    "\'" + "" + /* Look at Answer field */"\', " +
                    "\'1\', " +
                    "\'" + true + "\' " +
                "UNION ALL SELECT \'" + "23" + "\', " + "\'" +
                    Constants.CARD_TYPES.VERBAL_RESPONSE.ordinal() + "\', " +
                    "\'What are the three branches of the United States government?\', " +
                    "\'" + "Legislative, Judicial, and Executive" + "\', " +
                    "\'" + "" + /* Look at Answer field */"\', " +
                    "\'1\', " +
                    "\'" + true + "\' " +
                "UNION ALL SELECT \'" + "24" + "\', " + "\'" +
                    Constants.CARD_TYPES.VERBAL_RESPONSE.ordinal() + "\', " +
                    "\'Name all seven continents.\', " +
                    "\'" + "North America, South America, Africa, Europe, Asia, and Antarctica" + "\', " +
                    "\'" + "" + /* Look at Answer field */"\', " +
                    "\'1\', " +
                    "\'" + true + "\' " +
                "UNION ALL SELECT \'" + "25" + "\', " + "\'" +
                    Constants.CARD_TYPES.VERBAL_RESPONSE.ordinal() + "\', " +
                    "\'Give an example of Classical conditioning (aka. Pavlovian or Respondent conditioning) at work.\', " +
                    "\'" + "Having a person or animal elicit a response from a neutral stimulus through repeated negative or positive re-enforcement." + "\', " +
                    "\'" + "" + /* Look at Answer field */"\', " +
                    "\'1\', " +
                    "\'" + true + "\';";
        db.execSQL(query);
    }

    private static void setDefaultDeckSet(SQLiteDatabase db) {
        String query = ("insert into " + QuizSQLiteHelper.TABLE_DECKS) +
                " SELECT \'" + "1" + "\' AS \'" + QuizSQLiteHelper.DECK_COLUMN_ID + "\'," +
                    "\'" + "Default" + "\' AS \'" + QuizSQLiteHelper.DECK_COLUMN_DECKNAME + "\'," +
                    "\'" + "General" + "\' AS \'" + QuizSQLiteHelper.DECK_COLUMN_CATEGORY + "\'," +
                    "\'" + "General" + "\' AS \'" + QuizSQLiteHelper.DECK_COLUMN_SUBJECT + "\'," +
                    "\'" + true + "\' AS \'" + QuizSQLiteHelper.DECK_COLUMN_DUPLICATECARDS + "\'," +
                    "\'" + "Team Wolfpack" + "\' AS \'" + QuizSQLiteHelper.DECK_COLUMN_OWNER + "\';";
        db.execSQL(query);
    }

    private static void setDefaultCdRelationSet(SQLiteDatabase db) {
        String query = ("insert into " + QuizSQLiteHelper.TABLE_CDRELATIONS) +
                " SELECT \'1\' AS \'" + QuizSQLiteHelper.CDRELATIONS_COLUMN_ID + "\', " +
                    "\'1\' AS \'" + QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD + "\', " +
                    "\'1\' AS \'" + QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK + "\' " +
                " UNION ALL SELECT \'2\', " +
                    "\'2\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'3\', " +
                    "\'3\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'4\', " +
                    "\'4\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'5\', " +
                    "\'5\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'6\', " +
                    "\'6\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'7\', " +
                    "\'7\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'8\', " +
                    "\'8\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'9\', " +
                    "\'9\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'10\', " +
                    "\'10\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'11\', " +
                    "\'11\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'12\', " +
                    "\'12\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'13\', " +
                    "\'13\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'14\', " +
                    "\'14\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'15\', " +
                    "\'15\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'16\', " +
                    "\'16\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'17\', " +
                    "\'17\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'18\', " +
                    "\'18\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'19\', " +
                    "\'19\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'20\', " +
                    "\'20\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'21\', " +
                    "\'21\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'22\', " +
                    "\'22\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'23\', " +
                    "\'23\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'24\', " +
                    "\'24\', " +
                    "\'1\' " +
                " UNION ALL SELECT \'25\', " +
                    "\'25\', " +
                    "\'1\';";
        db.execSQL(query);
    }

    static void setDefaultRuleSet(SQLiteDatabase db) {
        String cardTypes = new Gson().toJson(Constants.CARD_TYPES.getAllCardTypes());
        String query = ("insert into " + QuizSQLiteHelper.TABLE_RULESETS) +
                " SELECT \'1\' AS \'" + QuizSQLiteHelper.RULES_COLUMN_ID + "\', " +
                    "\'60000\' AS \'" + QuizSQLiteHelper.RULES_COLUMN_TIMELIMIT + "\', " +
                    "\'600000\' AS \'" + QuizSQLiteHelper.RULES_COLUMN_CARDDISPLAYTIME + "\' " +
                    "\'15\' AS \'" + QuizSQLiteHelper.RULES_COLUMN_MAXCARDCOUNT + "\' " +
                    "\'" + cardTypes + "\' AS \'" + QuizSQLiteHelper.RULES_COLUMN_CARDTYPES + "\' " +
                    "\'1\' AS \'" + QuizSQLiteHelper.RULES_COLUMN_DECK_ID + "\' " +
                    "\'" + Constants.LAST_RULESET_USED + "\' AS \'" + QuizSQLiteHelper.RULES_COLUMN_RULESET_NAME + "\' " +
                    "\'null\' AS \'" + QuizSQLiteHelper.RULES_COLUMN_DOUBLE_EDGE_SWORD + "\' " +
                    "\'null\' AS \'" + QuizSQLiteHelper.RULES_COLUMN_LAST_CARD_WAGER + "\' " +
                    "\'null\' AS \'" + QuizSQLiteHelper.RULES_COLUMN_MULTIPLE_WINNERS + "\' " +
                " UNION ALL SELECT \'2\', " +
                    "\'60000\', " +
                    "\'600000\', " +
                    "\'15\', " +
                    "\'" + cardTypes + "\', " +
                    "'1' \', " +
                    "\'" + Constants.DOUBLE_DOWN_RULESET + "\', " +
                    "\'" + true + "\', " +
                    "\'" + true + "\', " +
                    "\'" + true + "\';";
        db.execSQL(query);
    }
}
