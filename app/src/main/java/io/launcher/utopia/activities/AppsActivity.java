package io.launcher.utopia.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.launcher.utopia.BuildConfig;
import io.launcher.utopia.R;
import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.adapters.ResolveInfoAdapter;
import io.launcher.utopia.adapters.ResolveInfoDockAdapter;
import io.launcher.utopia.utils.SimpleItemTouchHelperCallback;
import io.launcher.utopia.utils.SpaceItemDecoration;
import io.launcher.utopia.utils.Tools;

import static io.launcher.utopia.UtopiaLauncher.COLUMNS_SETTINGS;
import static io.launcher.utopia.activities.SettingsActivity.REQUEST_SETTINGS;

public class AppsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final int REQUEST_UNINSTALL = 7686;
    private PackageManager mPkgManager = null;
    private final ArrayList<ResolveInfo> apps = new ArrayList<>();
    private final ArrayList<ResolveInfo> docked = new ArrayList<>();
    private UtopiaLauncher app = null;
    private ResolveInfoAdapter adapter = null;
    private ResolveInfoDockAdapter dockAdapter = null;
    private final DisplayMetrics metrics = new DisplayMetrics();
    private RecyclerView rvAppList;
    public static Intent lastIntent = null;
    private DrawerLayout mDrawerLayout;
    private ProgressBar progressBar;
    private boolean isLoading = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        mPkgManager = getPackageManager();
        app = (UtopiaLauncher) getApplication();
        progressBar = (ProgressBar) findViewById(R.id.pbLoading);
        rvAppList = (RecyclerView) findViewById(R.id.rvAppList);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        initDock();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int columns = app.launcherSettings.getInt(COLUMNS_SETTINGS, 4);

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
        rvAppList.setLayoutManager(layoutManager);

        SearchView svSearch = (SearchView) findViewById(R.id.svSearch);

        svSearch.setOnQueryTextListener(this);

        adapter = new ResolveInfoAdapter(this, apps, mPkgManager) {
            @Override
            public void onAppPressed(ResolveInfo app) {
                Intent toStart = mPkgManager.getLaunchIntentForPackage(app.activityInfo.packageName);
                AppsActivity.this.startActivity(toStart);
            }
        };
        SpaceItemDecoration decoration = new SpaceItemDecoration(16);
        rvAppList.addItemDecoration(decoration);
        rvAppList.setAdapter(adapter);

        rvAppList.setItemViewCacheSize(30);

        AppCompatImageView ivsettings = (AppCompatImageView) findViewById(R.id.ivSettings);
        ivsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppsActivity.this, SettingsActivity.class);
                startActivityForResult(intent, REQUEST_SETTINGS);
            }
        });

        loadApplications();

        registerForContextMenu(rvAppList);
    }

    private void initDock() {
        RecyclerView navigationView = (RecyclerView) findViewById(R.id.dock);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        llm.setStackFromEnd(true);
        navigationView.setLayoutManager(llm);
        dockAdapter = new ResolveInfoDockAdapter(this, docked) {
            @Override
            protected void onAppPressed(ResolveInfo app) {
                mDrawerLayout.closeDrawers();
                Intent toStart = mPkgManager.getLaunchIntentForPackage(app.activityInfo.packageName);
                AppsActivity.this.startActivity(toStart);
            }

            @Override
            protected void onAppLongPressed(final ResolveInfo app) {

            }
        };
        navigationView.setAdapter(dockAdapter);
        navigationView.addItemDecoration(new SpaceItemDecoration(8));

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(dockAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(navigationView);
    }

    private synchronized void loadApplications() {
        if (isLoading) return;
        if (app.applicationsInstalled.size() == 0 ) {
            progressBar.setVisibility(View.VISIBLE);
            isLoading = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Intent.ACTION_MAIN, null);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);

                    apps.clear();
                    apps.addAll(mPkgManager.queryIntentActivities(intent, 0));
                    createIconCache(apps);
                    Collections.sort(apps, new Comparator<ResolveInfo>() {
                        @Override
                        public int compare(ResolveInfo appInfo, ResolveInfo t1) {
                            return appInfo.loadLabel(mPkgManager).toString()
                                    .compareTo(t1.loadLabel(mPkgManager).toString());
                        }
                    });
                    app.applicationsInstalled.addAll(apps);
                    AppsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            isLoading = false;
                        }
                    });
                }
            }).start();
        }
    }

    private synchronized int countApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mPkgManager.queryIntentActivities(intent, 0).size();
    }

    private void createIconCache(ArrayList<ResolveInfo> items) {
        Bitmap icon = null;
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
        ArrayList<ResolveInfo> temp = new ArrayList<>();
        for(int i = 0; i < app.applicationsInstalled.size(); i++) {
            final String label = app.applicationsInstalled.get(i).loadLabel(mPkgManager).toString();
            if (label.toLowerCase().contains(newText.toLowerCase())) {
                temp.add(app.applicationsInstalled.get(i));
            }
        }
        apps.clear();
        apps.addAll(temp);
        temp.clear();
        adapter.notifyDataSetChanged();
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
                if (app.applicationsInstalled.size() != countApps()) {
                    app.applicationsInstalled.clear();
                    loadApplications();
                    lastIntent = null;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (app.applicationsInstalled.size() != countApps() && adapter != null) {
            app.applicationsInstalled.clear();
            loadApplications();
            lastIntent = null;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (adapter != null && adapter.getAppSelected() != null) {
            switch (item.getItemId()) {
                case R.id.action_pin_to_dock: {
                    docked.add(adapter.getAppSelected());
                    dockAdapter.notifyItemInserted(dockAdapter.getItemCount() - 1);
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
