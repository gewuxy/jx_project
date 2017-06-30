package lib.ys.util;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AnimRes;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lib.ys.AppEx;

/**
 * 一些零碎的方法
 */
public class UtilEx {
    /**
     * 把数字转换成int型(丢精度)
     *
     * @param value
     * @return
     */
    public static String toIntStyle(String value) {
        float moneyF = Float.valueOf(value);
        int money = (int) moneyF;
        return String.valueOf(money);
    }

    public static String md5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    public static void killProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 复制到粘贴板
     *
     * @param text
     */
    @SuppressWarnings("deprecation")
    public static void copyToClipboard(CharSequence text) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // API 11
            ClipboardManager copy = (ClipboardManager) AppEx.ct().getSystemService(Context.CLIPBOARD_SERVICE);
            copy.setText(text);
        } else {
            android.text.ClipboardManager copy = (android.text.ClipboardManager) AppEx.ct().getSystemService(Context.CLIPBOARD_SERVICE);
            copy.setText(text);
        }
    }

    /**
     * 在主线程消息队列最后加载一个runnable
     *
     * @param r
     */
    public static void runOnUIThread(Runnable r) {
        Observable.just(r)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Runnable::run);
    }

    public static void runOnUIThread(Runnable r, long delayMillis) {
        Observable.just(r)
                .delay(delayMillis, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Runnable::run);
    }

//    private static ExecutorService mThreadPool;

    /**
     * 在子线程run一个runnable
     *
     * @param r
     */
    public static void runOnSubThread(Runnable r) {
        // FIXME: 暂时去掉线程池
//        if (mThreadPool == null) {
//            mThreadPool = Executors.newCachedThreadPool();
//        }
        Observable.just(r)
                .observeOn(Schedulers.computation())
                .subscribe(Runnable::run);

    }

    public static void startActAnim(Activity activity, @AnimRes int enterAnim, @AnimRes int exitAnim) {
        activity.overridePendingTransition(enterAnim, exitAnim);
    }

    /**
     * FIXME: 在静态方法中获取类名
     *
     * @return
     * @deprecated 这只是一个代码示例, 不能真正调用, 用的时候复制粘贴到类里使用
     */
    private final static Class<?> getStaticClass() {
        String name = new Object() {
            public String getName() {
                String name = this.getClass().getName();
                return name.substring(0, name.lastIndexOf('$'));
            }
        }.getName();

        Class clz = null;
        try {
            clz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clz;
    }
}
