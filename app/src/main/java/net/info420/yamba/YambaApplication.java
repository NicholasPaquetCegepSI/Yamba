package net.info420.yamba;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import social.bigbone.MastodonClient;

public class YambaApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "YambaApplication";
    private MastodonClient client;
    SharedPreferences prefs;
    private String delay = null;
    private String numberOfToots = null;
    private StatusData statusData;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate()");

        new Thread(() -> {
            client = new MastodonClient.Builder(getString(R.string.instanceHostname)).accessToken(
                    getString(R.string.accessToken)).build();
        }).start();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        //        delay = prefs.getString("delay", "60");
        //        numberOfToots = prefs.getString("numberOfToots", "5");
        statusData = new StatusData(this);
    }


    public MastodonClient getClient() {
        return client;
    }

    public String getDelay() {
        if (delay == null) {
            delay = prefs.getString("delay", "60");
        }
        return delay;
    }

    public String getNumberOfToots() {
        if (numberOfToots == null) {
            numberOfToots = prefs.getString("numberOfToots", "5");
        }
        return numberOfToots;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        // Forcera la récupération de delay et/ou numberOfToots dans getDelay() et getNumberOfToots, avant le retour de
        // leur valeur.
        delay = null;
        numberOfToots = null;

        // Confirmation de la préférence modifiée
        Log.d(TAG, "onSharedPreferenceChanged(): " + key);
    }

    public StatusData getStatusData() {
        return statusData;
    }
}
