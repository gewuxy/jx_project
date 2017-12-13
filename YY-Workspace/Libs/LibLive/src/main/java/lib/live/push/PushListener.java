package lib.live.push;

import android.os.Bundle;

/**
 * @auther : CaiXiang
 * @since : 2017/12/11
 */
abstract public class PushListener {

    /**
     * 推流失败回调
     */
    abstract protected void onPushFail();

    /**
     * 网络状态通知
     * @param var1
     */
    protected void onNetStatus(Bundle var1){}
}
