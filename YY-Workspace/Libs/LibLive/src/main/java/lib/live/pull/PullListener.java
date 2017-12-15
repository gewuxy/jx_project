package lib.live.pull;

/**
 * @auther : GuoXuan
 * @since : 2017/12/11
 */
public interface PullListener {

    /**
     * 视频播放loading(如果能够恢复,之后会有BEGIN事件)
     * LOADING 时隐藏画面, BEGIN 时显示画面，会造成严重的画面闪烁
     * 因为PLAY_LOADING -> PLAY_BEGIN 的时间长短是不确定的
     * 推荐的做法是在视频播放画面上叠加一个半透明的 loading 动画
     */
    void load();

    /**
     * 视频播放开始
     */
    void begin();

    void end();
}
