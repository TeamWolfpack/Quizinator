package com.seniordesign.wolfpack.quizinator.WifiDirect;

public final class Constants {

    public static final String PACKAGE_NAME = Constants.class.getPackage().getName();

    public static final int MSG_NULL = 0;
    public static final int MSG_STARTSERVER = 1001;
    public static final int MSG_STARTCLIENT = 1002;
    public static final int MSG_CONNECT = 1003;
    public static final int MSG_DISCONNECT = 1004;   // p2p disconnect
    public static final int MSG_PUSHOUT_DATA = 1005;
    public static final int MSG_NEW_CLIENT = 1006;
    public static final int MSG_FINISH_CONNECT = 1007;
    public static final int MSG_PULLIN_DATA = 1008;
    public static final int MSG_REGISTER_ACTIVITY = 1009;

    public static final int MSG_SELECT_ERROR = 2001;
    public static final int MSG_BROKEN_CONN = 2002;  // network disconnect

    public static final int MSG_SIZE = 50;    // the lastest 50 messages
    public static final String MSG_SENDER = "sender";
    public static final String MSG_TIME = "time";
    public static final String MSG_CONTENT = "msg";

    //gameplay messages
    public static final int MSG_START_GAMEPLAY_ACTIVITY = 3000;
    public static final int MSG_SEND_RULES_ACTIVITY = 3001;
    public static final int MSG_SEND_CARD_ACTIVITY = 3002;
}
