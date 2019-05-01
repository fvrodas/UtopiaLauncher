package io.launcher.utopia.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import io.launcher.utopia.BuildConfig;
import io.launcher.utopia.R;
import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.adapters.ResolveInfoAdapter;
import io.launcher.utopia.adapters.ResolveInfoDockAdapter;
import io.launcher.utopia.services.UtopiaService;
import io.launcher.utopia.utils.ActivityInfo;
import io.launcher.utopia.utils.IntentObservable;
import io.launcher.utopia.utils.SerializeHelper;
import io.launcher.utopia.utils.SimpleItemTouchHelperCallback;
import io.launcher.utopia.utils.SpaceItemDecoration;
import io.launcher.utopia.utils.Tools;

import static io.launcher.utopia.UtopiaLauncher.COLUMNS_SETTINGS;
import static io.launcher.utopia.activities.SettingsActivity.REQUEST_SETTINGS;

public class AppsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, Observer {
    private static final int REQUEST_UNINSTALL = 7686;
    private final DisplayMetrics metrics = new DisplayMetrics();
    private PackageManager mPkgManager = null;
    private UtopiaLauncher app = null;
    private ResolveInfoAdapter adapter = null;
    private ResolveInfoDockAdapter dockAdapter = null;
    private RecyclerView rvAppList;
    private DrawerLayout mDrawerLayout;
    private ProgressBar progressBar;
    private SerializeHelper<ArrayList<ActivityInfo>> helper = new SerializeHelper<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        app = (UtopiaLauncher) getApplication();
        mPkgManager = getPackageManager();
        progressBar = findViewById(R.id.pbLoading);
        rvAppList = findViewById(R.id.rvAppList);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        app.observable.addObserver(this);

        int columns = app.launcherSettings.getInt(COLUMNS_SETTINGS, 4);

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
        rvAppList.setLayoutManager(layoutManager);

        SearchView svSearch = findViewById(R.id.svSearch);

        svSearch.setOnQueryTextListener(this);

        adapter = new ResolveInfoAdapter(new ArrayList<ActivityInfo>()) {
            @Override
            public void onAppPressed(ActivityInfo app) {
                Intent toStart = mPkgManager.getLaunchIntentForPackage(app.getPackageName());
                AppsActivity.this.startActivity(toStart);
            }
        };
        SpaceItemDecoration decoration = new SpaceItemDecoration(16);
        rvAppList.addItemDecoration(decoration);
        rvAppList.setAdapter(adapter);
        rvAppList.setItemViewCacheSize(100);

        AppCompatImageView imgButtonSettings = findViewById(R.id.ivSettings);
        imgButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
                Intent intent = new Intent(AppsActivity.this, SettingsActivity.class);
                startActivityForResult(intent, REQUEST_SETTINGS);
            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
                float moveFactor = (dp * slideOffset);
                (findViewById(R.id.rootView)).setTranslationX(-moveFactor);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        refreshApplicationsList();

        //region Dock Initialisation
        RecyclerView navigationView = findViewById(R.id.dock);
        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        llm.setStackFromEnd(true);
        navigationView.setLayoutManager(llm);

        dockAdapter = new ResolveInfoDockAdapter(new ArrayList<ActivityInfo>()) {
            @Override
            protected void onAppPressed(ActivityInfo app) {
                mDrawerLayout.closeDrawers();
                Intent toStart = mPkgManager.getLaunchIntentForPackage(app.getPackageName());
                AppsActivity.this.startActivity(toStart);
            }

            @Override
            protected void onAppLongPressed(final ActivityInfo app) {

            }

            @Override
            protected void onItemRemoved(ArrayList<ActivityInfo> items) {
                SharedPreferences.Editor editor = app.launcherSettings.edit();
                editor.putString(UtopiaLauncher.DOCK, helper.serialize(items));
                editor.apply();
            }

            @Override
            protected void onItemSwapped(ArrayList<ActivityInfo> items) {
                SharedPreferences.Editor editor = app.launcherSettings.edit();
                editor.putString(UtopiaLauncher.DOCK, helper.serialize(items));
                editor.apply();
            }
        };
        navigationView.setAdapter(dockAdapter);
        navigationView.addItemDecoration(new SpaceItemDecoration(8));

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(dockAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(navigationView);
        //endregion

        registerForContextMenu(rvAppList);
    }


    private void refreshApplicationsList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);

                final ArrayList<ActivityInfo> apps = new ArrayList<>();

                for (ResolveInfo item : mPkgManager.queryIntentActivities(intent, 0)) {
                    apps.add(new ActivityInfo(item.activityInfo.packageName, item.loadLabel(getPackageManager()).toString()));
                    if (UtopiaLauncher.iconsCache.get(item.activityInfo.packageName) == null) {
                        UtopiaLauncher.iconsCache.put(
                                item.activityInfo.packageName,
                                Tools.createIcon(AppsActivity.this,
                                        Tools.getBitmapFromDrawable(
                                                item.loadIcon(mPkgManager),
                                                Bitmap.Config.ARGB_8888
                                        )
                                )
                        );
                    }
                }

                Collections.sort(apps, new Comparator<ActivityInfo>() {
                    @Override
                    public int compare(ActivityInfo appInfo, ActivityInfo t1) {
                        return appInfo.getLabel()
                                .compareTo(t1.getLabel());
                    }
                });

                AppsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateDataSet(apps);
                        progressBar.setVisibility(View.GONE);
                        dockAdapter.updateFromPreferences(app.launcherSettings);
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filterDataSet(newText);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(findViewById(R.id.clDrawer))) {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_SETTINGS) {
                int columns = app.launcherSettings.getInt(COLUMNS_SETTINGS, 4);
                StaggeredGridLayoutManager layoutManager =
                        new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
                rvAppList.setLayoutManager(layoutManager);
            }
            if (requestCode == REQUEST_UNINSTALL) {
                refreshApplicationsList();
                dockAdapter.removeShortcut(adapter.getAppSelected().getPackageName());
            }
        }
        adapter.setAppSelected(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UtopiaService.isRunning) {
            try {
                Intent service = new Intent(this, UtopiaService.class);
                startService(service);
            } catch (IllegalStateException ex) {
                if (BuildConfig.DEBUG) ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (adapter != null && adapter.getAppSelected() != null) {
            switch (item.getItemId()) {
                case R.id.action_pin_to_dock: {
                    dockAdapter.addItem(adapter.getAppSelected(), app.launcherSettings);
                    adapter.setAppSelected(null);
                    break;
                }
                case R.id.action_uninstall: {
                    Uri packageUri = Uri.parse("package:" + adapter.getAppSelected().getPackageName());
                    Intent uninstallIntent =
                            new Intent(Intent.ACTION_DELETE, packageUri);
                    startActivityForResult(uninstallIntent, REQUEST_UNINSTALL);
                    break;
                }
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (BuildConfig.DEBUG) Log.d(getClass().getCanonicalName(), "Received");
        IntentObservable obs =(IntentObservable) o;
        Intent intent = obs.getI();
        String pkg = Objects.requireNonNull(intent.getData()).toString().replace("package:", "");
        if (UtopiaLauncher.iconsCache.get(pkg) != null) UtopiaLauncher.iconsCache.remove(pkg);
        if (Objects.equals(obs.getI().getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            dockAdapter.removeShortcut(pkg);
        }
        refreshApplicationsList();
    }
}
