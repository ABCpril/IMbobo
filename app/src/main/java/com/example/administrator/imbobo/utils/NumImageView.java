package com.example.administrator.imbobo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Leon on 2018/12/9.
 * Functions: app内部 的角标 NumberImageView
 */
@SuppressLint("AppCompatCustomView")
public class NumImageView extends ImageView {

    //要显示的数量数量
    private String num = "0";
    //红色圆圈的半径
    private float radius;
    //圆圈内数字的半径
    private float textSize;
    //右边和上边内边距
    private int paddingRight;
    private int paddingTop;

    private int lengthOfASide;

    public NumImageView(Context context) {
        super(context);
    }

    public NumImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //设置显示的数量
    public void setNum(String num) {
        this.num = num;
        //重新绘制画布
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!num.equals("0")) {
            //初始化半径-把图形当做一个正方行
            lengthOfASide = Math.min(getHeight(),getWidth());
            //radius = getWidth() / 6; 原来方法
            radius = lengthOfASide / 6;
            //初始化字体大小
            //textSize = num < 10 ? radius + 5 : radius;
            textSize = radius + 5;
            //初始化边距
            paddingRight = getPaddingRight();
            paddingTop = getPaddingTop();
            //初始化画笔
            Paint paint;
            paint = new Paint();
            //设置抗锯齿
            paint.setAntiAlias(true);
            //设置颜色为红色
            paint.setColor(0xffff4444);
            //设置填充样式为充满
            paint.setStyle(Paint.Style.FILL);
            //画圆
            canvas.drawCircle(getWidth() - radius - paddingRight/2, radius + paddingTop/2, radius, paint);
            //设置颜色为白色
            paint.setColor(0xffffffff);
            //设置字体大小
            paint.setTextSize(textSize);
            //画数字
            canvas.drawText(num,
                    getWidth() - radius - textSize / 2 - paddingRight,
                    radius + textSize / 3 + paddingTop/2, paint);

//            canvas.drawText("" + (num < 99 ? num : 99),
//                    num < 10 ? getWidth() - radius - textSize / 4 - paddingRight/2
//                            : getWidth() - radius - textSize / 2 - paddingRight/2,
//                    radius + textSize / 3 + paddingTop/2, paint);
        }
    }

}
