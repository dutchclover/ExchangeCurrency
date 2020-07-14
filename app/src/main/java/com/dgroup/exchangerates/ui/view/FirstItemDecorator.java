package com.dgroup.exchangerates.ui.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.dgroup.exchangerates.R;

import java.util.logging.Logger;

public class FirstItemDecorator extends RecyclerView.ItemDecoration {

    private Paint paint;

    public FirstItemDecorator(int color) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final View child = parent.getChildAt(0);
        int pos = parent.getChildAdapterPosition(child);
        if (pos == 0) {
            final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            c.drawLine(
                    0,
                    layoutManager.getDecoratedBottom(child),
                    layoutManager.getDecoratedMeasuredWidth(child),
                    layoutManager.getDecoratedBottom(child),
                    paint);
        }
    }

}
