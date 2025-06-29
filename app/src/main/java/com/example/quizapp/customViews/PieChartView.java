package com.example.quizapp.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View{//Checked
    private int correctCount = 0;
    private int incorrectCount = 0;
    private Paint correctPaint;
    private Paint incorrectPaint;
    private Paint textPaint;
    private RectF pieBounds;

    public PieChartView(Context context, AttributeSet attrs){
        super(context, attrs);

        correctPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        correctPaint.setColor(Color.parseColor("#4CAF50")); // Green

        incorrectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        incorrectPaint.setColor(Color.parseColor("#F44336")); // Red

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);

        pieBounds = new RectF();
    }

    public void setAnswerCounts(int correct, int incorrect){
        this.correctCount = Math.max(0, correct);
        this.incorrectCount = Math.max(0, incorrect);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float padding = 50;
        float diameter = Math.min(width, height) - 2 * padding;
        float centerX = width / 2;
        float centerY = height / 2;

        pieBounds.set(padding, padding, padding + diameter, padding + diameter);

        int total = correctCount + incorrectCount;

        if (total == 0) {
            // No data to show, draw a full gray circle
            Paint emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            emptyPaint.setColor(Color.LTGRAY);
            canvas.drawArc(pieBounds, 0, 360, true, emptyPaint);
            return;
        }

//        float correctPercent = (correctCount / (float) total) * 100;
//        float incorrectPercent = (incorrectCount / (float) total) * 100;

        float correctAngle = (correctCount / (float) total) * 360;
        float incorrectAngle = (incorrectCount / (float) total) * 360;

        canvas.drawArc(pieBounds, 0, correctAngle, true, correctPaint);
        canvas.drawArc(pieBounds, correctAngle, incorrectAngle, true, incorrectPaint);

        drawSliceLabel(canvas, correctAngle/2, correctCount, total, centerX, centerY, diameter/2);
        drawSliceLabel(canvas, correctAngle + incorrectAngle/2, incorrectCount, total, centerX, centerY, diameter/2);
    }

    private void drawSliceLabel(Canvas canvas, float angle, int count, int total, float centerX, float centerY, float radius){
        float labelRadius;
        if(count == 0) return;

        double radians = Math.toRadians(angle);
        if(count == total) labelRadius = 0;
        else labelRadius = (float) (radius*0.5);

        float x = (float) (centerX + labelRadius * Math.cos(radians));
        float y = (float) (centerY + labelRadius * Math.sin(radians));

        canvas.drawText(String.valueOf(count), x, y, textPaint);
    }
}
