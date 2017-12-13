package lib.live;

import android.os.Bundle;

/**
 * @auther : GuoXuan
 * @since : 2017/12/11
 */
abstract public class LiveListener {

    /**
     * 视频播放loading(如果能够恢复,之后会有BEGIN事件)
     * LOADING 时隐藏画面, BEGIN 时显示画面，会造成严重的画面闪烁
     * 因为PLAY_LOADING -> PLAY_BEGIN 的时间长短是不确定的
     * 推荐的做法是在视频播放画面上叠加一个半透明的 loading 动画
     */
    public void load() {}

    /**
     * 视频播放开始
     */
    public void begin() {}

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
