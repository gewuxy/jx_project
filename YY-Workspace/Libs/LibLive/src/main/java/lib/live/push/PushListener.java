package lib.live.push;

import android.os.Bundle;

/**
 * @auther : CaiXiang
 * @since : 2017/12/11
 */
public interface PushListener {

    /**
     * 推流失败回调
     */
    void onPushFail();

    /**
     * 网络状态通知
     * @param var1
     */
    void onNetStatus(Bundle var1);
}
