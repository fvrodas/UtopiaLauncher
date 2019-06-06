package io.launcher.utopia.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import io.launcher.utopia.BuildConfig;
import io.launcher.utopia.R;
import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.presenters.SettingsPresenter;
import io.launcher.utopia.ui.dialogs.NumberPickerDialog;
import io.launcher.utopia.utils.Tools;
import io.launcher.utopia.views.SettingsView;

import static io.launcher.utopia.UtopiaLauncher.COLUMNS_SETTINGS;
import static io.launcher.utopia.UtopiaLauncher.GRAVITY_SETTINGS;

public class SettingsActivity extends AppCompatActivity implements SettingsView {
    public static final int REQUEST_SETTINGS = 111;
    private UtopiaLauncher app;
    private Intent intent = null;
    private SettingsPresenter mPresenter;
    private NavigationView nvSettingsContainer;
    private Integer columns = null;
    private Integer gravity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        app = (UtopiaLauncher) getApplication();

        mPresenter = new SettingsPresenter(app.launcherSettings);
        mPresenter.attachView(this);

        Toolbar tbSettings = findViewById(R.id.tbSettings);
        setSupportActionBar(tbSettings);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        nvSettingsContainer = findViewById(R.id.nvSettingsContainer);

        mPresenter.readIntFromSettings(COLUMNS_SETTINGS, 4);
        mPresenter.readIntFromSettings(GRAVITY_SETTINGS, GravityCompat.END);
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

    @Override
    public void onIntReadFromSettings(String key, int value) {
        switch (key) {
            case COLUMNS_SETTINGS: {
                columns = value;
                break;
            }
            case GRAVITY_SETTINGS: {
                gravity = value;
                break;
            }
        }
        if (columns != null && gravity != null) {
            intent = new Intent();
            nvSettingsContainer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_wallpaper: {
                            Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                            startActivity(intent);
                            return true;
                        }

                        case R.id.action_columns: {
                            NumberPickerDialog dlg = new NumberPickerDialog(
                                    SettingsActivity.this, columns) {
                                @Override
                                public void onOKPressed(int i) {
                                    intent.putExtra(COLUMNS_SETTINGS, mPresenter.writeIntIntoSettings(COLUMNS_SETTINGS, i));
                                }
                            };
                            dlg.show();
                            return true;
                        }

                        case R.id.action_about: {
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle(R.string.app_name)
                                    .setMessage(String.format(getString(R.string.about_text), BuildConfig.VERSION_NAME))
                                    .setNegativeButton(R.string.about_negative, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNeutralButton(R.string.about_neutral, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_github_url)));
                                            startActivity(browserIntent);
                                        }
                                    })
                                    .show();
                            return true;
                        }
                        case R.id.action_dock_gravity: {
                            String[] items = getResources().getStringArray(R.array.dock_gravity);

                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle(R.string.menu_dock_gravity)
                                    .setSingleChoiceItems(items, gravity == GravityCompat.START? 0: 1, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                intent.putExtra(GRAVITY_SETTINGS,
                                                        mPresenter.writeIntIntoSettings(GRAVITY_SETTINGS, GravityCompat.START));
                                            } else {
                                                intent.putExtra(GRAVITY_SETTINGS,
                                                        mPresenter.writeIntIntoSettings(GRAVITY_SETTINGS, GravityCompat.END));
                                            }
                                        }
                                    })
                                    .setPositiveButton(R.string.btOK, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                            return true;
                        }

                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void showMessage(String text) {
        Tools.showSnackbar(this, text);
    }

    @Override
    public Context provideContext() {
        return app.getApplicationContext();
    }

    @Override
    public void showProgress(Boolean show) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}
