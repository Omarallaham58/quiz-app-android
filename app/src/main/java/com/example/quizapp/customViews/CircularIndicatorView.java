package com.example.quizapp.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.graphics.Color;
import android.view.View;

public class CircularIndicatorView extends View{//Checked
    private float value = 0;
    private float maxValue = 100;
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private RectF rectF;

    public CircularIndicatorView(Context context, AttributeSet attrs){
        super(context, attrs);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(40);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(40);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);

        rectF = new RectF();
    }

    public void setValue(float value){
        this.value = value;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if(value>=0 && value<40) progressPaint.setColor(Color.parseColor("#F44336")); // Red
        else if(value>=40 && value<50) progressPaint.setColor(Color.parseColor("#FFA500")); // Orange
        else if(value>=50 && value<70) progressPaint.setColor(Color.parseColor("#FFFF00")); // Yellow
        else if(value>=70 && value<85) progressPaint.setColor(Color.parseColor("#90EE90")); // Light Green
        else progressPaint.setColor(Color.parseColor("#006400")); // Dark Green

        float width = getWidth();
        float height = getHeight();
        float radius = Math.min(width, height)/2 - 50;

        float centerX = width / 2;
        float centerY = height / 2;

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        canvas.drawArc(rectF, 0, 360, false, backgroundPaint);
        float sweepAngle = (value / maxValue) * 360;
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);

        String valueText = String.format("%.2f", value);
        canvas.drawText(valueText + " / 100", centerX, centerY + 20, textPaint);
    }
}
