package com.lody.virtual.client.hook.delegate;


import android.content.Intent;

import android.app.Activity;

public interface ComponentDelegate {

    ComponentDelegate EMPTY = new ComponentDelegate() {

        @Override
        public void beforeActivityCreate(Activity activity) {
            // Empty
        }

        @Override
        public void beforeActivityResume(Activity activity) {
            // Empty
        }

        @Override
        public void beforeActivityPause(Activity activity) {
            // Empty
        }

        @Override
        public void beforeActivityDestroy(Activity activity) {
            // Empty
        }

        @Override
        public void onSendBroadcast(Intent intent) {
            // Empty
        }
    };

    void beforeActivityCreate(Activity activity);

    void beforeActivityResume(Activity activity);

    void beforeActivityPause(Activity activity);

    void beforeActivityDestroy(Activity activity);

    void onSendBroadcast(Intent intent);
}
