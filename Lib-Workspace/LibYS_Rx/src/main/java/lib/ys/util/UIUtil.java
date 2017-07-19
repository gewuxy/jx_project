package lib.ys.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.StringRes;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import lib.ys.AppEx;
import lib.ys.ConstantsEx;

@SuppressWarnings("deprecation")
public class UIUtil {

    private static final int KInvalidValue = ConstantsEx.KInvalidValue;
    private static final float KHalfVal = 0.5f;

    private static Integer sStatusBarHeight = null;
    private static PaintFlagsDrawFilter sDrawFilter = null;

    public static int dpToPx(int dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) ((dpValue * scale) + KHalfVal);
    }

    public static int dpToPx(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) ((dpValue * scale) + KHalfVal);
    }

    public static int pxToDp(float pxValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + KHalfVal) + 1;
    }

    public static void setCanvasAntialias(Canvas canvas) {
        // 设置canvas抗锯齿
        if (canvas.getDrawFilter() == null) {
            if (sDrawFilter == null) {
                sDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            }
            canvas.setDrawFilter(sDrawFilter);
        }
    }

    public static void clearCanvasAntialias(Canvas canvas) {
        canvas.setDrawFilter(null);
    }

    public static void clearCanvas(Canvas canvas) {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    /**
     * 设置activity的window全屏显示, 主要适用在dialog样式的
     *
     * @param activity
     */
    public static void setWindowToFullScreen(Activity activity) {
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay();
        setWindowAttr(activity, d.getWidth(), d.getHeight(), Gravity.CENTER);
    }

    /**
     * 设置activity的window宽匹配屏幕宽度
     *
     * @param activity
     * @param gravity  对齐方式. 比如 Gravity.CENTER
     */
    public static void setWindowWidthMatchScreen(Activity activity, int gravity) {
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay();
        setWindowAttr(activity, d.getWidth(), KInvalidValue, gravity);
    }

    public static void setWindowAttr(Activity activity, int width, int height, int gravity) {
        WindowManager.LayoutParams p = activity.getWindow().getAttributes();
        if (width != KInvalidValue) {
            p.width = width;
        }
        if (height != KInvalidValue) {
            p.height = height;
        }
        p.gravity = gravity;
        activity.getWindow().setAttributes(p);
    }

    /**
     * 设置空白处黑暗度
     *
     * @param amount 0-1.0, 0为全透明. 1为全黑
     */
    public static void setWindowDimAmount(Window win, float amount) {
        WindowManager.LayoutParams params = win.getAttributes();
        params.dimAmount = amount;
        win.setAttributes(params);
        win.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    /**
     * 设置使用沉浸式通知栏, 4.4特性
     * @param window
     */
    public static void setFlatBar(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public static int computeStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取状态栏高度
     *
     * @return 未经缩放处理的原始px
     */
    public static int getStatusBarHeight(Context context) {
        if (sStatusBarHeight == null) {
            sStatusBarHeight = computeStatusBarHeight(context);
        }
        return sStatusBarHeight;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void setOverScrollNever(ListView lv) {
        if (DeviceUtil.getBrand().equals(ConstantsEx.KBrandMeiZu)) {
            // 取消魅族的下拉悬停功能
            try {
                lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
            } catch (Throwable e) {
            }
        }
    }

    public static void setTvTextIfExist(TextView tv, CharSequence text) {
        if (tv != null) {
            tv.setText(text);
        }
    }

    public static void setTvTextIfExist(TextView tv, @StringRes int id) {
        if (tv != null) {
            tv.setText(id);
        }
    }

    private static Context getContext() {
        return AppEx.getContext();
    }
}
