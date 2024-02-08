package net.info420.yamba;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

import social.bigbone.MastodonClient;
import social.bigbone.MastodonRequest;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

public class StatusActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "StatusActivity";
    EditText editStatus;
    Button buttonUpdate;
    MastodonClient client;
    MastodonRequest<Status> request;
    Handler handler;
    final String[] message = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_layout);

        // Composantes
        editStatus = findViewById(R.id.editStatus);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(this);

        handler = new Handler();
    }

    @Override
    public void onClick(View view) {
        String status;

        status = editStatus.getText().toString();

        new Thread(() -> {
            try {
                client = new MastodonClient.Builder(getString(R.string.instanceHostname)).accessToken(getString(R.string.accessToken)).build();
                request = client.statuses().postStatus(status);
                request.execute();

                Log.d(TAG, "onClick(): " + getString(R.string.tootIsSent) + " : " + status);
                message[0] = getString(R.string.tootIsSent);
            } catch (BigBoneRequestException e) {
                Log.d(TAG, "onClick(): " + getString(R.string.bigBoneException) + " : " + status);
                message[0] = getString(R.string.bigBoneException);
            }
            handler.post(() -> Toast.makeText(StatusActivity.this, message[0], Toast.LENGTH_LONG).show());
        }).start();
    }
}