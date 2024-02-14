package net.info420.yamba;

import android.app.Application;
import android.util.Log;

import social.bigbone.MastodonClient;

public class YambaApplication extends Application {
    private static final String TAG = "YambaApplication";
    private MastodonClient client;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate()");

        new Thread(() -> {
            client = new MastodonClient.Builder(getString(R.string.instanceHostname)).accessToken(
                    getString(R.string.accessToken)).build();
        }).start();
    }


    public MastodonClient getClient() {
        return client;
    }
}
