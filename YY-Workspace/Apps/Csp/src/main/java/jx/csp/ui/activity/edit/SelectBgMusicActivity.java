package jx.csp.ui.activity.edit;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.Extra;
import jx.csp.R;
import jx.csp.adapter.MusicAdapter;
import jx.csp.model.editor.BgMusic;
import jx.csp.model.editor.Music;
import jx.csp.model.editor.Music.TMusic;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseListActivity;
import lib.network.model.NetworkError;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.model.FileSuffix;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;

/**
 * 添加背景音乐页面
 *
 * @author CaiXiang
 * @since 2018/2/6
 */
@Route
public class SelectBgMusicActivity extends BaseListActivity<Music, MusicAdapter> implements OnAdapterClickListener {

    private final int KBgMusicReqId = 1000;
    private final int KSelectReqId = 1001;

    @Arg(opt = true)
    String mCourseId;

    @Arg(opt = true)
    int mMusicId;

    private LinkedList<NetworkReq> mDownloadList;  // 下载音频队列
    private LinkedList<Integer> mMusicIdList;
    private List<Music> mMusicList;
    private boolean mDownloadState = false;
    private MediaPlayer mMediaPlayer;
    private int mPlayPos = -1;
    private int mSelectPos = -1;

    @Override
    public void initData() {
        mDownloadList = new LinkedList<>();
        mMusicIdList = new LinkedList<>();
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.add_music, this);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnAdapterClickListener(this);
        refresh(RefreshWay.embed);
        exeNetworkReq(KBgMusicReqId, MeetingAPI.bgMusic().build());
    }

    @Override
    public void onAdapterClick(int position, View v) {
        switch (v.getId()) {
            case R.id.music_iv_play_state: {
                play(position);
                mPlayPos = position;
            }
            break;
            case R.id.music_iv_select: {
                mSelectPos = position;
                Music item = getItem(position);
                if (TextUtil.isEmpty(mCourseId)) {
                    forResult(item);
                } else {
                    refresh(RefreshWay.dialog);
                    exeNetworkReq(KSelectReqId, MeetingAPI.selectBgMusic(mCourseId, item.getString(TMusic.id)).build());
                }
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KBgMusicReqId) {
            return JsonParser.ev(resp.getText(), BgMusic.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (id == KBgMusicReqId) {
            if (r.isSucceed()) {
                setViewState(ViewState.normal);
                BgMusic res = (BgMusic) r.getData();
                if (res != null) {
                    mMusicList = res.getList(BgMusic.TBgMusic.list);
                    if (mMusicList != null && mMusicList.size() > 0) {
                        setData(mMusicList);
                        //  下载音频
                        downloadBgMusic(mMusicList);
                    }
                    addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            if (mMusicId != 0) {
                                for (int i = 0; i < mMusicList.size(); i++) {
                                    int musicId = (mMusicList.get(i)).getInt(TMusic.id);
                                    if (mMusicId == musicId) {
                                        getItem(i).put(TMusic.select, true);
                                        invalidate(i);
                                    }
                                }
                            }
                            removeOnGlobalLayoutListener(this);
                        }
                    });
                }
            } else {
                setViewState(ViewState.error);
                onNetworkError(id, r.getError());
            }
        } else if (id == KSelectReqId) {
            if (r.isSucceed()) {
                forResult(getItem(mSelectPos));
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            mDownloadList.removeFirst();
            mMusicIdList.removeFirst();
            mDownloadState = false;
            download();
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);
        if (id != KSelectReqId && id != KBgMusicReqId) {
            retryNetworkRequest(id);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void play(int position) {
        Music item = getItem(position);
        File file = new File(CacheUtil.getBgMusicFilePath(item.getString(TMusic.id)));
        if (mMediaPlayer == null || !file.exists()) {
            showToast(R.string.play_fail);
            return;
        }

        if (position != mPlayPos) {
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(file.getAbsolutePath());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mMediaPlayer.setOnCompletionListener(mp -> {
                    getItem(position).put(TMusic.play, false);
                    invalidate(position);
                });
            } catch (Exception e) {
                showToast(R.string.play_fail);
            }
        } else {
            boolean play = getItem(position).getBoolean(TMusic.play);
            if (play) {
                try {
                    mMediaPlayer.start();
                } catch (Exception e) {
                    showToast(R.string.play_fail);
                }
            } else {
                if (mMediaPlayer.isPlaying()) {
                    try {
                        mMediaPlayer.pause();
                    } catch (Exception e) {
                        mMediaPlayer.reset();
                    }
                }
            }
        }
    }

    private void downloadBgMusic(List<Music> list) {
        for (int i = 0; i < list.size(); i++) {
            Music music = list.get(i);
            String id = music.getString(TMusic.id);
            String filePath = CacheUtil.getBgMusicFilePath(id);
            if (!(new File(filePath)).exists()) {
                NetworkReq req = MeetingAPI.downloadAudio
                        (CacheUtil.getBgMusicCacheDir(), id + FileSuffix.mp3, music.getString(TMusic.url))
                        .build();
                mDownloadList.addLast(req);
                mMusicIdList.addLast(music.getInt(TMusic.id));
            }
        }
        download();
    }

    private void download() {
        if (mDownloadList.isEmpty()) {
            YSLog.d(TAG, "下载列表为空");
            return;
        }
        if (!mDownloadState) {
            mDownloadState = true;
            exeNetworkReq(mMusicIdList.getFirst(), mDownloadList.getFirst());
        }
    }

    private void forResult(Music item) {
        Intent intent = new Intent()
                .putExtra(Extra.KData, item.getString(TMusic.name))
                .putExtra(Extra.KLimit, item.getInt(TMusic.duration))
                .putExtra(Extra.KId, item.getInt(TMusic.id));
        setResult(RESULT_OK, intent);
        finish();
    }
}
