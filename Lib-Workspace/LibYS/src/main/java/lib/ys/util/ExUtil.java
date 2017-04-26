package lib.ys.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.view.View;

import lib.ys.AppEx;
import lib.ys.ex.NavBar;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;

/**
 * 保留, 暂时无用, 因为去掉了list的fit header机制
 *
 * @author yuansui
 */
@Deprecated
public class ExUtil {

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    public static int getFitHeaderHeight(View v, Context context) {
        int h = NavBar.getConfig().getHeight();

        if (DeviceUtil.getSDKVersion() >= VERSION_CODES.KITKAT) {
            if (!v.getFitsSystemWindows() && AppEx.getConfig().isFlatBarEnabled()) {
                /**
                 * 有些底部布局的输入框在使用flat bar的时候会有adjustResize无效的bug,
                 * 所以会使用{@link View#setFitsSystemWindows(boolean)}来修复这个bug.
                 * 但是系统会自动给这个view加上statusBar高度的空白view,
                 * 所以需要判断如果系统加了我们就不加
                 */
                h += UIUtil.getStatusBarHeight(context);
            }
        }

        if (h == 0) {
            h = LayoutUtil.WRAP_CONTENT;
        }
        return h;
    }

    /**
     * 创造一个能适配titleBar和status bar高度的空白header
     *
     * @param context
     * @return
     */
    @TargetApi(VERSION_CODES.JELLY_BEAN)
    public static View createFitHeader(View v, Context context) {
        return ViewUtil.inflateSpaceViewPx(getFitHeaderHeight(v, context));
    }

    /**
     * 创造一个能适配titleBar和status bar高度的空白header
     *
     * @param height
     * @return
     */
    public static View createFitHeader(int height) {
        return ViewUtil.inflateSpaceViewPx(height);
    }
}
