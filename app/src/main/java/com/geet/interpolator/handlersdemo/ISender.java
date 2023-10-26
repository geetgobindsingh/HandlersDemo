package com.geet.interpolator.handlersdemo;

import android.os.Handler;

/**
 * Created by geetgobindsingh on 13/11/17.
 */

public interface ISender {
    void startSenderThread(Handler UIhandler, IReceiver receiver);
    void stopSenderThread();

    void startConversation();
}
