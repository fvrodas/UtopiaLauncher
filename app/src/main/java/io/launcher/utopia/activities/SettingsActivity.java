package io.launcher.utopia.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import io.launcher.utopia.R;
import io.launcher.utopia.UtopiaLauncher;

public class SettingsActivity extends AppCompatActivity {
    private UtopiaLauncher app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        app = (UtopiaLauncher) getApplication();

        Toolbar tbSettings = findViewById(R.id.tbSettings);
        setSupportActionBar(tbSettings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nvSettingsContainer = findViewById(R.id.nvSettingsContainer);

        nvSettingsContainer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_wallpaper : {
                        Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                        startActivity(intent);
                        return true;
                    }

                    case R.id.action_columns : {

                    }

                }
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
