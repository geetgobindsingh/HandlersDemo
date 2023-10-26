package com.geet.interpolator.handlersdemo;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Created by geetgobindsingh on 13/11/17.
 */

public class LeakGuardHandlerWrapper<T> extends Handler {
    private final WeakReference<T> mOwnerInstanceRef;

    public LeakGuardHandlerWrapper(@NonNull final T ownerInstance) {
        this(ownerInstance, Looper.myLooper());
    }

    public LeakGuardHandlerWrapper(@NonNull final T ownerInstance, final Looper looper) {
        super(looper);
        if (ownerInstance == null) {
            throw new NullPointerException("ownerInstance is null");
        }
        mOwnerInstanceRef = new WeakReference<>(ownerInstance);
    }

    @Nullable
    public T getOwnerInstance() {
        return mOwnerInstanceRef.get();
    }
}