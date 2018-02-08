package jx.csp.ui.activity.edit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import inject.annotation.router.Arg;
import jx.csp.Extra;
import jx.csp.R;
import jx.csp.adapter.share.EditorAdapter;
import jx.csp.constant.Constants;
import jx.csp.model.editor.Editor;
import jx.csp.model.editor.Theme;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.util.UISetter;
import jx.csp.util.Util;
import lib.jx.notify.Notifier;
import lib.jx.ui.activity.base.BaseRecyclerActivity;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.view.LayoutUtil;

/**
 * 编辑
 *
 * @auther HuoXuYu
 * @since 2018/2/1
 */
abstract public class BaseEditActivity extends BaseRecyclerActivity<Theme, EditorAdapter> implements
        OnAdapterClickListener,
        TextWatcher, View.OnFocusChangeListener {

    protected final int KReqTheme = 0;
    private final int KTheme = 10;
    private final int KMusic = 11;

    private ImageView mIvClean;
    private EditText mEtTitle;
    private TextView mTvMusicName;
    private TextView mTvMusicTime;
    private TextView mTvTheme;

    private View mLayoutMusic; // 背景音乐layout

    protected int mThemeId; // 主题图片id
    protected int mMusicId;   // 音乐id
    protected String mMeetId;    // 会议id

    @Arg(opt = true)
    String mPreviewUrl; // 预览的图片

    @Override
    public int getContentViewId() {
        return R.layout.activity_editor;
    }

    @CallSuper
    @Override
    public void initData() {
        mThemeId = Constants.KInvalidValue;
        mMusicId = Constants.KInvalidValue;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.editor, this);
    }

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();

        mEtTitle = findView(R.id.editor_et_title);
        mIvClean = findView(R.id.editor_iv_clean);

        mTvTheme = findView(R.id.theme_choice_tv_theme);

        //背景音乐的id
        mLayoutMusic = findView(R.id.editor_music_layout);
        mTvMusicName = findView(R.id.editor_music_name);
        mTvMusicTime = findView(R.id.editor_music_time);

        LinearLayout layout = findView(R.id.editor_layout_footer);
        layout.addView(inflate(getFooterId()), LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT); // 不影响子类重写
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.editor_music_clean);
        setOnClickListener(R.id.edit_layout_theme_more);
        setOnClickListener(R.id.edit_layout_music_more);
        setOnClickListener(mIvClean);
        setOnAdapterClickListener(this);
        mEtTitle.addTextChangedListener(this);
        mEtTitle.setOnFocusChangeListener(this);

        refresh(RefreshWay.embed);
        getDataFromNet();
    }

    @Override
    protected LayoutManager initLayoutManager() {
        LinearLayoutManager manager = new GridLayoutManager(this, 4);
        return manager;
    }

    @Override
    public final void onClick(View v) {
        switch (v.getId()) {
            case R.id.editor_iv_clean: {
                mEtTitle.setText(Constants.KEmpty);
            }
            break;
            case R.id.editor_music_clean: {
                goneView(mLayoutMusic);
                mMusicId = Constants.KInvalidValue;
            }
            break;
            case R.id.edit_layout_music_more: {
                SelectBgMusicActivityRouter.create().route(this, KMusic);
            }
            break;
            case R.id.edit_layout_theme_more: {
                ChoiceThemeActivityRouter.create(mMeetId, mPreviewUrl)
                        .save(true)
                        .themeId(mThemeId)
                        .route(this, KTheme);
            }
            break;
            default: {
                onClick(v.getId());
            }
            break;
        }
    }

    @Override
    public void onAdapterClick(int position, View v) {
        switch (v.getId()) {
            case R.id.editor_theme_layout: {
                boolean select = getItem(position).getBoolean(Theme.TTheme.select);
                mThemeId = select ? getItem(position).getInt(Theme.TTheme.id) : Constants.KInvalidValue;
                mTvTheme.setText(getItem(position).getString(Theme.TTheme.imgName));
            }
            break;
            case R.id.editor_preview_tv_item: {
                // 预览主题皮肤
                String theme = getItem(position).getString(Theme.TTheme.imgUrl);
                UISetter.previewTheme(this, theme, mPreviewUrl);
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KReqTheme) {
            return JsonParser.ev(resp.getText(), Editor.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        if (id == KReqTheme) {
            setViewState(ViewState.error);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == KMusic && data != null) {
            // 背景音乐返回
            mMusicId = data.getIntExtra(Extra.KId, Constants.KInvalidValue);
            String musicName = data.getStringExtra(Extra.KData);
            long time = data.getLongExtra(Extra.KLimit, 0);
            setMusic(musicName, time);
        } else if (requestCode == KTheme && data != null) {
            // 主题返回
            String themeName = data.getStringExtra(Extra.KData);
            if (TextUtil.isEmpty(themeName)) {
                themeName = getString(R.string.more);
            }
            mTvTheme.setText(themeName);
            mThemeId = data.getIntExtra(Extra.KId, Constants.KInvalidValue);
            for (Theme theme : getData()) {
                if (theme.getInt(Theme.TTheme.id) == mThemeId) {
                    theme.put(Theme.TTheme.select, true);
                } else {
                    theme.put(Theme.TTheme.select, false);
                }
            }
            invalidate();
        }
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onNotify(int type, Object data) {
        switch (type) {
            case Notifier.NotifyType.finish_editor_meet: {
                finish();
            }
            break;
        }
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            getDataFromNet();
        }
        return true;
    }

    @Override
    public void getDataFromNet() {
        // 获取主题皮肤
        exeNetworkReq(KReqTheme, MeetingAPI.editMeet().courseId(mMeetId).build());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mEtTitle.hasFocus() && TextUtil.isNotEmpty(s)) {
            showView(mIvClean);
        } else {
            goneView(mIvClean);
        }
        titleState(TextUtil.isNotEmpty(s.toString().trim()));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.editor_et_title) {
            if (hasFocus && TextUtil.isNotEmpty(getTitleText())) {
                showView(mIvClean);
            } else {
                goneView(mIvClean);
            }
        }
    }

    /**
     * 获取标题的文本
     */
    protected String getTitleText() {
        return mEtTitle.getText().toString().trim();
    }

    /**
     * 设置标题的文本
     *
     * @param title 标题的文本
     */
    protected void setTitleText(String title) {
        mEtTitle.setText(title);
    }

    /**
     * 设置主题的文本
     *
     * @param theme 主题的文本
     */
    protected void setThemeText(String theme) {
        mTvTheme.setText(theme);
    }

    /**
     * 设置音乐相关的
     *
     * @param musicName 音乐名称
     * @param time      时长
     */
    protected void setMusic(String musicName, long time) {
        if (TextUtil.isEmpty(musicName)) {
            goneView(mLayoutMusic);
        } else {
            showView(mLayoutMusic);
            mTvMusicName.setText(musicName);
            mTvMusicTime.setText(Util.getSpecialTimeFormat(time, "'", "''"));
        }
    }

    @LayoutRes
    abstract protected int getFooterId();

    abstract protected void onClick(int id);

    /**
     * 标题修改的状态
     *
     * @param notEmpty 标题是否有内容
     */
    abstract protected void titleState(boolean notEmpty);

}
