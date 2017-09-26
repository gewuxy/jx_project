package lib.yy.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PLMediaPlayer.OnCompletionListener;
import com.pili.pldroid.player.PLMediaPlayer.OnPreparedListener;
import com.pili.pldroid.player.widget.PLVideoView;

/**
 * @auther : GuoXuan
 * @since : 2017/6/29
 */
public class PlayerView extends View implements
        OnPreparedListener,
        OnCompletionListener {

    private static final String TAG = PlayerView.class.getSimpleName();
    private PLVideoView mPLVideoView;

    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PlayerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);

        init();
    }

    private void init() {
        mPLVideoView = new PLVideoView(getContext());
        mPLVideoView.setOnPreparedListener(this);
        mPLVideoView.setOnCompletionListener(this);
    }

    /**
     * 自动启动播放
     * 在调用 prepareAsync 或者 setVideoPath 之后自动启动播放, 无需调用 start()
     * @param path
     */
    public void setPathAutomatic(String path) {
        mPLVideoView.setVideoPath(path);
    }

    /**
     *
     * 默认值是 : 1
     * @param path
     */
    public void setPath(String path) {
        //  请在开始播放之前配置
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);
        mPLVideoView.setAVOptions(options);
        mPLVideoView.setVideoPath(path);
    }

    /**
     * 该对象用于监听播放器的 prepare 过程, 该过程主要包括 : 创建资源、建立连接、请求码流等等
     * 当 prepare 完成后, SDK会回调该对象的 onPrepared 接口, 下一步则可以调用播放器的 start() 启动播放
     * @param plMediaPlayer
     */
    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {

    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {

    }
}
