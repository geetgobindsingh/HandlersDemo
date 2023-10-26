package com.geet.interpolator.handlersdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Handler.Callback {

    public static final int MSG_FROM_SENDER = 0;
    public static final int MSG_FROM_RECEIVER = 1;

    private Handler mUIHandler;
    private TextView mTextView;
    private LinearLayout mRootLayout;
    private ISender mSender;
    private IReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootLayout = (LinearLayout) findViewById(R.id.root_layout);

        mTextView = new TextView(this);
        mTextView.setText("Welcome");
        mRootLayout.addView(mTextView);
        mTextView = new TextView(this);
        mTextView.setText("----------------------------------------");
        mRootLayout.addView(mTextView);

        // init handler
        mUIHandler = new Handler(this);

        // init Sender & Receiver
        mReceiver = ReceiverHandlerRunnable.getInstance();
        mSender = SenderHandlerThread.getInstance();

        // start Receiver Engine
        mReceiver.startReceiverThread(mUIHandler);

        // start Sender Engine
        mSender.startSenderThread(mUIHandler, mReceiver);
    }

    @Override
    public boolean handleMessage(final Message message) {
        switch (message.what) {
            case MSG_FROM_SENDER: {
                String messageData = message.getData().getString("sender_data");
                addText(messageData, true);
                break;
            }
            case MSG_FROM_RECEIVER: {
                String messageData = message.getData().getString("receiver_data");
                addText(messageData, false);
                break;
            }
        }
        return false;
    }

    private void addText(String text, boolean isSender) {
        mTextView = new TextView(MainActivity.this);
        if (isSender) {
            mTextView.setText("Sender : '" + text + "'");
            setColor(mTextView, mTextView.getText().toString(), "Sender", Color.RED);
        } else {
            mTextView.setText("Receiver : '" + text + "'");
            setColor(mTextView, mTextView.getText().toString(), "Receiver", Color.MAGENTA);
        }
        mRootLayout.addView(mTextView);
    }

    private void setColor(TextView view, String fulltext, String subtext, int color) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fulltext.indexOf(subtext);
        str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
