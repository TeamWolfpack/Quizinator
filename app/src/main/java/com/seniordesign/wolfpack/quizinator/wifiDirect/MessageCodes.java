package com.seniordesign.wolfpack.quizinator.wifiDirect;

public final class MessageCodes {

    static final String PACKAGE_NAME =
            MessageCodes.class.getPackage().getName();

    static final int MSG_NULL = 0;
    static final int MSG_STARTSERVER = 1001;
    static final int MSG_STARTCLIENT = 1002;
    static final int MSG_PUSHOUT_DATA = 1005;
    static final int MSG_NEW_CLIENT = 1006;
    static final int MSG_FINISH_CONNECT = 1007;
    static final int MSG_PULLIN_DATA = 1008;
    static final int MSG_REGISTER_ACTIVITY = 1009;

    static final int MSG_SELECT_ERROR = 2001;
    static final int MSG_BROKEN_CONN = 2002;

    //gameplay messages
    public static final int MSG_PLAYER_READY_ACTIVITY = 3000;
    public static final int MSG_SEND_RULES_ACTIVITY = 3001;
    public static final int MSG_SEND_CARD_ACTIVITY = 3002;
    public static final int MSG_SEND_ANSWER_ACTIVITY = 3003;
    public static final int MSG_ANSWER_CONFIRMATION_ACTIVITY = 3004;
    public static final int MSG_END_OF_GAME_ACTIVITY = 3005;
    public static final int MSG_SEND_WAGER_ACTIVITY = 3006;
    public static final int MSG_SEND_WAGER_CONFIRMATION_ACTIVITY = 3007;

    //update peerlist
    public static final int MSG_DISCONNECT_FROM_ALL_PEERS = 4000;
}
