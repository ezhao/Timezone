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
    private Paint purplePaint;
    private Paint bluePaint;
    private Paint greenPaint;
    private float motionStartX;
    private float motionMoveX;
    private float currentOffset;
    private Path tickMarks;

    public TimezoneView(Context context, AttributeSet attrs) {
        super(context, attrs);

        testPaint = new Paint();
        testPaint.setColor(Color.WHITE);
        testPaint.setTextSize(24);

        tickPaint = new Paint();
        tickPaint.setStyle(Paint.Style.STROKE);
        tickPaint.setStrokeWidth(1);
        tickPaint.setColor(Color.WHITE);

        purplePaint = new Paint();
        purplePaint.setStyle(Paint.Style.FILL);
        purplePaint.setColor(Color.parseColor("#605691"));

        bluePaint = new Paint();
        bluePaint.setStyle(Paint.Style.FILL);
        bluePaint.setColor(Color.parseColor("#4982A8"));

        greenPaint = new Paint();
        greenPaint.setStyle(Paint.Style.FILL);
        greenPaint.setColor(Color.parseColor("#53AE53"));

        currentOffset = 0.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float WIDTH = getWidth();
        float HEIGHT = getHeight();
        int tickPadding = 42; // bit of space on either side
        int textPaddingSide = 8;
        int textPaddingTop = 40;
        float spacing = (3 * WIDTH - 2 * tickPadding) / 24;

        float newOffset = motionMoveX - motionStartX;
        float hiddenAmount = WIDTH - (currentOffset + newOffset);
        hiddenAmount = Math.max(hiddenAmount, 0.0f);
        hiddenAmount = Math.min(hiddenAmount, WIDTH * 2.0f);

        float positionX;

        // Background colors
        positionX = tickPadding - hiddenAmount;
        canvas.drawRect(positionX - tickPadding, 0.0f, positionX + 6 * spacing, HEIGHT, purplePaint);
        canvas.drawRect(positionX + 6 * spacing, 0.0f, positionX + 9 * spacing, HEIGHT, bluePaint);
        canvas.drawRect(positionX + 9 * spacing, 0.0f, positionX + 16 * spacing, HEIGHT, greenPaint);
        canvas.drawRect(positionX + 16 * spacing, 0.0f, positionX + 20 * spacing, HEIGHT, bluePaint);
        canvas.drawRect(positionX + 20 * spacing, 0.0f, positionX +tickPadding + 24 * spacing, HEIGHT, purplePaint);

        // Labels on the hours
        int displayHour;
        String displaySuffix;
        String displayString;

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

            displayString = String.valueOf(displayHour) + displaySuffix;
            positionX = i * spacing + textPaddingSide - hiddenAmount;
            canvas.drawText(displayString, positionX, textPaddingTop, testPaint);
        }

        // Tickmarks
        if (tickMarks == null) {
            tickMarks = new Path();
            for (int i = 0; i <= 8; i++) {
                positionX = i * spacing;
                tickMarks.moveTo(positionX, HEIGHT * 0.3f);
                tickMarks.lineTo(positionX, HEIGHT);
                tickMarks.moveTo(positionX + spacing / 2, HEIGHT * 0.6f);
                tickMarks.lineTo(positionX + spacing / 2, HEIGHT);
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
