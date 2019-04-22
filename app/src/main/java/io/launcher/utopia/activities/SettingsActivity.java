package io.launcher.utopia.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import io.launcher.utopia.R;
import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.dialogs.NumberPickerDialog;

import static io.launcher.utopia.UtopiaLauncher.COLUMNS_SETTINGS;

public class SettingsActivity extends AppCompatActivity {
    public static final int REQUEST_SETTINGS = 111;
    private UtopiaLauncher app;
    private Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        app = (UtopiaLauncher) getApplication();

        Toolbar tbSettings = findViewById(R.id.tbSettings);
        setSupportActionBar(tbSettings);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
                        int columns = app.launcherSettings.getInt(COLUMNS_SETTINGS, 4);
                        NumberPickerDialog dlg = new NumberPickerDialog(
                                SettingsActivity.this, columns) {
                            @Override
                            public void onOKPressed(int i) {
                                SharedPreferences.Editor editor = app.launcherSettings.edit();
                                editor.putInt(COLUMNS_SETTINGS, i);
                                editor.apply();
                                intent = new Intent();
                                intent.putExtra(COLUMNS_SETTINGS, i);
                            }
                        };
                        dlg.show();
                        return true;
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

    @Override
    public void onBackPressed() {
        if (intent != null) {
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
