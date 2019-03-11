package com.example.herem1t.rc_client.ui.screens.mainlist;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Herem1t on 21.04.2018.
 */

public class CustomDeviderItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[] {android.R.attr.listDivider};
    private static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    private static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Drawable devider;
    private int orientation;
    private Context context;
    private int margin;

    public CustomDeviderItemDecoration(Context context, int orientation, int margin){
        this.context = context;
        this.margin = margin;
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        devider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("Incalid orientation");
        }
        this.orientation = orientation;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (orientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + devider.getIntrinsicHeight();
            devider.setBounds(left + dpToPx(margin), top, right - dpToPx(0), bottom);
            devider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + devider.getIntrinsicHeight();
            devider.setBounds(left, top + dpToPx(margin), right, bottom - dpToPx(margin));
            devider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (orientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, devider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, devider.getIntrinsicWidth(), 0);
        }
    }

    private int dpToPx(int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
