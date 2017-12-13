package lib.live.push;

import android.os.Bundle;

/**
 * @auther : CaiXiang
 * @since : 2017/12/11
 */
abstract public class PushListener {

    /**
     * 推流事件通知
     * @param var1
     * @param var2
     */
    public void onPushEvent(int var1, Bundle var2) {}

    /**
     * 网络状态通知
     * @param var1
     */
    public void onNetStatus(Bundle var1){}
}
