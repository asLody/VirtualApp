package com.lody.virtual.client.env;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * @author Lody
 */

public class DeadServerException extends RuntimeException {

    public DeadServerException() {
    }

    public DeadServerException(String message) {
        super(message);
    }

    public DeadServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeadServerException(Throwable cause) {
        super(cause);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public DeadServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
