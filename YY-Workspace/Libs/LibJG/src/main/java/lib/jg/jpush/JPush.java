package lib.jg.jpush;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import lib.ys.AppEx;
import lib.ys.YSLog;
import lib.ys.util.TextUtil;

/**
 * @author yuansui
 */
public class JPush {

    private static final String TAG = JPush.class.getSimpleName();

    private static JPush mInst;

    private Handler mHandler;
    private Context mContext;
    private int mWhat;

    private JPush() {
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                String id = (String) msg.obj;

                if (mContext == null) {
                    YSLog.e(TAG, "handleMessage context = null");
                    mContext = AppEx.getContext();

                    mHandler.sendMessageDelayed(mHandler.obtainMessage(0, id), 1000);
                    return;
                }

                JPushInterface.setAlias(mContext, id, new TagAliasCallback() {

                    @Override
                    public void gotResult(int code, String alias, Set<String> tags) {
                        YSLog.d(TAG, "gotResult: code = " + code);
                        YSLog.d(TAG, "gotResult: alias = " + alias);

                        switch (code) {
                            case 0: {
                                SpPush.inst().save(SpPush.KKeyAlias, alias);
                            }
                            break;
                            case 6002: {
                                YSLog.d(TAG, "gotResult() " + "失败");
                                // 延迟 30 秒来调用 Handler 设置别名
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(0, alias), 1000 * 30);
                            }
                            break;
                            default: {
                                YSLog.d(TAG, "gotResult: error code = " + code);
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(0, alias), 1000 * 30);
                            }
                            break;
                        }
                    }
                });
            }
        };
    }

    public static JPush inst() {
        if (mInst == null) {
            mInst = new JPush();
        }
        return mInst;
    }

    public void init(boolean isDebug) {
        mContext = AppEx.getContext();

        JPushInterface.setDebugMode(isDebug); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(mContext); // 初始化 JPush
    }

    public void login(String id) {
        if (TextUtil.isEmpty(SpPush.inst().getString(SpPush.KKeyAlias))) {
            mHandler.sendMessage(mHandler.obtainMessage(mWhat, id));
        }
    }

    /**
     * 在已登录的情况下再次登录其他的号码
     *
     * @param id
     */
    public void changeLogin(String id) {
        SpPush.inst().clear();
        mHandler.sendMessage(mHandler.obtainMessage(mWhat, id));
    }

    public void logout() {
        if (mHandler.hasMessages(mWhat)) {
            mHandler.removeMessages(mWhat);
        }
        mWhat++;
        mHandler.sendMessage(mHandler.obtainMessage(mWhat, ""));
        SpPush.inst().clear();
    }
}
