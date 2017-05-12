package com.seniordesign.wolfpack.quizinator.wifiDirect;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

final class WorkHandler {

    private HandlerThread mHandlerThread;
    private Handler mHandler;

    WorkHandler(String threadName) {
        mHandlerThread = new HandlerThread(threadName,
                Process.THREAD_PRIORITY_DEFAULT);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    Looper getLooper() {
        return mHandler != null ? mHandler.getLooper() : null;
    }

    @Override
    protected void finalize() {
        if (mHandler != null) {
            mHandlerThread.getLooper().quit();
            mHandlerThread.quit();
            mHandler = null;
        }
    }
}
