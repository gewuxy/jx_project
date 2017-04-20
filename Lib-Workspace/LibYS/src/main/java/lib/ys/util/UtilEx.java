package lib.ys.util;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AnimRes;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * 启动外部浏览器
     *
     * @param url
     */
    public static void startOuterBrowser(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * 获取调用市场评分的intent
     *
     * @return 如果没有, 返回null
     */
    public static Intent getMarketIntent() {
        Intent intent_market = new Intent(Intent.ACTION_VIEW);
        intent_market.setData(Uri.parse("market://details?id=" + AppEx.ct().getPackageName()));
        // 检测intent是否可用
        PackageManager packageManager = AppEx.ct().getPackageManager();
        List<ResolveInfo> shareAppList = packageManager.queryIntentActivities(intent_market, PackageManager.MATCH_DEFAULT_ONLY);
        if (shareAppList == null || shareAppList.size() == 0) {
            return null;
        } else {
            return intent_market;
        }
    }

    /**
     * 在主线程消息队列最后加载一个runnable
     *
     * @param r
     */
    public static void runOnUIThread(Runnable r) {
        new Handler(Looper.getMainLooper()).post(r);
    }

    public static void runOnUIThread(Runnable r, long delayMillis) {
        new Handler(Looper.getMainLooper()).postDelayed(r, delayMillis);
    }

    private static ExecutorService mThreadPool;

    /**
     * 在子线程run一个runnable
     *
     * @param r
     */
    public static void runOnSubThread(Runnable r) {
        if (mThreadPool == null) {
            mThreadPool = Executors.newCachedThreadPool();
        }
        mThreadPool.execute(r);
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
