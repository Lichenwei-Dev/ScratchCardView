package com.lcw.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 刮刮卡(完善版)
 * Create by: chenwei.li
 * Date: 2017/7/22
 * Time: 下午7:25
 * Email: lichenwei.me@foxmail.com
 */

public class ScratchCardView2 extends View {

    //处理文字
    private String mText = "恭喜您中奖啦!!";
    private Paint mTextPaint;
    private Rect mRect;

    //处理图层
    private Paint mForePaint;
    private Path mPath;

    private Bitmap mBitmap;//加载资源文件
    private Canvas mForeCanvas;//前景图Canvas
    private Bitmap mForeBitmap;//前景图Bitmap

    //记录位置
    private int mLastX;
    private int mLastY;

    private volatile boolean isClear;//标志是否被清除


    public ScratchCardView2(Context context) {
        this(context, null);
    }

    public ScratchCardView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScratchCardView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

        mRect = new Rect();
        mPath = new Path();

        //文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.GREEN);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(30);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mRect);

        //擦除画笔
        mForePaint = new Paint();
        mForePaint.setAntiAlias(true);
        mForePaint.setAlpha(0);
        mForePaint.setStrokeCap(Paint.Cap.ROUND);
        mForePaint.setStrokeJoin(Paint.Join.ROUND);
        mForePaint.setStyle(Paint.Style.STROKE);
        mForePaint.setStrokeWidth(30);
        mForePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        //通过资源文件创建Bitmap对象
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.background);
        mForeBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //双缓冲,装载画布
        mForeCanvas = new Canvas(mForeBitmap);
        mForeCanvas.drawBitmap(mBitmap, 0, 0, null);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(mText, mForeBitmap.getWidth() / 2 - mRect.width() / 2, mForeBitmap.getHeight() / 2 + mRect.height() / 2, mTextPaint);
        if (!isClear) {
            canvas.drawBitmap(mForeBitmap, 0, 0, null);
        }
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
                new Thread(mRunnable).start();
                break;
            default:
                break;
        }

        mForeCanvas.drawPath(mPath, mForePaint);
        invalidate();
        return true;
    }


    /**
     * 开启子线程计算被擦除的像素点
     */
    private Runnable mRunnable = new Runnable() {
        int[] pixels;

        @Override
        public void run() {

            int w = mForeBitmap.getWidth();
            int h = mForeBitmap.getHeight();

            float wipeArea = 0;
            float totalArea = w * h;


            pixels = new int[w * h];
            /**
             * pixels      接收位图颜色值的数组
             * offset      写入到pixels[]中的第一个像素索引值
             * stride      pixels[]中的行间距个数值(必须大于等于位图宽度)。可以为负数
             * x          　从位图中读取的第一个像素的x坐标值。
             * y           从位图中读取的第一个像素的y坐标值
             * width    　　从每一行中读取的像素宽度
             * height 　　　读取的行数
             */
            mForeBitmap.getPixels(pixels, 0, w, 0, 0, w, h);

            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int index = i + j * w;
                    if (pixels[index] == 0) {
                        wipeArea++;
                    }
                }
            }


            if (wipeArea > 0 && totalArea > 0) {
                int percent = (int) (wipeArea * 100 / totalArea);
                if (percent > 50) {
                    isClear = true;
                    postInvalidate();
                }
            }

        }
    };
}
