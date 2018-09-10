package io.launcher.utopia.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import java.util.ArrayList;
import java.util.Collections;

import io.launcher.utopia.R;
import io.launcher.utopia.models.AppInfo;
import io.launcher.utopia.utils.ItemTouchHelperAdapter;
import io.launcher.utopia.utils.Tools;

/**
 * Created by fernando on 10/15/17.
 */

public abstract class ApplicationsAdapter extends RecyclerView.Adapter<GenericViewHolder> implements ItemTouchHelperAdapter {
    private Context mContext;
    private ArrayList<AppInfo> mItems;

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    protected ApplicationsAdapter(Context c, ArrayList<AppInfo> appInfos) {
        mContext = c;
        mItems = appInfos;
    }

    @Override
    public GenericViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_app, parent, false);
        return new GenericViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final GenericViewHolder holder, int position) {
        final AppInfo current = mItems.get(holder.getAdapterPosition());

        if (current.getCachedImage() != null) {
            holder.getView(R.id.ivIcon, ImageView.class).setImageBitmap(current.getCachedImage());
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap b = Tools.compress(createIcon(mContext, current), 70);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            current.setCachedImage(b);
                            holder.getView(R.id.ivIcon, ImageView.class)
                                    .setImageBitmap(current.getCachedImage());
                        }
                    });
                }
            }).start();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAppPressed(mItems.get(holder.getAdapterPosition()));
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                onAppLongPressed(mItems.get(holder.getAdapterPosition()));
                return false;
            }
        });

        SpringSystem springSystem = SpringSystem.create();
        Spring spring = springSystem.createSpring();
        spring.setSpringConfig(new SpringConfig(250, 10));
        spring.setCurrentValue(0.5f);
        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                holder.itemView.setScaleX(value);
                holder.itemView.setScaleY(value);
            }
        });
        spring.setEndValue(1);

    }

    protected abstract void onAppPressed(AppInfo app);
    protected abstract void onAppLongPressed(AppInfo app);

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static Bitmap createIcon(Context ctx, AppInfo item) {
        LayoutInflater  mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.item_square, null, false);
        GenericViewHolder vh = new GenericViewHolder(view);
        vh.getView(R.id.ivIcon, ImageView.class).setImageDrawable(item.icon);
        vh.getView(R.id.tvAppName, TextView.class).setText(item.label);
        vh.getView(R.id.tvAppName, TextView.class).setGravity(Gravity.CENTER);
        Drawable bg = ContextCompat.getDrawable(ctx, R.drawable.item_background);
        bg.setColorFilter(item.bgColor, PorterDuff.Mode.SRC_IN);
        vh.itemView.setBackground(createBackground(item.bgColor, item.bgColorDark));
        vh.getView(R.id.tvAppName, TextView.class).setTextColor(Color.WHITE);
        vh.getView(R.id.tvAppName, TextView.class).setShadowLayer(5, 1, 1, Color.BLACK);

        view.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);

        view.draw(c);
        return bitmap;
    }

    private static Drawable createBackground(int color, int dark) {
        int[] colors = new int[2];
        colors[0] = color;
        colors[1] = dark;

        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        d.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(6);

        return d;
    }
}
