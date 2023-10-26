package com.geet.interpolator.handlersdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Messenger;
import android.os.RemoteException;

import static com.geet.interpolator.handlersdemo.GlobalConstants.MSG_BEGIN_CONVERSATION;
import static com.geet.interpolator.handlersdemo.GlobalConstants.MSG_END_CONVERSATION;
import static com.geet.interpolator.handlersdemo.MainActivity.MSG_FROM_RECEIVER;
import static com.geet.interpolator.handlersdemo.MainActivity.MSG_FROM_SENDER;
import static com.geet.interpolator.handlersdemo.SenderHandlerThread.MSG_SENDER_MESSAGE;

/**
 * Created by geetgobindsingh on 13/11/17.
 */

public class ReceiverHandlerRunnable implements Runnable, IReceiver, MessageQueue.IdleHandler {

    public static final int MSG_RECEIVER_MESSAGE = 3;
    private ReceiverHandler mReceiverHandler;
    private Messenger mReceiverMessenger;
    private Thread mReceiverThread;
    private Looper mReceiverLooper;
    private Handler mUIHandler;
    private boolean mIsFirstIdle = true;

    private ReceiverHandlerRunnable() {

    }

    public static ReceiverHandlerRunnable getInstance() {
        return new ReceiverHandlerRunnable();
    }

    @Override
    public void run() {
        Looper.prepare();
        mReceiverHandler = new ReceiverHandler(mUIHandler);
        mReceiverMessenger = new Messenger(mReceiverHandler);
        mReceiverHandler.setMessenger(mReceiverMessenger);
        mReceiverLooper = Looper.myLooper();
        Looper.myQueue().addIdleHandler(this);
        Looper.loop();
    }



    @Override
    public void startReceiverThread(Handler UIhandler) {
        mReceiverThread = new Thread(this);
        // this is init before start
        mUIHandler = UIhandler;
        mReceiverThread.start();
    }

    @Override
    public void stopReceiverThread() {
        mReceiverLooper.quitSafely();
    }

    @Override
    public Messenger getMessenger() {
        return mReceiverMessenger;
    }

    @Override
    public boolean queueIdle() {

        if (mIsFirstIdle) {
            mIsFirstIdle = false;
            return true;
        }

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bundle bundleResponse = new Bundle();
        bundleResponse.putString("receiver_data", "Hey Sender I am waiting For Your Reply");

        Message messageToUI = mUIHandler.obtainMessage(MSG_FROM_RECEIVER);
        messageToUI.setData(bundleResponse);

        // show in UI thread
        mUIHandler.sendMessage(messageToUI);
        return false;
    }

    private static class ReceiverHandler extends Handler {

        private Handler mUIHandler;
        private Messenger mMessenger;

        public ReceiverHandler(Handler UIHandler) {
            this.mUIHandler = UIHandler;
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_BEGIN_CONVERSATION: {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message responseMessage = Message.obtain(null, MSG_SENDER_MESSAGE);
                    Bundle bundleResponse = new Bundle();
                    bundleResponse.putString("receiver_data", "Hi I am Receiver");
                    responseMessage.setData(bundleResponse);
                    responseMessage.replyTo = mMessenger;

                    Message messageToUI = mUIHandler.obtainMessage(MSG_FROM_RECEIVER);
                    messageToUI.setData(responseMessage.getData());

                    // show in UI thread
                    mUIHandler.sendMessage(messageToUI);

                    try {
                        message.replyTo.send(responseMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                }
                case MSG_RECEIVER_MESSAGE: {
                    try {
                        // Incoming data
                        String data = message.getData().getString("sender_data");

                        try {
                            Thread.sleep(6000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Message responseMessage = Message.obtain(null, MSG_SENDER_MESSAGE);
                        Bundle bundleResponse = new Bundle();
                        bundleResponse.putString("receiver_data", "Because their horns don't work :P");
                        responseMessage.setData(bundleResponse);
                        responseMessage.replyTo = mMessenger;

                        Message messageToUI = mUIHandler.obtainMessage(MSG_FROM_RECEIVER);
                        messageToUI.setData(responseMessage.getData());

                        // show in UI thread
                        mUIHandler.sendMessage(messageToUI);

                        // this below line creates deadlock
                        //message.replyTo.send(responseMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case MSG_END_CONVERSATION: {
                    break;
                }
            }
        }

        public void setMessenger(Messenger mReceiverMessenger) {
            this.mMessenger = mReceiverMessenger;
        }
    }
}
