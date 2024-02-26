package net.info420.yamba;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";
    Toolbar myToolBar;
    ListView outputTimeline;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_layout);

        // Ajout de myToolBar
        myToolBar = findViewById(R.id.toolbar);
        myToolBar.setTitle("");
        setSupportActionBar(myToolBar);
        myToolBar.setTitle(R.string.titleTimeline);
        myToolBar.setTitleTextColor(Color.WHITE);

        outputTimeline = findViewById(R.id.outputTimeline);

        cursor = ((YambaApplication) getApplication()).getStatusData().query();
        Log.d(TAG, "onCreate*( : Le Cursor pointe a " + cursor.getCount() + " enregistrements");
    }
}