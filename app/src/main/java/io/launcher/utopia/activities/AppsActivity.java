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
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import io.launcher.utopia.BuildConfig;
import io.launcher.utopia.R;
import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.adapters.ResolveInfoAdapter;
import io.launcher.utopia.adapters.ResolveInfoDockAdapter;
import io.launcher.utopia.services.UtopiaService;
import io.launcher.utopia.utils.SimpleItemTouchHelperCallback;
import io.launcher.utopia.utils.SpaceItemDecoration;
import io.launcher.utopia.utils.Tools;

import static io.launcher.utopia.UtopiaLauncher.COLUMNS_SETTINGS;
import static io.launcher.utopia.activities.SettingsActivity.REQUEST_SETTINGS;

public class AppsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final int REQUEST_UNINSTALL = 7686;
    private final DisplayMetrics metrics = new DisplayMetrics();
    private PackageManager mPkgManager = null;
    private UtopiaLauncher app = null;
    private ResolveInfoAdapter adapter = null;
    private ResolveInfoDockAdapter dockAdapter = null;
    private RecyclerView rvAppList;
    private DrawerLayout mDrawerLayout;
    private ProgressBar progressBar;


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

        int columns = app.launcherSettings.getInt(COLUMNS_SETTINGS, 4);

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
        rvAppList.setLayoutManager(layoutManager);

        SearchView svSearch = findViewById(R.id.svSearch);

        svSearch.setOnQueryTextListener(this);

        adapter = new ResolveInfoAdapter(new ArrayList<ResolveInfo>(), mPkgManager) {
            @Override
            public void onAppPressed(ResolveInfo app) {
                Intent toStart = mPkgManager.getLaunchIntentForPackage(app.activityInfo.packageName);
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

        dockAdapter = new ResolveInfoDockAdapter(new ArrayList<ResolveInfo>()) {
            @Override
            protected void onAppPressed(ResolveInfo app) {
                mDrawerLayout.closeDrawers();
                Intent toStart = mPkgManager.getLaunchIntentForPackage(app.activityInfo.packageName);
                AppsActivity.this.startActivity(toStart);
            }

            @Override
            protected void onAppLongPressed(final ResolveInfo app) {

            }

            @Override
            protected void onItemRemoved(ArrayList<ResolveInfo> items) {
                SharedPreferences.Editor editor = app.launcherSettings.edit();
                editor.putString(UtopiaLauncher.DOCK, new Gson().toJson(items));
                editor.apply();
            }

            @Override
            protected void onItemSwapped(ArrayList<ResolveInfo> items) {
                SharedPreferences.Editor editor = app.launcherSettings.edit();
                editor.putString(UtopiaLauncher.DOCK, new Gson().toJson(items));
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

                final ArrayList<ResolveInfo> apps = new ArrayList<>(mPkgManager.queryIntentActivities(intent, 0));

                createIconCache(apps);

                Collections.sort(apps, new Comparator<ResolveInfo>() {
                    @Override
                    public int compare(ResolveInfo appInfo, ResolveInfo t1) {
                        return appInfo.loadLabel(mPkgManager).toString()
                                .compareTo(t1.loadLabel(mPkgManager).toString());
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

    private void createIconCache(ArrayList<ResolveInfo> items) {
        Bitmap icon;
        for(ResolveInfo item : items) {
            final String packageName = item.activityInfo.packageName;
            if (UtopiaLauncher.iconsCache.get(packageName) == null) {
                icon = Tools.createIcon(this, Tools.getBitmapFromDrawable(item.loadIcon(mPkgManager), Bitmap.Config.ARGB_8888));
                UtopiaLauncher.iconsCache.put(packageName, icon);
            }
        }
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
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_SETTINGS) {
                int columns = app.launcherSettings.getInt(COLUMNS_SETTINGS, 4);

                StaggeredGridLayoutManager layoutManager =
                        new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
                rvAppList.setLayoutManager(layoutManager);
            }
            if (requestCode == REQUEST_UNINSTALL) {
                refreshApplicationsList();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if  (!UtopiaService.isRunning) {
            Intent service = new Intent(this, UtopiaService.class);
            startService(service);
        }
        if (app.refreshNeeded) {
           refreshApplicationsList();
           app.refreshNeeded = false;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (adapter != null && adapter.getAppSelected() != null) {
            switch (item.getItemId()) {
                case R.id.action_pin_to_dock: {
                    dockAdapter.addItem(adapter.getAppSelected(), app.launcherSettings);
                    break;
                }
                case R.id.action_uninstall: {
                    Uri packageUri = Uri.parse("package:" + adapter.getAppSelected().activityInfo.packageName);
                    Intent uninstallIntent =
                            new Intent(Build.VERSION.SDK_INT > 19? Intent.ACTION_DELETE :Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                    startActivityForResult(uninstallIntent, REQUEST_UNINSTALL);
                    break;
                }
            }
            adapter.setAppSelected(null);
        }
        return super.onContextItemSelected(item);
    }
}
