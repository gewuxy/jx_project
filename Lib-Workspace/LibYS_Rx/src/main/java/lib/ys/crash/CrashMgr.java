package lib.ys.crash;

import java.lang.Thread.UncaughtExceptionHandler;

import lib.ys.YSLog;
import lib.ys.util.UtilEx;

public class CrashMgr implements UncaughtExceptionHandler {

    public static final String TAG = CrashMgr.class.getSimpleName();

    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    private static CrashMgr mInst = null;

    private OnCrashListener mCrashListener;

    private CrashMgr() {
    }

    public static CrashMgr inst() {
        if (mInst == null) {
            mInst = new CrashMgr();
        }
        return mInst;
    }

    public void init(OnCrashListener l) {
        mCrashListener = l;

        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!mCrashListener.handleCrashException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                YSLog.e(TAG, e);
            }
            // 退出程序
            killSelf();
            System.exit(1);
        }
    }

    private void killSelf() {
        UtilEx.killProcess();
    }
}
