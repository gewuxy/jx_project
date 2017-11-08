package jx.csp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import jx.csp.R;
import lib.ys.fitter.DpFitter;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.res.ResLoader;

/**
 * @author CaiXiang
 * @since 2017/5/3
 */
public class ClipImgMaskView extends View {

    /**
     * 截取框的边框宽度
     */
    private final int KBorderWidth = DpFitter.dp(3);
    private final int KRadius = DpFitter.dp(140);

    private Paint mPaint;
    private Bitmap mMaskBmp;
    private Canvas mMaskCanvas;


    public ClipImgMaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ResLoader.getColor(R.color.white));
        mPaint.setStrokeWidth(KBorderWidth);
        mPaint.setStyle(Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int halfW = getMeasuredWidth() / 2;
        int halfH = getMeasuredHeight() / 2;

        // 需要创建一层画布来使用SRC_OUT模式, 不要会影响整张画布
        mMaskBmp = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
        mMaskCanvas = BmpUtil.createCanvas(mMaskBmp);
        mMaskCanvas.drawCircle(halfW, halfH, KRadius, new Paint());
        mMaskCanvas.drawColor(ResLoader.getColor(R.color.mask_bg), Mode.SRC_OUT);

        mMaskCanvas.drawCircle(halfW, halfH, KRadius, mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mMaskBmp, 0, 0, null);
    }
}
