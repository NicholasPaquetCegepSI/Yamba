package net.info420.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceived extends BroadcastReceiver {
    private static final String TAG = "BootReceived";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()");
        context.startForegroundService(new Intent(context, MyMusicService.class));
    }
}