package com.herokuapp.ezhao.timezone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TimezoneView extends View {
    private Paint testPaint;
    private float motionStartX;
    private float motionMoveX;
    private float currentOffset;

    public TimezoneView(Context context, AttributeSet attrs) {
        super(context, attrs);

        testPaint = new Paint();
        testPaint.setStyle(Paint.Style.FILL);
        testPaint.setColor(Color.BLACK);

        currentOffset = 0.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float newOffset = motionMoveX - motionStartX;
        canvas.drawRect(currentOffset+newOffset, 0, 300+currentOffset+newOffset, getHeight(), testPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                motionStartX = event.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                motionMoveX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                currentOffset += event.getX() - motionStartX;
                motionMoveX = 0.0f;
                motionStartX = 0.0f;
                break;
            default:
                break;
        }

        postInvalidate();
        return false;
    }
}
