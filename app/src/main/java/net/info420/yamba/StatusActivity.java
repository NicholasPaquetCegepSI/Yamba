package net.info420.yamba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import social.bigbone.MastodonClient;
import social.bigbone.MastodonRequest;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

public class StatusActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "StatusActivity";
    EditText editStatus;
    Button buttonUpdate;
    Intent intentUpdaterService;
    Intent intentPrefsActivity;
    Intent intentTimelineActivity;
    MastodonRequest<Status> request;
    Handler handler;
    final String[] message = new String[1];
    Toolbar myToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_layout);

        // Composantes
        editStatus = findViewById(R.id.editStatus);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        myToolBar = findViewById(R.id.toolbar);

        // Ajout de myToolBar
        myToolBar.setTitle("");
        setSupportActionBar(myToolBar);
        myToolBar.setTitle(R.string.titleStatusUpdate);
        myToolBar.setTitleTextColor(Color.WHITE);

        intentUpdaterService = new Intent(this, UpdaterService.class);
        intentPrefsActivity = new Intent(this, PrefsActivity.class);
        intentTimelineActivity = new Intent(this, TimelineActivity.class);

        // Listeners
        buttonUpdate.setOnClickListener(this);

        handler = new Handler();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        String status;

        switch (view.getId()) {
            case R.id.buttonUpdate:
                status = editStatus.getText().toString();

                new Thread(() -> {
                    try {
                        request = ((YambaApplication) getApplication()).getClient().statuses().postStatus(status);
                        request.execute();

                        Log.d(TAG, "onClick(): " + getString(R.string.tootIsSent) + " : " + status);
                        message[0] = getString(R.string.tootIsSent);
                    } catch (BigBoneRequestException e) {
                        Log.d(TAG, "onClick(): " + getString(R.string.bigBoneException) + " : " + status);
                        message[0] = getString(R.string.bigBoneException);
                    }
                    handler.post(() -> Toast.makeText(StatusActivity.this, message[0], Toast.LENGTH_LONG).show());
                }).start();
                break;
        }
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
        else if (itemId == R.id.itemTimeline)
            startActivity(intentTimelineActivity);
        return true;
    }
}