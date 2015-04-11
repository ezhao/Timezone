package com.herokuapp.ezhao.timezone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TimezoneView extends View {
    private Paint testPaint;
    private Paint tickPaint;
    private float motionStartX;
    private float motionMoveX;
    private float currentOffset;
    private Path tickMarks;
    private int tickPadding = 16; // bit of space on either side
    private int topPadding = 20;

    public TimezoneView(Context context, AttributeSet attrs) {
        super(context, attrs);

        testPaint = new Paint();
        testPaint.setStyle(Paint.Style.FILL);
        testPaint.setColor(Color.BLACK);

        tickPaint = new Paint();
        tickPaint.setStyle(Paint.Style.STROKE);
        tickPaint.setStrokeWidth(1);
        tickPaint.setColor(Color.LTGRAY);

        currentOffset = 0.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float WIDTH = getWidth();
        float HEIGHT = getHeight();
        float spacing = (3 * WIDTH - 2 * tickPadding) / 24;

        float newOffset = motionMoveX - motionStartX;
        float hiddenAmount = WIDTH - (currentOffset + newOffset);
        hiddenAmount = Math.max(hiddenAmount, 0.0f);
        hiddenAmount = Math.min(hiddenAmount, WIDTH * 2.0f);

        // Labels on the hours
        int displayHour;
        String displaySuffix;
        String displayString;
        float positionX;
        for (int i = 0; i <= 24; i++) {
            displayHour = i;
            displaySuffix = "AM";
            if (i >= 12) {
                displayHour = i - 12;
                displaySuffix = "PM";
            }
            if (displayHour == 0) {
                displayHour = 12;
            }
            if (i == 24) {
                displayHour = 12;
                displaySuffix = "AM";
            }

            displaySuffix = String.valueOf(displayHour) + displaySuffix;
            positionX = i * spacing - hiddenAmount;
            canvas.drawText(displaySuffix, positionX, topPadding, testPaint);
        }

        if (tickMarks == null) {
            tickMarks = new Path();
            for (int i = 0; i <= 8; i++) {
                positionX = i * spacing;
                tickMarks.moveTo(positionX, topPadding);
                tickMarks.lineTo(positionX, HEIGHT);
                tickMarks.moveTo(positionX + spacing / 2, topPadding);
                tickMarks.lineTo(positionX + spacing / 2, HEIGHT / 2);
            }
        }
        float tickMarksOffset = (tickPadding - hiddenAmount) % spacing;
        tickMarks.offset(tickMarksOffset, 0.0f);
        canvas.drawPath(tickMarks, tickPaint);
        tickMarks.offset(-1.0f * tickMarksOffset, 0.0f);
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
                currentOffset = Math.min(currentOffset, getWidth());
                currentOffset = Math.max(currentOffset, -getWidth());
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
