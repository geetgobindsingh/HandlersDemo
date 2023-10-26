package com.geet.interpolator.handlersdemo;

import android.os.Handler;
import android.os.Messenger;

/**
 * Created by geetgobindsingh on 13/11/17.
 */

public interface IReceiver {
    void startReceiverThread(Handler UIhandler);
    void stopReceiverThread();

    Messenger getMessenger();
}
