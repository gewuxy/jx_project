package lib.yy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.view.View;

import lib.ys.fitter.DpFitter;

/**
 * @author CaiXiang
 * @since 2017/5/3
 */
public class ClipImageBorderView extends View {

    /**
     * 截取框的边框宽度
     */
    private int mBorderWidth = 3;

    /**
     * 边框的颜色
     */
    private int mBorderColor = Color.parseColor("#ffffff");

    private Paint mPaint;

    public ClipImageBorderView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        //设定截取框周边背景的颜色
        /*mPaint.setColor(Color.parseColor("#4a000000"));
        mPaint.setStyle(Style.FILL);*/



        //绘制截取框的外边框
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setStyle(Style.STROKE);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, DpFitter.dp(140), mPaint);

        canvas.save();
        canvas.drawColor(Color.parseColor("#4a000000"), Mode.SRC_OUT);
        canvas.restore();
    }

}
