package net.info420.yamba;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";
    Toolbar myToolBar;
    ListView outputTimeline;
    Cursor cursor;
    SimpleCursorAdapter adapter;
    MyViewBinder myViewBinder;

    // Tableau contenant les noms des champs utilises
    static final String[] from = {StatusData.C_USER, StatusData.C_CREATED_AT, StatusData.C_TEXT};
    // Tableau contenant l'id des views
    static final int[] to = {R.id.c_user, R.id.c_createdAt, R.id.c_text};
    Intent intentUpdaterService;
    Intent intentPrefsActivity;
    Intent intentStatusActivity;
    TimelineReceiver timelineReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_layout);

        intentUpdaterService = new Intent(this, UpdaterService.class);
        intentPrefsActivity = new Intent(this, PrefsActivity.class);
        intentStatusActivity = new Intent(this, StatusActivity.class);

        // Ajout de myToolBar
        myToolBar = findViewById(R.id.toolbar);
        myToolBar.setTitle("");
        setSupportActionBar(myToolBar);
        myToolBar.setTitle(R.string.titleTimeline);
        myToolBar.setTitleTextColor(Color.WHITE);

        outputTimeline = findViewById(R.id.outputTimeline);

        cursor = ((YambaApplication) getApplication()).getStatusData().query();
        Log.d(TAG, "onCreate*( : Le Cursor pointe a " + cursor.getCount() + " enregistrements");

        adapter = new SimpleCursorAdapter(this, R.layout.timeline_row_layout, cursor, from, to, 0);

        myViewBinder = new MyViewBinder();
        adapter.setViewBinder(myViewBinder);

        outputTimeline.setAdapter(adapter);

        timelineReceiver = new TimelineReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(timelineReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(
                timelineReceiver, new IntentFilter("net.info420.yamba.action.BD_CHANGED"), RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(intentUpdaterService);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.itemStartUpdaterService)
            startService(intentUpdaterService);
        else if (itemId == R.id.itemStopUpdaterService)
            stopService(intentUpdaterService);
        else if (itemId == R.id.itemPrefs)
            startActivity(intentPrefsActivity);
        else if (itemId == R.id.itemStartStatusActivity)
            startActivity(intentStatusActivity);
        return true;
    }

    private class MyViewBinder implements SimpleCursorAdapter.ViewBinder {
        long timestamp;
        CharSequence relativeTime;

        @Override
        public boolean setViewValue(View view, Cursor cursor, int fieldIndex) {
            if (cursor.getColumnIndex(StatusData.C_CREATED_AT) == fieldIndex) {
                timestamp = cursor.getLong(fieldIndex);
                relativeTime = DateUtils.getRelativeTimeSpanString(timestamp);
                ((TextView) view).setText(relativeTime);
                return true;
            } else {
                return false;
            }
        }
    }

    private class TimelineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceived() : Broadcast BD_CHANGED reçu");

            cursor = ((YambaApplication) getApplication()).getStatusData().query();

            adapter.changeCursor(cursor);
            Log.d(TAG, "onReceived() : Mise a jour du Fil Mastodon effectuee");

            Toast.makeText(context, "Vous avez recu " + intent.getIntExtra("newTootsCount", 0) + " nouveau(x) toot(s)",
                           Toast.LENGTH_LONG
            ).show();
        }
    }

}