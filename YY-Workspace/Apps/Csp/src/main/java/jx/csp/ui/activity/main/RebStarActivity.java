package jx.csp.ui.activity.main;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.Extra;
import jx.csp.R;
import jx.csp.constant.Constants;
import jx.csp.contact.StarContract;
import jx.csp.model.editor.Theme;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.BgMusicThemeInfo;
import jx.csp.model.meeting.BgMusicThemeInfo.TBgMusicThemeInfo;
import jx.csp.model.meeting.Code;
import jx.csp.presenter.star.RebStarPresenterImpl;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.ui.activity.edit.ChoiceThemeActivityRouter;
import jx.csp.ui.activity.edit.SelectBgMusicActivityRouter;
import jx.csp.util.Util;
import lib.jx.notify.Notifier;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.decor.DecorViewEx;
import lib.ys.util.TextUtil;

/**
 * 录播星评界面
 *
 * @auther : GuoXuan
 * @since : 2018/1/17
 */
@Route
public class RebStarActivity extends BaseStarActivity {

    private final int KMusic = 100;
    private final int KTheme = 101;

    @Arg(opt = true)
    BgMusicThemeInfo mBgMusicThemeInfo;

    private TextView mTvAll;
    private View mLayoutBgMusic;  // 已经选择了的背景音乐的音乐名称/时长
    private ImageView mIvHaveMusic;  // 已经有背景音乐显示的图片
    private TextView mTvBgMusic;

    @Override
    protected StarContract.P createPresenterImpl() {
        return new RebStarPresenterImpl(getV(), mMeet);
    }

    @Override
    protected StarContract.V createViewImpl() {
        return new RebStarViewImpl();
    }

    @Override
    protected void barBack() {
        finish();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_star_reb;
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvAll = findView(R.id.star_tv_finish_all_time);

        mLayoutBgMusic = findView(R.id.star_layout_bg_music);
        mIvHaveMusic = findView(R.id.star_iv_add_music);
        mTvBgMusic = findView(R.id.star_tv_choose_bg_music);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.star_tv_preview);
        setOnClickListener(R.id.star_tv_add_theme);
        setOnClickListener(R.id.star_tv_add_music);
        setOnClickListener(R.id.star_iv_close_bg_music_layout);

        // 判断是否已经有背景音乐
        if (mBgMusicThemeInfo != null && TextUtil.isNotEmpty(mBgMusicThemeInfo.getString(TBgMusicThemeInfo.name))) {
            long time = mBgMusicThemeInfo.getLong(TBgMusicThemeInfo.duration);
            showView(mLayoutBgMusic);
            showView(mIvHaveMusic);
            mTvBgMusic.setText(mBgMusicThemeInfo.getString(TBgMusicThemeInfo.name) + " " + Util.getSpecialTimeFormat(time, "'", "''"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.star_tv_preview: {
                CommonWebViewActivityRouter.create(Util.getShareUrl(mMeet.getString(TMeet.id)))
                        .name(getString(R.string.preview))
                        .route(this);
            }
            break;
            case R.id.star_tv_add_theme: {
                if (mBgMusicThemeInfo != null) {
                    ChoiceThemeActivityRouter.create(mMeet.getString(TMeet.id), mMeet.getString(TMeet.coverUrl))
                            .themeId(mBgMusicThemeInfo.getInt(TBgMusicThemeInfo.imageId))
                            .route(this, KTheme);
                } else {
                    ChoiceThemeActivityRouter.create(mMeet.getString(TMeet.id), mMeet.getString(TMeet.coverUrl))
                            .route(this, KTheme);
                }
            }
            break;
            case R.id.star_tv_add_music: {
                if (mBgMusicThemeInfo != null) {
                    SelectBgMusicActivityRouter.create()
                            .courseId(mMeet.getString(TMeet.id))
                            .musicId(mBgMusicThemeInfo.getInt(TBgMusicThemeInfo.musicId))
                            .route(this, KMusic);
                } else {
                    SelectBgMusicActivityRouter.create()
                            .courseId(mMeet.getString(TMeet.id))
                            .route(this, KMusic);
                }
            }
            break;
            case R.id.star_iv_close_bg_music_layout: {
                // 删除背景音乐
                getP().getDataFromNet(RebStarPresenterImpl.KDeleteMusic);
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        // 返回的背景音乐信息
        if (requestCode == KMusic && data != null) {
            int time = (data.getIntExtra(Extra.KLimit, 0));
            showView(mLayoutBgMusic);
            showView(mIvHaveMusic);
            mTvBgMusic.setText(data.getStringExtra(Extra.KData) + " " + Util.getSpecialTimeFormat(time, "'", "''"));
            if (mBgMusicThemeInfo == null) {
                mBgMusicThemeInfo = new BgMusicThemeInfo();
            }
            mBgMusicThemeInfo.put(TBgMusicThemeInfo.musicId, data.getIntExtra(Extra.KId, Constants.KInvalidValue));
        } else if (requestCode == KTheme && data != null) {
            if (mBgMusicThemeInfo == null) {
                mBgMusicThemeInfo = new BgMusicThemeInfo();
            }
            mBgMusicThemeInfo.put(TBgMusicThemeInfo.imageId, data.getIntExtra(Extra.KId, Constants.KInvalidValue));
        }
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == Notifier.NotifyType.theme_refresh && data != null) {
            Theme t = (Theme) data;
            if (mBgMusicThemeInfo == null) {
                mBgMusicThemeInfo = new BgMusicThemeInfo();
            }
            int musicId = t.getInt(Theme.TTheme.musicId);
            if (musicId == Constants.KInvalidValue) {
                goneView(mLayoutBgMusic);
                goneView(mIvHaveMusic);
            } else {
                int time = t.getInt(Theme.TTheme.duration, 0);
                mTvBgMusic.setText(t.getString(Theme.TTheme.name) + " " + Util.getSpecialTimeFormat(time, "'", "''"));
                showView(mLayoutBgMusic);
                showView(mIvHaveMusic);
            }
            mBgMusicThemeInfo.put(TBgMusicThemeInfo.musicId, musicId);
            mBgMusicThemeInfo.put(TBgMusicThemeInfo.imageId, t.getInt(Theme.TTheme.imageId));
        }
    }

    private class RebStarViewImpl extends BaseStarViewImpl implements StarContract.V {

        @Override
        public void onNetworkSuccess(int id, IResult r) {
            if (id == StarContract.KReqStar) {
                if (r.isSucceed()) {
                    setViewState(DecorViewEx.ViewState.normal);
                    Code c = (Code) r.getData();
                    if (c == null) {
                        return;
                    }
                    // 讲本时长
                    String time = mMeet.getString(TMeet.playTime);
                    if (TextUtil.isEmpty(time)) {
                        time = Util.getSpecialTimeFormat(0, "'", "\"");
                    }
                    mTvAll.setText(time);
                    // 星评二维码
                    boolean startStatus = c.getBoolean(Code.TCode.starStatus);
                    if (startStatus) {
                        // 有星评
                        setDataMatrix(c.getString(Code.TCode.startCodeUrl));
                        starState(true);
                    } else {
                        // 没有星评
                        starState(false);
                    }
                } else {
                    onNetworkError(id, r.getError());
                }
            } else if (id == RebStarPresenterImpl.KDeleteMusic) {
                if (r.isSucceed()) {
                    goneView(mLayoutBgMusic);
                    goneView(mIvHaveMusic);
                    mBgMusicThemeInfo.put(TBgMusicThemeInfo.musicId, 0);
                } else {
                    retryNetworkRequest(id);
                }
            }
        }

    }

}
