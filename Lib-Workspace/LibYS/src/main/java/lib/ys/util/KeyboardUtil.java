package lib.ys.util;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import lib.ys.AppEx;

/**
 * 输入法的键盘管理
 */
public class KeyboardUtil {

    private static InputMethodManager mImm;

    static {
        mImm = (InputMethodManager) AppEx.ct().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static void hideFromView(View v) {
        hideFromWindow(v.getWindowToken());
        v.clearFocus();
    }

    public static void hideFromWindow(IBinder windowToken) {
        mImm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 触发开关, 输入法键盘显示相反状态, 即如果当前为显示则隐藏
     */
    public static void toggle() {
        mImm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 强制弹出输入法键盘(无法使用代码控制隐藏输入法)
     */
    public static void forceShow(View v) {
        mImm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    /**
     * * 弹出输入法键盘
     * PS: 前提是view已经通过requestFocus()或其他方式获取完焦点, 不然无效
     *
     * @param v
     */
    public static void showFromView(View v) {
        v.requestFocus();
        mImm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }

    public static boolean isActive(View v) {
        return mImm.isActive(v);
    }

    public static boolean isActive() {
        return mImm.isActive();
    }

    public static void restartInput(View v) {
        mImm.restartInput(v);
    }
}
