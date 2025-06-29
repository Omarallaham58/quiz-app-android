package com.example.quizapp.customViews;
import android.util.Log;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.quizapp.R;
import com.google.android.material.snackbar.Snackbar;

public class CourseHistogramView extends View {//Checked

    private int []grades = {0,0,0,0};
    private Paint paint1;
    private Paint paint2;
    private Paint paint3;
    private Paint paint4;
    private Paint borderPaint;
    private Paint textPaint;
    private boolean areFetched = false;

    public CourseHistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CourseHistogramView, 0, 0);
        a.recycle();

        paint1 = init_paint(Color.RED,75);
        paint2 = init_paint(Color.CYAN,75);
        paint3 = init_paint(Color.YELLOW,75);
        paint4 = init_paint(Color.GREEN,75);
        borderPaint = init_paint(Color.BLACK,25);
        textPaint = init_text_paint(Color.BLACK, 35);
    }

    private Paint init_paint(int color, int width) {
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        return paint;
    }

    private Paint init_text_paint(int color, float textSize) {
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER); // Set text alignment to center
        return paint;
    }

    public void setData(int[] grades) {
        this.grades = grades;
        areFetched = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int total = grades[0] + grades[1] + grades[2] + grades[3];
        if(areFetched && total==0)
            Snackbar.make(this, "No student has taken an exam in this course!", Snackbar.LENGTH_LONG).show();

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float baseLineY = (float) (5 * height / 8);
        float totalAvHeight = (float) (4 * height / 8);

        // base y-axis -  (fraction * total available height) - 15

        float height1 = baseLineY - (((float) grades[0] / total) * totalAvHeight) -15;
        float height2 = baseLineY - (((float) grades[1] / total) * totalAvHeight) -15;
        float height3 = baseLineY - (((float) grades[2] / total) * totalAvHeight) -15;
        float height4 = baseLineY - (((float) grades[3] / total) * totalAvHeight) -15;

        canvas.drawLine((float)width/8, baseLineY, (float)(7.5*width/8),baseLineY,borderPaint);     // x-axis
        canvas.drawLine((float)width/8, (float)0.5*height/8, (float)width/8, baseLineY, borderPaint); // y-axis

        canvas.drawLine((float)(2.5*width/8), baseLineY -15, (float)(2.5*width/8), height1, paint1);
        canvas.drawText("<25", (float)(2.5*width/8), baseLineY +70, textPaint);

        canvas.drawLine((float)(4*width/8), baseLineY-15, (float)(4*width/8), height2, paint2);
        canvas.drawText("26-50", (float)(4*width/8), baseLineY +70, textPaint);

        canvas.drawLine((float)(5.5*width/8), baseLineY -15, (float)(5.5*width/8), height3, paint3);
        canvas.drawText("51-75", (float)(5.5*width/8), baseLineY +70, textPaint);

        canvas.drawLine((float)(7*width/8), baseLineY -15, (float)(7*width/8), height4, paint4);
        canvas.drawText("76-100", (float)(7*width/8), baseLineY +70, textPaint);

        canvas.drawText("0%",(float)(0.5*width/8), (float)5*height/8, textPaint);
        canvas.drawText("25%",(float)(0.5*width/8), (float)4*height/8, textPaint);
        canvas.drawText("50%",(float)(0.5*width/8), (float)3*height/8, textPaint);
        canvas.drawText("75%",(float)(0.5*width/8), (float)2*height/8, textPaint);
        canvas.drawText("100%",(float)(0.5*width/8), (float)1*height/8, textPaint);

        canvas.drawText("x-axis: Grades",(float)(4*width/8), (float)6*height/8, textPaint);
        canvas.drawText("y-axis: % of Students",(float)(4*width/8), (float)6.5*height/8, textPaint);
    }

}


