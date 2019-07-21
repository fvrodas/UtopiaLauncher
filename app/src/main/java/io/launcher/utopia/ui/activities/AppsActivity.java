package io.launcher.utopia.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import io.launcher.utopia.BuildConfig;
import io.launcher.utopia.R;
import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.adapters.ResolveInfoAdapter;
import io.launcher.utopia.adapters.ResolveInfoDockAdapter;
import io.launcher.utopia.presenters.AppsPresenter;
import io.launcher.utopia.services.UtopiaService;
import io.launcher.utopia.ui.IDockItem;
import io.launcher.utopia.utils.ActivityInfo;
import io.launcher.utopia.utils.IntentObservable;
import io.launcher.utopia.utils.SerializeHelper;
import io.launcher.utopia.utils.SimpleItemTouchHelperCallback;
import io.launcher.utopia.utils.SpaceItemDecoration;
import io.launcher.utopia.utils.Tools;
import io.launcher.utopia.views.IAppsView;

import static io.launcher.utopia.UtopiaLauncher.COLUMNS_SETTINGS;
import static io.launcher.utopia.UtopiaLauncher.GRAVITY_SETTINGS;
import static io.launcher.utopia.ui.activities.SettingsActivity.REQUEST_SETTINGS;

public class AppsActivity extends AppCompatActivity implements IAppsView, IDockItem, SearchView.OnQueryTextListener, Observer {
    private static final int REQUEST_UNINSTALL = 7686;
    private UtopiaLauncher app = null;
    private ResolveInfoAdapter adapter = null;
    private ResolveInfoDockAdapter dockAdapter = null;
    private RecyclerView rvAppList;
    private DrawerLayout mDrawerLayout;
    private final SerializeHelper<ArrayList<ActivityInfo>> helper = new SerializeHelper<>();
    private SwipeRefreshLayout mSwipeLayout;
    private AppsPresenter mPresenter;
    private Integer gravity = null;
    private Integer columns = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        app = (UtopiaLauncher) getApplication();
        rvAppList = findViewById(R.id.rvAppList);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mSwipeLayout = findViewById(R.id.srlShortcut);
        mSwipeLayout.setEnabled(false);

        mPresenter = new AppsPresenter(app, helper);
        mPresenter.attachView(this);

        app.observable.addObserver(this);

        mPresenter.readIntStrFromSettings(COLUMNS_SETTINGS, "4");
        mPresenter.readIntFromSettings(GRAVITY_SETTINGS, GravityCompat.END);

        SearchView svSearch = findViewById(R.id.svSearch);

        svSearch.setOnQueryTextListener(this);

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
                if (gravity == GravityCompat.END) {
                    (findViewById(R.id.rootView)).setTranslationX(-moveFactor);
                } else {
                    (findViewById(R.id.rootView)).setTranslationX(moveFactor);
                }
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

        mPresenter.retrieveApplicationsList(savedInstanceState, getPackageManager());

        //region Dock Initialisation
        RecyclerView navigationView = findViewById(R.id.dock);
        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        llm.setStackFromEnd(false);
        navigationView.setLayoutManager(llm);

        dockAdapter = new ResolveInfoDockAdapter(new ArrayList<ActivityInfo>(), this, app.launcherSettings);
        navigationView.setAdapter(dockAdapter);
        navigationView.addItemDecoration(new SpaceItemDecoration(8));

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(dockAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(navigationView);
        //endregion

        registerForContextMenu(rvAppList);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
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
                mPresenter.readIntStrFromSettings(COLUMNS_SETTINGS, "4");
                mPresenter.readIntFromSettings(GRAVITY_SETTINGS, GravityCompat.END);
            }
            if (requestCode == REQUEST_UNINSTALL) {
                mPresenter.retrieveApplicationsList(getPackageManager());
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
        rvAppList.scrollToPosition(0);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (adapter != null && adapter.getAppSelected() != null) {
            switch (item.getItemId()) {
                case R.id.action_pin_to_dock: {
                    if (dockAdapter.exists(adapter.getAppSelected())) {
                        showMessage(getString(R.string.apps_app_exists));
                    } else {
                        dockAdapter.addItem(adapter.getAppSelected());
                        adapter.setAppSelected(null);
                        mDrawerLayout.openDrawer(gravity);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mDrawerLayout.closeDrawers();
                            }
                        }, 500);
                    }
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
        mPresenter.removeFromIconCache(pkg);
        if (Objects.equals(obs.getI().getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            dockAdapter.removeShortcut(pkg);
        }
        mPresenter.retrieveApplicationsList(getPackageManager());
    }

    private void openActivity(ActivityInfo activityInfo) {
        try {
            Intent toStart = getPackageManager().getLaunchIntentForPackage(activityInfo.getPackageName());
            Objects.requireNonNull(toStart).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Objects.requireNonNull(toStart).addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            AppsActivity.this.startActivity(toStart);
        } catch (Exception e) {
            mPresenter.removeFromIconCache(activityInfo);
            dockAdapter.removeShortcut(activityInfo.getPackageName());
            mPresenter.retrieveApplicationsList(getPackageManager());
        }
    }

    @Override
    public void populateApplicationsList(final ArrayList<ActivityInfo> apps){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.updateDataSet(apps);
                mPresenter.readPersistentDockList();
                showProgress(false);
            }
        });
    }

    @Override
    public void onDockItemsRetrieved(ArrayList<ActivityInfo> dock) {
        dockAdapter.updateDataSet(dock);
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
            GridLayoutManager layoutManager =
                    new GridLayoutManager(this, columns);
            rvAppList.setLayoutManager(layoutManager);
            if (adapter == null) {
                adapter = new ResolveInfoAdapter(new ArrayList<ActivityInfo>(), this);
                SpaceItemDecoration decoration = new SpaceItemDecoration(16);
                rvAppList.addItemDecoration(decoration);
                rvAppList.setHasFixedSize(true);
                rvAppList.setAdapter(adapter);
                rvAppList.setItemViewCacheSize(100);
            }
            changeDockGravity(gravity);
        }
    }

    public void changeDockGravity(int gravity) {
        try {
            View drawer = findViewById(R.id.clDrawer);
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawer.getLayoutParams();
            params.gravity = gravity;
            drawer.setLayoutParams(params);
            drawer.requestLayout();
            mDrawerLayout.closeDrawer(gravity);
        } catch (RuntimeException ex) {
            if (BuildConfig.DEBUG) ex.printStackTrace();
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
        mSwipeLayout.setRefreshing(show);
    }

    @Override
    public void onAppPressed(ActivityInfo app) {
        if (mDrawerLayout.isDrawerOpen(gravity)) mDrawerLayout.closeDrawers();
        openActivity(app);
    }

    @Override
    public void onAppLongPressed(final ActivityInfo app) {

    }

    @Override
    public void onItemRemoved(ArrayList<ActivityInfo> items) {
        showMessage(getString(R.string.apps_msg_removed));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Commented to avoid Force Close
//        mPresenter.onDestroy();
    }
}
