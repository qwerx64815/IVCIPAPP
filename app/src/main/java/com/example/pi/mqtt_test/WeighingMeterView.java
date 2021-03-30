package com.example.pi.mqtt_test;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class WeighingMeterView extends View {
    //畫筆(白色圓弧、藍色圓弧、秤重數據、刻度數據)
    private Paint whiteArcPaint, blueArcPaint, weightDataPaint, scaleDataPaint;
    private RectF oval; //圓弧矩形範圍
    private float currentData; //當前秤重數據
    private float sweepAngle = 240; //圓弧經過的角度範圍
    private String StartData = "", MiddleData = "", WeighingData = "";
    String Type, EndData = "";

    protected void setType(String type) { Type = type; }

    public WeighingMeterView(Context context) {
        super(context);
        Init();
    }

    public WeighingMeterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    private void Init() {
        Type = "TEMPFISH";

        whiteArcPaint = new Paint();
        whiteArcPaint.setAntiAlias(true);
        whiteArcPaint.setColor(Color.WHITE); //未填滿的顏色
        whiteArcPaint.setStyle(Paint.Style.STROKE);
        whiteArcPaint.setStrokeWidth(10);

        blueArcPaint = new Paint();
        blueArcPaint.setAntiAlias(true);
        blueArcPaint.setColor(Color.GREEN);
        //blueArcPaint.setColor(Color.parseColor("#3a84f4")); //填滿的顏色
        blueArcPaint.setStyle(Paint.Style.STROKE);
        blueArcPaint.setStrokeWidth(10);
        blueArcPaint.setShadowLayer((float) 10, (float) 10, (float) 10, Color.parseColor("#99000000"));

        weightDataPaint = new Paint();
        weightDataPaint.setColor(Color.parseColor("#FF1493")); //資料值的顏色
        weightDataPaint.setTextSize(30);
        //weightDataPaint.setStyle(Paint.Style.STROKE); //字的樣式

        scaleDataPaint = new Paint();
        scaleDataPaint.setColor(Color.parseColor("#00008B")); //字的顏色
        scaleDataPaint.setTextSize(28);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = MeasureSpec.getSize(widthMeasureSpec);
        //int height = MeasureSpec.getSize(heightMeasureSpec);

        whiteArcPaint.setStrokeWidth(w * (float) 0.1);
        blueArcPaint.setStrokeWidth(w * (float) 0.12);

        oval = new RectF(
                w * (float) 0.2,
                w * (float) 0.2,
                w * (float) 0.8,
                w * (float) 0.8);

        setMeasuredDimension(w, w);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        SetChartRange();
        drawWhiteArc(canvas);
        drawBlueArc(canvas);
        drawArrow(canvas);
        drawWeightingData(canvas);
        drawScaleData(canvas);
    }

    private void SetChartRange() {
        StartData = ""; MiddleData = ""; WeighingData = "";
        switch (Type) {
            /* 水族部分 */
            case "TEMPFISH":
                StartData = "0";
                EndData = "40";
                MiddleData = "20";
                WeighingData = "溫度(Temp)";
                break;
            case "TDS":
                StartData = "0";
                EndData = "400";
                MiddleData = "200";
                WeighingData = "總溶解固體(TDS)";
                break;
            case "PH":
                StartData = "0";
                EndData = "14";
                MiddleData = "7";
                WeighingData = "酸鹼值(PH)";
                break;
            case "ORP":
                StartData = "0";
                EndData = "800";
                MiddleData = "400";
                WeighingData = "氧化還原值(ORP)";
                break;
            case "DO":
                StartData = "0";
                EndData = "15";
                MiddleData = "7.5";
                WeighingData = "溶氧量(DO)";
                break;

            /* 居家部分 */
            case "TEMPHOME":
                StartData = "0";
                EndData = "40";
                MiddleData = "20";
                WeighingData = "溫度(Temp)";
                break;
            case "HUMIDITY":
                StartData = "0";
                EndData = "100";
                MiddleData = "50";
                WeighingData = "濕度(Humidity)";
                break;
            case "HCHO":
                StartData = "0";
                EndData = "1";
                MiddleData = "0.5";
                WeighingData = "甲醛(HCHO)";
                break;
            case "PM1.0":
                StartData = "0";
                EndData = "500";
                MiddleData = "250";
                WeighingData = "超細懸浮微粒(PM1.0)";
                break;
            case "PM2.5":
                StartData = "0";
                EndData = "500";
                MiddleData = "250";
                WeighingData = "細懸浮微粒(PM2.5)";
                break;
            case "PM10":
                StartData = "0";
                EndData = "500";
                MiddleData = "250";
                WeighingData = "懸浮微粒(PM10)";
                break;
        }

    }

    private void drawWhiteArc(Canvas canvas) {
        canvas.save();
        canvas.drawArc(oval, 150, sweepAngle, false, whiteArcPaint);
    }

    private void drawBlueArc(Canvas canvas) { //藍色填滿部分
        canvas.save();
        switch (Type) {
            /* 水族部分 */
            case "TEMPFISH":
                canvas.drawArc(
                        oval, 150,
                        sweepAngle *
                                (currentData + (1 / 1000)) / Float.parseFloat(EndData),
                        false, blueArcPaint);
                break;
            case "TDS":
                canvas.drawArc(
                        oval, 150,
                        sweepAngle *
                                (currentData + (1 / 1000)) / Float.parseFloat(EndData),
                        false, blueArcPaint);
                break;
            case "PH":
                canvas.drawArc(
                        oval, 150,
                        (sweepAngle *
                                (currentData + (1 / 1000)) / Float.parseFloat(EndData)),
                        false, blueArcPaint);
                break;
            case "ORP":
                canvas.drawArc(
                        oval, 150,
                        sweepAngle *
                                (currentData + (1 / 1000)) / Float.parseFloat(EndData),
                        false, blueArcPaint);
                break;
            case "DO":
                canvas.drawArc(
                        oval, 150,
                        sweepAngle *
                                (currentData + (1 / 1000)) / Float.parseFloat(EndData),
                        false, blueArcPaint);
                break;

            /* 居家部分 */
            case "TEMPHOME":
                canvas.drawArc(
                        oval, 150,
                        sweepAngle *
                                (currentData + (1 / 1000)) / Float.parseFloat(EndData),
                        false, blueArcPaint);
                break;
            case "HUMIDITY":
                canvas.drawArc(
                        oval, 150,
                        sweepAngle *
                                (currentData + (1 / 1000)) / Float.parseFloat(EndData),
                        false, blueArcPaint);
                break;
            case "HCHO":
                canvas.drawArc(
                        oval, 150,
                        sweepAngle *
                                (currentData + (1 / 1000)) * Float.parseFloat(EndData),
                        false, blueArcPaint);
                break;
            case "PM1.0":
                canvas.drawArc(
                        oval, 150,
                        sweepAngle *
                                (currentData + (1 / 1000)) / Float.parseFloat(EndData),
                        false, blueArcPaint);
                break;
            case "PM2.5":
                canvas.drawArc(
                        oval, 150,
                        sweepAngle *
                                (currentData + (1 / 1000)) / Float.parseFloat(EndData),
                        false, blueArcPaint);
                break;
            case "PM10":
                canvas.drawArc(
                        oval, 150,
                        sweepAngle *
                                (currentData + (1 / 1000)) / Float.parseFloat(EndData),
                        false, blueArcPaint);
                break;
        }
    }

    //指針部分
    private void drawArrow(Canvas canvas) {
        canvas.save();
        Bitmap oldBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow);
        int width = oldBitmap.getWidth();
        int height = oldBitmap.getHeight();
        int newWidth = (int) (getWidth() * 0.15);
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newWidth) / height;
        Matrix matrix = new Matrix();
        //TODO 下面这两个顺序不能变
        switch (Type) {
            /* 水族部分 */
            case "TEMPFISH":
                matrix.setRotate(
                        -165 + (sweepAngle * currentData / Float.parseFloat(EndData)),
                        oldBitmap.getWidth() / 2,
                        oldBitmap.getHeight() / 2);
                break;
            case "TDS":
                matrix.setRotate(
                        -165 + (sweepAngle * (currentData) / Float.parseFloat(EndData)),
                        oldBitmap.getWidth() / 2,
                        oldBitmap.getHeight() / 2);
                break;
            case "PH":
                matrix.setRotate(
                        -165 + (sweepAngle * (currentData) / Float.parseFloat(EndData)),
                        oldBitmap.getWidth() / 2,
                        oldBitmap.getHeight() / 2);
                break;
            case "ORP":
                matrix.setRotate(
                        -165 + (sweepAngle * (currentData) / Float.parseFloat(EndData)),
                        oldBitmap.getWidth() / 2,
                        oldBitmap.getHeight() / 2);
                break;
            case "DO":
                matrix.setRotate(
                        -165 + (sweepAngle * currentData / Float.parseFloat(EndData)),
                        oldBitmap.getWidth() / 2,
                        oldBitmap.getHeight() / 2);
                break;

            /* 居家部分 */
            case "TEMPHOME":
                matrix.setRotate(
                        -165 + (sweepAngle * currentData / Float.parseFloat(EndData)),
                        oldBitmap.getWidth() / 2,
                        oldBitmap.getHeight() / 2);
                break;
            case "HUMIDITY":
                matrix.setRotate(
                        -165 + (sweepAngle * currentData / Float.parseFloat(EndData)),
                        oldBitmap.getWidth() / 2,
                        oldBitmap.getHeight() / 2);
                break;
            case "HCHO":
                matrix.setRotate(
                        -165 + (sweepAngle * currentData / Float.parseFloat(EndData)),
                        oldBitmap.getWidth() / 2,
                        oldBitmap.getHeight() / 2);
                break;
            case "PM1.0":
                matrix.setRotate(
                        -165 + (sweepAngle * currentData / Float.parseFloat(EndData)),
                        oldBitmap.getWidth() / 2,
                        oldBitmap.getHeight() / 2);
                break;
            case "PM2.5":
                matrix.setRotate(
                        -165 + (sweepAngle * currentData / Float.parseFloat(EndData)),
                        oldBitmap.getWidth() / 2,
                        oldBitmap.getHeight() / 2);
                break;
            case "PM10":
                matrix.setRotate(
                        -165 + (sweepAngle * currentData / Float.parseFloat(EndData)),
                        oldBitmap.getWidth() / 2,
                        oldBitmap.getHeight() / 2);
                break;
        }
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix, true);
        canvas.drawBitmap(
                newBitmap,
                (getWidth() / 2) - (newBitmap.getWidth() / 2),
                (getHeight() / 2) - (newBitmap.getHeight() / 2), whiteArcPaint
        );
        oldBitmap.recycle();
        newBitmap.recycle();
    }

    private void drawWeightingData(Canvas canvas) {
        canvas.save();
        Rect rect = new Rect();
        StringBuilder data = new StringBuilder().append(String.format("%.2f", currentData));
        switch (Type) {
            /* 水族部分 */
            case "TEMPFISH":
                data.append("°C");
                break;
            case "TDS":
                data.append("ppm");
                break;
            case "PH": //PH無單位
                break;
            case "ORP":
                data.append("mV");
                break;
            case "DO":
                data.append("mg/L");
                break;

            /* 居家部分 */
            case "TEMPHOME":
                data.append("°C");
                break;
            case "HUMIDITY":
                data.append("%");
                break;
            case "HCHO":
                data.append("mg/m³");
                break;
            case "PM1.0":
                data.append("ug/m³");
                break;
            case "PM2.5":
                data.append("ug/m³");
                break;
            case "PM10":
                data.append("ug/m³");
                break;
        }
        weightDataPaint.getTextBounds(data.toString(), 0, data.length(), rect);
        canvas.drawText(data.toString(),
                (getWidth() / 2) - (rect.width() / 2),
                (int) (getHeight() * (float) 0.4), weightDataPaint); //位置
    }

    private void drawScaleData(Canvas canvas) {
        canvas.save();
        Rect startScaleText = new Rect();
        scaleDataPaint.getTextBounds(StartData, 0, StartData.length(), startScaleText);
        int h = (int) (getWidth() * 0.3 * Math.cos(Math.PI / 6) / 2
                + getWidth() * 0.6 * 0.5 + getWidth() * 0.25),
                w = (int) ((getWidth() * 0.17 + getWidth() * 0.3 * 0.25)
                        - startScaleText.width() - getWidth() * 0.1);
        canvas.drawText(StartData, w, h, scaleDataPaint);
        canvas.save();

        Rect endScaleText = new Rect();
        scaleDataPaint.getTextBounds(EndData, 0, EndData.length(), endScaleText);
        w = (int) (getWidth() * 0.83 - getWidth() * 0.3 * 0.25 + getWidth() * 0.1);
        canvas.drawText(EndData, w, h, scaleDataPaint);
        canvas.save();

        Rect middleScaleText = new Rect();
        scaleDataPaint.getTextBounds(MiddleData, 0, MiddleData.length(), middleScaleText);
        w = getWidth() / 2 - middleScaleText.width() / 2;
        h = (int) (getWidth() * 0.12);
        canvas.drawText(MiddleData, w, h, scaleDataPaint);
        canvas.save();

        Rect weighingText = new Rect();
        scaleDataPaint.getTextBounds(WeighingData, 0, WeighingData.length(), weighingText);
        w = getWidth() / 2 - weighingText.width() / 2;
        h = (int) (getWidth() * 0.74);
        canvas.drawText(WeighingData, w, h, scaleDataPaint);
        canvas.save();
    }

    public void setPercentData(float data, TimeInterpolator interpolator) {
        switch (Type) {
            /* 水族部分 */
            case "TEMPFISH":
            case "DO":
                currentData = 0;
                break;
            case "TDS":
                currentData = -200;
                break;
            case "PH":
                currentData = -1;
                break;
            case "ORP":
                currentData = -1000;
                break;

            /* 居家部分 */
            case "TEMPHOME":
            case "HUMIDITY":
            case "HCHO":
            case "PM1.0":
            case "PM2.5":
            case "PM10":
                currentData = 0;
                break;
        }
        float AnimatorEndValue = data;
        if(data >= Float.parseFloat(EndData)) AnimatorEndValue = Float.parseFloat(EndData);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, AnimatorEndValue);
        valueAnimator.setDuration((long) (Math.abs(currentData - data) * 2));
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            float value = (float) valueAnimator1.getAnimatedValue();
            currentData = ((value * 10)) / 10;
            switch (Type) {
                case "TEMPFISH":
                    if (currentData < 22 || currentData > 28)
                        blueArcPaint.setColor(Color.parseColor("#FF7275"));
                    else blueArcPaint.setColor(Color.parseColor("#75FF7F"));
                    break;
                case "TDS":
                    if (currentData < 100 || currentData > 300)
                        blueArcPaint.setColor(Color.parseColor("#FF7275"));
                    else blueArcPaint.setColor(Color.parseColor("#75FF7F"));
                    break;
                case "PH":
                    if (currentData < 6.5 || currentData > 8.5)
                        blueArcPaint.setColor(Color.parseColor("#FF7275"));
                    else blueArcPaint.setColor(Color.parseColor("#75FF7F"));
                    break;
                case "ORP":
                    if (currentData < 230 || currentData > 360)
                        blueArcPaint.setColor(Color.parseColor("#FF7275"));
                    else blueArcPaint.setColor(Color.parseColor("#75FF7F"));
                    break;
                case "DO":
                    if (currentData < 6 || currentData > 8)
                        blueArcPaint.setColor(Color.parseColor("#FF7275"));
                    else blueArcPaint.setColor(Color.parseColor("#75FF7F"));
                    break;

                /* 居家部分 */
                case "TEMPHOME":
                    if (currentData < 22 || currentData > 28)
                        blueArcPaint.setColor(Color.parseColor("#FF7275"));
                    else blueArcPaint.setColor(Color.parseColor("#75FF7F"));
                    break;
                case "HUMIDITY":
                    if (currentData < 40 || currentData > 70)
                        blueArcPaint.setColor(Color.parseColor("#FF7275"));
                    else blueArcPaint.setColor(Color.parseColor("#75FF7F"));
                    break;
                case "HCHO":
                    if (currentData > 0.08)
                        blueArcPaint.setColor(Color.parseColor("#FF7275"));
                    else blueArcPaint.setColor(Color.parseColor("#75FF7F"));
                    break;
                case "PM1.0":
                    if (currentData > 52)
                        blueArcPaint.setColor(Color.parseColor("#FF7275"));
                    else blueArcPaint.setColor(Color.parseColor("#75FF7F"));
                    break;
                case "PM2.5":
                    if (currentData > 35.5)
                        blueArcPaint.setColor(Color.parseColor("#FF7275"));
                    else blueArcPaint.setColor(Color.parseColor("#75FF7F"));
                    break;
                case "PM10":
                    if (currentData > 126)
                        blueArcPaint.setColor(Color.parseColor("#FF7275"));
                    else blueArcPaint.setColor(Color.parseColor("#75FF7F"));
                    break;
            }
            invalidate();
        });
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.start();
    }
}
