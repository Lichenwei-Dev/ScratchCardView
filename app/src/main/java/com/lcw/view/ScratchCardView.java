package com.lcw.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 刮刮卡(体验版)
 * Create by: chenwei.li
 * Date: 2017/7/21
 * Time: 下午11:39
 * Email: lichenwei.me@foxmail.com
 */

public class ScratchCardView extends View {


    private Paint mPaint;
    private Path mPath;
    private Canvas mCanvas;

    private Bitmap mBackGroundBitmap;//背景图(图案)
    private Bitmap mForeGroundBitmap;//前景图(灰色)

    private int mLastX;
    private int mLastY;

    public ScratchCardView(Context context) {
        this(context, null);
    }

    public ScratchCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScratchCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化操作
     */
    private void init() {

        mPaint = new Paint();
        mPaint.setAlpha(0);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(80);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        mPath = new Path();
        //背景图
        mBackGroundBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.background);
        //创建一个和背景图大小一致的Bitmap对象作为装载画布
        mForeGroundBitmap = Bitmap.createBitmap(mBackGroundBitmap.getWidth(), mBackGroundBitmap.getHeight(), Config.ARGB_8888);
        //与Canvas进行绑定
        mCanvas = new Canvas(mForeGroundBitmap);
        //涂成灰色
        mCanvas.drawColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制背景层
        canvas.drawBitmap(mBackGroundBitmap, 0, 0, null);
        //绘制前景层
        canvas.drawBitmap(mForeGroundBitmap, 0, 0, null);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) event.getX();
                mLastY = (int) event.getY();
                mPath.moveTo(mLastX, mLastY);
                break;
            case MotionEvent.ACTION_MOVE:
                mLastX = (int) event.getX();
                mLastY = (int) event.getY();
                mPath.lineTo(mLastX, mLastY);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }

        mCanvas.drawPath(mPath, mPaint);
        invalidate();

        return true;
    }
}
