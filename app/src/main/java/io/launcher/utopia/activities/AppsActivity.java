package io.launcher.utopia.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.launcher.utopia.R;
import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.adapters.ApplicationsAdapter;
import io.launcher.utopia.models.AppInfo;
import io.launcher.utopia.utils.SimpleItemTouchHelperCallback;
import io.launcher.utopia.utils.SpaceItemDecoration;
import io.launcher.utopia.utils.Tools;

import static io.launcher.utopia.UtopiaLauncher.COLUMNS_SETTINGS;
import static io.launcher.utopia.activities.SettingsActivity.REQUEST_SETTINGS;

public class AppsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private PackageManager mPkgManager = null;
    private ArrayList<AppInfo> apps = new ArrayList<>();
    private UtopiaLauncher app = null;
    private ApplicationsAdapter adapter = null;
    private DisplayMetrics metrics = new DisplayMetrics();
    private RecyclerView rvAppList;
    private BroadcastReceiver appsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            app = (UtopiaLauncher) getApplication();
            app.applicationsInstalled = new SparseArray<>();
            loadApplications();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        mPkgManager = getPackageManager();
        app = (UtopiaLauncher) getApplication();

        rvAppList = (RecyclerView) findViewById(R.id.rvAppList);

        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int columns = app.launcherSettings.getInt(COLUMNS_SETTINGS, 4);

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
        rvAppList.setLayoutManager(layoutManager);

        SearchView svSearch = (SearchView) findViewById(R.id.svSearch);

        svSearch.setOnQueryTextListener(this);

        adapter = new ApplicationsAdapter(this, apps) {
            @Override
            public void onAppPressed(AppInfo app) {
                Intent toStart = mPkgManager.getLaunchIntentForPackage(app.name.toString());
                AppsActivity.this.startActivity(toStart);
            }

            @Override
            public void onAppLongPressed(AppInfo app) {

            }
        };
        SpaceItemDecoration decoration = new SpaceItemDecoration(16);
        rvAppList.addItemDecoration(decoration);
        rvAppList.setAdapter(adapter);

        rvAppList.setItemViewCacheSize(30);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvAppList);

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

    private void loadApplications() {
        if(app.applicationsInstalled.size() > 0) {
            apps.clear();
            for (int i = 0; i < app.applicationsInstalled.size(); i++) {
                apps.add(app.applicationsInstalled.valueAt(i));
            }
            adapter.notifyDataSetChanged();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(Intent.ACTION_MAIN, null);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);

                    List<ResolveInfo> available = mPkgManager.queryIntentActivities(intent, 0);
                    Collections.sort(available, new Comparator<ResolveInfo>() {
                        @Override
                        public int compare(ResolveInfo appInfo, ResolveInfo t1) {
                            return appInfo.loadLabel(mPkgManager).toString()
                                    .compareTo(t1.loadLabel(mPkgManager).toString());
                        }
                    });
                    apps.clear();
                    for (int i = 0; i < available.size(); i++) {
                        AppInfo appInfo = new AppInfo();
                        appInfo.label = available.get(i).loadLabel(mPkgManager);
                        appInfo.name = available.get(i).activityInfo.packageName;
                        appInfo.icon = available.get(i).loadIcon(mPkgManager);
                        int colors[] = getColorsFromBitmap(appInfo.icon);
                        appInfo.bgColor = colors[0];
                        appInfo.bgColorDark = colors[1];
                        appInfo.textColor = Tools.ColorTools.getContrastColor(appInfo.bgColor);
                        appInfo.setCachedBackground(createBackground(colors));
                        apps.add(appInfo);
                    }

                    for (int i = 0; i < apps.size(); i++) {
                        app.applicationsInstalled.put(i, apps.get(i));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        SparseArray<AppInfo> temp = new SparseArray<>();
        for(int i = 0; i < app.applicationsInstalled.size(); i++) {
            if (app.applicationsInstalled.valueAt(i)
                    .label.toString().toLowerCase().contains(newText.toLowerCase())) {
                temp.put(i, app.applicationsInstalled.valueAt(i));
            }

        }
        apps.clear();
        for(int i = 0; i < temp.size(); i++) {
            apps.add(temp.valueAt(i));
        }
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadApplications();
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

    @NonNull
    private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return Tools.compress(bmp, 70);
    }

    private int[] getColorsFromBitmap(Drawable icon) {
        int[] colors = new int[3];
        Palette p = Palette.from((getBitmapFromDrawable(icon))).generate();
        int color;
        if (p.getVibrantSwatch() != null) {
            color = p.getVibrantSwatch().getRgb();
        } else if (p.getLightVibrantSwatch() != null) {
            color = p.getLightVibrantSwatch().getRgb();
        } else {
            color = Color.LTGRAY;
        }

        float[] hsl1 = new float[3];
        ColorUtils.colorToHSL(color, hsl1);
        hsl1[0] = hsl1[0] * 0.85f;
        hsl1[1] = hsl1[1] * 0.6f;
        hsl1[2] = .8f;
        colors[0] = ColorUtils.HSLToColor(hsl1);

        float[] hsl = new float[3];
        ColorUtils.colorToHSL(color, hsl);
        hsl[0] = hsl[0];
        hsl[1] = hsl[1] * 0.7f;
        hsl[2] = .5f;
        colors[1] = ColorUtils.HSLToColor(hsl);

        float[] hsl2 = new float[3];
        ColorUtils.colorToHSL(color, hsl2);
        hsl2[0] = hsl2[0] * 1.1f;
        hsl2[1] = hsl2[1] * 0.8f;
        hsl2[2] = .3f;
        colors[2] = ColorUtils.HSLToColor(hsl2);

        return colors;
    }

    private static Drawable createBackground(int[] colors) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        d.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        d.setSize(16, 16);
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(8);
        return d;
    }

}
