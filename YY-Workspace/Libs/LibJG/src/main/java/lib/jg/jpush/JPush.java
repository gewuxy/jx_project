package lib.jg.jpush;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import lib.jg.jpush.SpJPush.SpJPushKey;
import lib.ys.AppEx;
import lib.ys.YSLog;

/**
 * @author yuansui
 */
public class JPush {

    private static final String TAG = JPush.class.getSimpleName();

    private static JPush mInst;

    private Handler mHandler;
    private Context mContext;
    private int mWhat;

    //bd90b96a0dc4ca3064782df216fa52ab
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
                                SpJPush.inst().save(SpJPushKey.KKeyAlias, alias);
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

    /**
     * 在已登录的情况下再次登录其他的号码
     *
     * @param id
     */
    public void changeLogin(String id) {
        SpJPush.inst().clear();
        mHandler.sendMessage(mHandler.obtainMessage(mWhat, id));
    }

    public void logout() {
        if (mHandler.hasMessages(mWhat)) {
            mHandler.removeMessages(mWhat);
        }
        mWhat++;
        mHandler.sendMessage(mHandler.obtainMessage(mWhat, ""));
    }

}
