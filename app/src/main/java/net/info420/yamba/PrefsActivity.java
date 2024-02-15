package net.info420.yamba;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;


public class PrefsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs_layout);

        // Ajout de myToolBar
        Toolbar myToolBar = findViewById(R.id.toolbar);
        myToolBar.setTitle("");
        setSupportActionBar(myToolBar);
        myToolBar.setTitle(R.string.titlePrefs);
        myToolBar.setTitleTextColor(Color.WHITE);

        // Ajout du Fragment MyPreferencesFragment dans le layout de prefsActivity (prefs_layout)
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new MyPreferencesFragment())
                .commit();
    }

    // Fragment contenant les préférences

}