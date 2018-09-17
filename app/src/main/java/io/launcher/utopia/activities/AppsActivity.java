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
import android.support.design.widget.NavigationView;
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
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    private PackageManager mPkgManager = null;
    private ArrayList<ResolveInfo> apps = new ArrayList<>();
    private ArrayList<ResolveInfo> docked = new ArrayList<>();
    private UtopiaLauncher app = null;
    private ResolveInfoAdapter adapter = null;
    private ResolveInfoDockAdapter dockAdapter = null;
    private DisplayMetrics metrics = new DisplayMetrics();
    private RecyclerView rvAppList;
    public static Intent lastIntent = null;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        mPkgManager = getPackageManager();
        app = (UtopiaLauncher) getApplication();

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

            @Override
            public void onAppLongPressed(final ResolveInfo app) {
                AlertDialog.Builder b = new AlertDialog.Builder(AppsActivity.this);
                b.setTitle(app.loadLabel(mPkgManager).toString());
                b.setMessage("What do you want to do?");
                b.setNegativeButton("UNINSTALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri packageUri = Uri.parse("package:" + app.activityInfo.packageName);
                        Intent uninstallIntent =
                                new Intent(Build.VERSION.SDK_INT > 19? Intent.ACTION_DELETE :Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                        startActivity(uninstallIntent);
                    }
                });
                b.setPositiveButton("ADD TO DOCK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        docked.add(app);
                        dockAdapter.notifyItemInserted(dockAdapter.getItemCount() - 1);
                    }
                });
                b.show();
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

    private void loadApplications() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating icons cache...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);

                apps.clear();
                apps.addAll(mPkgManager.queryIntentActivities(intent, 0));
                Collections.sort(apps, new Comparator<ResolveInfo>() {
                    @Override
                    public int compare(ResolveInfo appInfo, ResolveInfo t1) {
                        return appInfo.loadLabel(mPkgManager).toString()
                                .compareTo(t1.loadLabel(mPkgManager).toString());
                    }
                });

                createIconCache(apps);
                app.applicationsInstalled.addAll(apps);
                AppsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    private void createIconCache(ArrayList<ResolveInfo> items) {
        Bitmap icon = null, color = null;
        for(ResolveInfo item : items) {
            final String packageName = item.activityInfo.packageName;
            if (UtopiaLauncher.iconsCache.get(packageName) == null) {
                icon = Tools.getBitmapFromDrawable(item.loadIcon(mPkgManager), Bitmap.Config.ARGB_4444);
                UtopiaLauncher.iconsCache.put(packageName, icon);
                if (UtopiaLauncher.bgCache.get(packageName) == null) {
                    color = Tools.getBitmapFromDrawable(item.loadIcon(mPkgManager), Bitmap.Config.RGB_565);
                    int[] colors = Tools.getColorsFromBitmap(color);
                    UtopiaLauncher.bgCache.put(packageName, Tools.createBackground(colors));
                }
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

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_SETTINGS) {
            int columns = app.launcherSettings.getInt(COLUMNS_SETTINGS, 4);

            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
            rvAppList.setLayoutManager(layoutManager);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lastIntent != null) {
            loadApplications();
            lastIntent = null;
        }
    }
}
