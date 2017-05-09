package lib.ys.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.os.Build.VERSION_CODES;

public class DrawUtil {

    /**
     * 根据X方向绘制文本
     *
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param paint
     * @param align  Paint.Align
     */
    public static void drawTextByAlignX(Canvas canvas, String text, float x, float y, Paint paint, Align align) {
        float newY = y + paint.getTextSize();
        switch (align) {
            case LEFT:
                drawTextInLeftX(canvas, text, x, newY, paint);
                break;
            case CENTER:
                drawTextInCenterX(canvas, text, x, newY, paint);
                break;
            case RIGHT:
                drawTextInRightX(canvas, text, x, newY, paint);
                break;
            default:
                break;
        }
    }

    private static void drawTextInLeftX(Canvas canvas, String text, float x, float y, Paint paint) {
        canvas.drawText(text, x, y, paint);
    }

    private static void drawTextInCenterX(Canvas canvas, String text, float x, float y, Paint paint) {
        canvas.drawText(text, x - paint.measureText(text) / 2, y, paint);
    }

    private static void drawTextInRightX(Canvas canvas, String text, float x, float y, Paint paint) {
        canvas.drawText(text, x - paint.measureText(text), y, paint);
    }

    /**
     * 根据中心点绘制文本
     *
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param p
     */
    public static void drawTextInCenterXY(Canvas canvas, String text, float x, float y, Paint p) {
        float halfSize = p.measureText(text) / 2;
        canvas.drawText(text, x - halfSize, y + halfSize, p);
    }

    /**
     * 只调整X的对齐方式, Y不变
     *
     * @param canvas
     * @param bmp
     * @param x
     * @param y
     * @param paint
     * @param align
     */
    public static void drawBmpByAlignX(Canvas canvas, Bitmap bmp, float x, float y, Paint paint, Align align) {
        switch (align) {
            case LEFT:
                drawBmpInLeftX(canvas, bmp, x, y, paint);
                break;
            case CENTER:
                drawBmpInCenterX(canvas, bmp, x, y, paint);
                break;
            case RIGHT:
                drawBmpInRightX(canvas, bmp, x, y, paint);
                break;
            default:
                break;
        }
    }

    public static void drawBmpInCenterX(Canvas canvas, Bitmap bmp, float x, float y, Paint paint) {
        canvas.drawBitmap(bmp, x - bmp.getWidth() / 2, y, paint);
    }

    private static void drawBmpInLeftX(Canvas canvas, Bitmap bmp, float x, float y, Paint paint) {
        canvas.drawBitmap(bmp, x, y, paint);
    }

    private static void drawBmpInRightX(Canvas canvas, Bitmap bmp, float x, float y, Paint paint) {
        canvas.drawBitmap(bmp, x - bmp.getWidth(), y, paint);
    }

    /**
     * 把图片缩放后居中画在指定点, X和Y都居中
     *
     * @param bmp
     * @param canvas
     * @param x
     * @param y
     * @param paint
     * @param scaleW 1.0f表示不缩放
     * @param scaleH 1.0f表示不缩放
     */
    public static void drawBmpScaleInCenter(Canvas canvas, Bitmap bmp, float x, float y, Paint paint, float scaleW, float scaleH) {
        Matrix m = new Matrix();
        m.setScale(scaleW, scaleH);
        float newWidth = bmp.getWidth() * scaleW;
        float newHeight = bmp.getHeight() * scaleH;
        m.postTranslate(x - newWidth / 2, y - newHeight / 2);
        canvas.drawBitmap(bmp, m, paint);
    }

    /**
     * 在canvas上绘制圆角区域, 兼容旧版本
     *
     * @param canvas
     * @param paint
     * @param radius 半径
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public static void drawRoundRect(Canvas canvas, Paint paint, float radius, float left, float top, float right, float bottom) {
        RectF r = new RectF(left, top, right, bottom);
        drawRoundRect(canvas, paint, radius, r);
    }

    /**
     * 在canvas上绘制圆角区域, 兼容旧版本
     *
     * @param canvas
     * @param paint
     * @param radius
     * @param r
     */
    public static void drawRoundRect(Canvas canvas, Paint paint, float radius, RectF r) {
        if (DeviceUtil.getSDKVersion() < VERSION_CODES.LOLLIPOP) {
            Path path = new Path();
            path.addRoundRect(r, radius, radius, Direction.CW);
            canvas.drawPath(path, paint);
        } else {
            canvas.drawRoundRect(r, radius, radius, paint);
        }
    }

}
