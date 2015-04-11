package com.herokuapp.ezhao.timezone;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TimezoneView extends View {
    private int startingHour;
    private Paint textPaint;
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

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TimezoneView, 0, 0);
        try {
            startingHour = typedArray.getInt(R.styleable.TimezoneView_startingHour, 0);
            startingHour %= 24;
        } finally {
            typedArray.recycle();
        }

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(24);

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
        int tickPadding = 42; // space on either side
        int textPaddingSide = 8;
        int textPaddingTop = 40;
        float hourWidth = (3 * WIDTH - 2 * tickPadding) / 24;

        float newOffset = motionMoveX - motionStartX;
        float hiddenAmount = WIDTH - (currentOffset + newOffset);
        hiddenAmount = Math.max(hiddenAmount, 0.0f);
        hiddenAmount = Math.min(hiddenAmount, WIDTH * 2.0f);

        float positionX;

        // Background colors
        positionX = tickPadding - hiddenAmount;
        Paint[] paints = new Paint[] {purplePaint, bluePaint, greenPaint, bluePaint, purplePaint};
        int[] paintChangeTimes = new int[] {6 - startingHour, 10 - startingHour, 16 - startingHour, 20 - startingHour};

        // TODO (emily) need if statements and paint buckets for other startingHours
        if (startingHour >= 6) {
            paints = new Paint[] {purplePaint, bluePaint, greenPaint, bluePaint, purplePaint};
            paintChangeTimes = new int[] {6 - startingHour, 10 - startingHour, 16 - startingHour, 20 - startingHour};
        }

        for (int i = 0; i < paintChangeTimes.length; i++) {
            if (i == 0) {
                canvas.drawRect(positionX - tickPadding, 0.0f, positionX + paintChangeTimes[i] * hourWidth, HEIGHT, paints[i]);
            } else {
                canvas.drawRect(positionX + paintChangeTimes[i-1] * hourWidth, 0.0f, positionX + paintChangeTimes[i] * hourWidth, HEIGHT, paints[i]);
            }

            if (i == paintChangeTimes.length - 1) {
                canvas.drawRect(positionX + paintChangeTimes[i] * hourWidth, 0.0f, positionX + tickPadding + 24 * hourWidth, HEIGHT, paints[i+1]);
            }
        }

        // Labels on the hours
        int displayHour;
        String displaySuffix;
        String displayString;

        for (int i = startingHour; i <= startingHour + 24; i++) {
            displaySuffix = "AM";
            if (i % 24 >= 12) {
                displaySuffix = "PM";
            }
            displayHour = i % 12;
            if (i % 12 == 0) {
                displayHour = 12;
            }

            displayString = String.valueOf(displayHour) + displaySuffix;
            positionX = (i - startingHour) * hourWidth + textPaddingSide - hiddenAmount;
            canvas.drawText(displayString, positionX, textPaddingTop, textPaint);
        }

        // Tickmarks
        if (tickMarks == null) {
            tickMarks = new Path();
            for (int i = 0; i <= 8; i++) {
                positionX = i * hourWidth;
                tickMarks.moveTo(positionX, HEIGHT * 0.3f);
                tickMarks.lineTo(positionX, HEIGHT);
                tickMarks.moveTo(positionX + hourWidth / 2, HEIGHT * 0.6f);
                tickMarks.lineTo(positionX + hourWidth / 2, HEIGHT);
            }
        }
        float tickMarksOffset = (tickPadding - hiddenAmount) % hourWidth;
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
