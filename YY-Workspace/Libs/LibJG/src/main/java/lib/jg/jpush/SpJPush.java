package lib.jg.jpush;

import android.content.Context;

import java.util.Observable;

import lib.ys.AppEx;
import lib.ys.util.sp.SpBase;

/**
 * @auther yuansui
 * @since 2017/5/4
 */

public class SpJPush extends SpBase {

    private static final String KSpJPush = "sp_push";

    private static SpJPush mInst = null;

    public interface SpJPushKey {
        String KJPushRegisterId = "j_register_id";
        String KKeyAlias = "j_alias";
        String KIsRegister = "is_register";
    }

    private SpJPush(Context context, String fileName) {
        super(context, fileName);
    }

    public synchronized static SpJPush inst() {
        if (mInst == null) {
            mInst = new SpJPush(AppEx.getContext(), KSpJPush);
        }
        return mInst;
    }

    @Override
    public void update(Observable o, Object arg) {
        mInst = null;
    }

    /**
     * 保存极光推送的RegisterId
     * @param id
     */
    public void jPushRegisterId(String id) {
        save(SpJPushKey.KJPushRegisterId, id);
    }

    /**
     * 获取RegisterId
     * @return
     */
    public String registerId() {
        return getString(SpJPushKey.KJPushRegisterId);
    }

    /**
     * 保存是否绑定极光推送
     * @param state
     */
    public void jPushIsRegister(boolean state) {
        save(SpJPushKey.KIsRegister, state);
    }

    /**
     * 获取是否需要绑定
     * @return
     */
    public boolean needRegisterJP() {
        return !getBoolean(SpJPushKey.KIsRegister);
    }

}
