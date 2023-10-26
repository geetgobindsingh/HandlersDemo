package com.geet.interpolator.handlersdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Messenger;
import android.os.RemoteException;

import static android.R.attr.data;
import static com.geet.interpolator.handlersdemo.GlobalConstants.MSG_BEGIN_CONVERSATION;
import static com.geet.interpolator.handlersdemo.GlobalConstants.MSG_END_CONVERSATION;
import static com.geet.interpolator.handlersdemo.MainActivity.MSG_FROM_SENDER;
import static com.geet.interpolator.handlersdemo.ReceiverHandlerRunnable.MSG_RECEIVER_MESSAGE;

/**
 * Created by geetgobindsingh on 13/11/17.
 */

public class SenderHandlerThread extends HandlerThread implements ISender, MessageQueue.IdleHandler {

    public static final int MSG_SENDER_MESSAGE = 3;

    private SenderHandler mSenderHandler;
    private Messenger mSenderMessenger;

    private Handler mUIHandler;
    private IReceiver mReceiver;
    private boolean mIsFirstIdle = true;

    public SenderHandlerThread(String name) {
        super(name);
    }

    public static SenderHandlerThread getInstance() {
        return new SenderHandlerThread(SenderHandlerThread.class.getSimpleName());
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mSenderHandler = new SenderHandler(getLooper());
        mSenderMessenger = new Messenger(mSenderHandler);
        startConversation();
        Looper.myQueue().addIdleHandler(this);
    }

    @Override
    public void startSenderThread(Handler UIhandler, IReceiver receiver) {
        this.mUIHandler = UIhandler;
        this.mReceiver = receiver;
        this.start();
    }

    @Override
    public void stopSenderThread() {
        getLooper().quitSafely();
    }

    @Override
    public void startConversation() {
        Message startMessage = mSenderHandler.obtainMessage(MSG_BEGIN_CONVERSATION);
        Bundle startBundle = new Bundle();
        startBundle.putString("sender_data", "Hi I am Sender");
        startMessage.setData(startBundle);
        startMessage.replyTo = mSenderMessenger;
        mSenderHandler.sendMessage(startMessage);
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
        bundleResponse.putString("sender_data", "Hey Receiver NOW I am waiting For Your Reply");

        Message messageToUI = mUIHandler.obtainMessage(MSG_FROM_SENDER);
        messageToUI.setData(bundleResponse);

        // show in UI thread
        mUIHandler.sendMessage(messageToUI);
        return false;
    }


    private class SenderHandler extends LeakGuardHandlerWrapper {

        public SenderHandler(Looper looper) {
            super(looper);
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


                    Message messageToUI = mUIHandler.obtainMessage(MSG_FROM_SENDER);
                    messageToUI.setData(message.getData());

                    // show in UI thread
                    mUIHandler.sendMessage(messageToUI);

                    try {
                        mReceiver.getMessenger().send(Message.obtain(message));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                }
                case MSG_SENDER_MESSAGE: {
                    try {
                        // Incoming data
                        String data = message.getData().getString("receiver_data");

                        try {
                            Thread.sleep(6000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Message responseMessage = Message.obtain(null, MSG_RECEIVER_MESSAGE);
                        Bundle bundleResponse = new Bundle();
                        bundleResponse.putString("sender_data", "Why do cows wear bells ?");
                        responseMessage.setData(bundleResponse);
                        responseMessage.replyTo = mSenderMessenger;

                        Message messageToUI = mUIHandler.obtainMessage(MSG_FROM_SENDER);
                        messageToUI.setData(responseMessage.getData());

                        // show in UI thread
                        mUIHandler.sendMessage(messageToUI);


                        message.replyTo.send(responseMessage);
                    }
                    catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case MSG_END_CONVERSATION: {
                    break;
                }
            }
        }
    }
}
