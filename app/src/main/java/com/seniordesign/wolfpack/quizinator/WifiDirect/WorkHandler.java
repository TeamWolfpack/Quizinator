package com.seniordesign.wolfpack.quizinator.WifiDirect;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

/**
 * Creates a standalone thread other than the main thread.
 *
 * @creation 10/26/2016
 */
public final class WorkHandler {

    private HandlerThread mHandlerThread;
    private Handler mHandler;

    /*
     * @author kuczynskij (10/26/2016)
     */
    public WorkHandler(String threadName) {
        //mHandlerThread = new HandlerThread(threadName, Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread = new HandlerThread(threadName,
                Process.THREAD_PRIORITY_DEFAULT);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    public Handler getHandler() {
        return mHandler;
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    public Looper getLooper() {
        Handler h = mHandler;
        return h != null ? h.getLooper() : null;
    }

    /**
     * Close the WorkHandler instance. Should be called when the
     * WorkHandler is no longer needed.
     */
    /*
     * @author kuczynskij (10/26/2016)
     */
    public void close() {
        if (mHandler != null) {
            mHandlerThread.getLooper().quit();
            mHandlerThread.quit();
            mHandler = null;
        }
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    @Override
    protected void finalize() {
        //cleanup if needed and log the fact the WorkHandler
        //instance was not properly closed.
        if (mHandler != null) {
            close();
        }
    }
}
