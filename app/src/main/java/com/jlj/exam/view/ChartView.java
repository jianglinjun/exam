package com.jlj.exam.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.CorrectionInfo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.jlj.exam.entity.Data;
import com.jlj.exam.entity.ResultEntity;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends View {

    private final int OFFSET_SPACE_YEAR = 200;
    private final int TOP_POINT_Y = 50;
    private final int SPACE_VALUE_Y = 10;

    private int zero_point_x = 0;
    private int zero_point_y = 0;
    private int max_point_x = 0;
    private int y_length = 0;
    private int markSize = 0;
    private int max_mark_value_y = 0;

    private Paint basePaint = new Paint();
    private Path basePath = new Path();
    private ResultEntity entity = null;
    private List<Coordinate> dropYearList = new ArrayList<>();
    private ClickCallback callback = null;

    public ChartView(Context context) {
        super(context);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void fill(ResultEntity entity, ClickCallback callback){
        this.entity = entity;
        this.callback = callback;
        basePaint.setAntiAlias(true);

        requestLayout();
    }

    private void init(){
        //原点坐标
        zero_point_x = 100;
        zero_point_y = (int) (getMeasuredHeight() * 0.9);
        //x轴最大值
        max_point_x = (int) (getMeasuredWidth() * 0.95) + OFFSET_SPACE_YEAR;
        //y轴长度
        y_length = zero_point_y - 50;
        //y轴标记数量
        markSize = (int) ((entity.getMaxValue() + SPACE_VALUE_Y) / SPACE_VALUE_Y);
        //y轴最大标记
        max_mark_value_y = markSize * SPACE_VALUE_Y;

        dropYearList.clear();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(entity != null && !entity.getRecords().isEmpty()){
            super.onMeasure(MeasureSpec.makeMeasureSpec(OFFSET_SPACE_YEAR * entity.getRecords().size() + 1, MeasureSpec.EXACTLY), heightMeasureSpec);
            init();
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                checkTouch(event.getX(), event.getY());
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawPath(getYLine(), getYPaint());
        canvas.drawPath(getXLine(), getYPaint());
        drawXMark(canvas);
        drawYMark(canvas);
        canvas.restore();

        super.onDraw(canvas);
    }

    private void checkTouch(float touchX, float touchY){
        if(callback != null){
            for(int i = 0 ; i < dropYearList.size() ; i++){
                Coordinate coordinate = dropYearList.get(i);
                if(coordinate.contain(touchX, touchY)){
                    callback.onClicked(coordinate.getData());
                    break;
                }
            }
        }
    }

    private Paint getYPaint(){
        basePaint.setStyle(Paint.Style.STROKE);
        basePaint.setStrokeWidth(2);
        basePaint.setColor(Color.BLUE);

        return basePaint;
    }

    private Path getYLine(){
        basePath.reset();
        basePath.moveTo(zero_point_x, TOP_POINT_Y);
        basePath.lineTo(zero_point_x, zero_point_y);

        return basePath;
    }

    private Path getXLine(){
        basePath.reset();
        basePath.moveTo(zero_point_x, zero_point_y);
        basePath.lineTo(max_point_x, zero_point_y);

        return basePath;
    }

    private void drawXMark(Canvas canvas){
        basePaint.reset();
        basePaint.setStrokeWidth(1);
        basePaint.setTextSize(getTextSize());
        basePaint.setTextAlign(Paint.Align.CENTER);
        float offset_textHeight = basePaint.ascent() + basePaint.descent();
        float offset_x = 50;

        Path tempPath = new Path();
        tempPath.moveTo(zero_point_x, zero_point_y);

        for(int i = 0 ; i < entity.getRecords().size() ; i++){
            basePaint.setColor(Color.BLACK);
            basePaint.setStyle(Paint.Style.FILL);
            basePaint.setStrokeWidth(1);

            Data data = entity.getRecords().get(i);
            String text = data.getYear();
            float x_point = zero_point_x + offset_x + i * OFFSET_SPACE_YEAR;
            float y_point = zero_point_y - offset_textHeight + 20;
            canvas.drawText(text, x_point, y_point, basePaint);

            basePaint.setColor(Color.RED);
            int value_y = (int) (zero_point_y - ((data.getVolume_of_mobile_data() / max_mark_value_y) * zero_point_y));
            canvas.drawCircle(x_point, value_y, 10, basePaint);

            //连线
            basePaint.setColor(Color.RED);
            basePaint.setStyle(Paint.Style.STROKE);
            basePaint.setStrokeWidth(3);
            if(i == 0){
                tempPath.moveTo(x_point, value_y);
            }else{
                tempPath.lineTo(x_point, value_y);
            }
            canvas.drawPath(tempPath, basePaint);

            //标记并记录可点击位置
            if(data.isDrop()){
                Coordinate coordinate = new Coordinate();
                coordinate.setData(data);
                coordinate.setX((int) x_point);
                coordinate.setY(value_y);
                dropYearList.add(coordinate);

                basePaint.setColor(Color.BLUE);
                basePaint.setStyle(Paint.Style.STROKE);
                basePaint.setStrokeWidth(4);
                canvas.drawCircle(x_point, value_y, 15, basePaint);
            }
        }

    }

    private void drawYMark(Canvas canvas){
        int yAxisSpace = y_length / markSize;

        basePaint.reset();
        basePaint.setStrokeWidth(1);
        basePaint.setColor(Color.BLACK);
        basePaint.setTextSize(getTextSize());
        basePaint.setTextAlign(Paint.Align.RIGHT);
        float offset_textHeight = (basePaint.ascent() + basePaint.descent()) / 2;

        for(int i = 0 ; i <= markSize ; i++){
            String text = (i * SPACE_VALUE_Y) + "";
            float y_point = zero_point_y - i * yAxisSpace - offset_textHeight;
            canvas.drawText(text, zero_point_x - 10, y_point, basePaint);
        }
    }

    private int getTextSize(){
        double d = getResources().getDisplayMetrics().density;
        return (int) (14 * d + 0.5);
    }

    static class Coordinate{
        private final int OFFSET_TOUCH_POINT = 20;
        private int x;
        private int y;
        private Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public boolean contain(float touchX, float touchY){
            int max_x = x + OFFSET_TOUCH_POINT;
            int min_x = x - OFFSET_TOUCH_POINT;
            int max_y = y + OFFSET_TOUCH_POINT;
            int min_y = y - OFFSET_TOUCH_POINT;
            if(min_x <= touchX && touchX <= max_x
                && min_y <= touchY && touchY <= max_y){
                return true;
            }

            return false;
        }
    }

    public interface ClickCallback{
        void onClicked(Data data);
    }
}
