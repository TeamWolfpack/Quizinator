package com.seniordesign.wolfpack.quizinator.GameplayHandler;

import com.seniordesign.wolfpack.quizinator.Activities.GamePlayActivity;

/**
 * Created by farrowc on 11/29/2016.
 */

public interface GamePlayHandler {

    /*
     * @author farrowc (11/30/2016)
     */
    public boolean handleInitialization(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    /*
     * @author farrowc (11/30/2016)
     */
    public long handleAnswerClicked(GamePlayActivity gamePlayActivity, GamePlayProperties properties, String answer);

    /*
     * @author farrowc (11/30/2016)
     */
    public boolean initializeDB(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    /*
     * @author farrowc (11/30/2016)
     */
    public long handleNextCard(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    /*
     * @author farrowc (11/30/2016)
     */
    public boolean handleCleanup(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    /*
     * @author farrowc (11/30/2016)
     */
    public boolean handleResume(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    /*
     * @author farrowc (11/30/2016)
     */
    public boolean handlePause(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    /*
     * @author leonardj (12/6/2016)
     */
    public boolean handleDestroy(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    /*
     * @author farrowc (11/30/2016)
     */
    public boolean handleInitializeGameplay(GamePlayActivity gamePlayActivity, GamePlayProperties properties);

    /*
     * @author farrowc (11/30/2016)
     */
    public void onFragmentInteraction(GamePlayActivity gamePlayActivity, GamePlayProperties properties,String choice);

}
