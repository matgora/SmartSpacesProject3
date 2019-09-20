package com.example.smartspacesproject3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import java.util.Vector;

public class DrawView extends View
{
    // CONSTANT VARIABLES

    Paint paint = new Paint();
    public DrawView(Context context) {
        super(context);

    }

    /**
     * redraws the view
     * for when the boxes are changed and must be drawn again
     */
    public void updateView()
    {
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {

        canvas.drawColor(paint.getColor());

    }

    public void changePaintColor(int color)
    {
        paint.setColor(color);
    }

}
