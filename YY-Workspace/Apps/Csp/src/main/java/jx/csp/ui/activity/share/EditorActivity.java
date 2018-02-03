package jx.csp.ui.activity.share;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.Extra;
import jx.csp.R;
import jx.csp.adapter.share.ShareEditorAdapter;
import jx.csp.constant.MusicType;
import jx.csp.dialog.PreviewDialog;
import jx.csp.model.editor.Editor;
import jx.csp.model.editor.Editor.TEditor;
import jx.csp.model.editor.Picture;
import jx.csp.model.editor.Picture.TPicture;
import jx.csp.model.editor.Theme;
import jx.csp.model.editor.Theme.TTheme;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseRecyclerActivity;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ConstantsEx;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;

/**
 * 编辑
 *
 * @auther HuoXuYu
 * @since 2018/2/1
 */
@Route
public class EditorActivity extends BaseRecyclerActivity<Theme, ShareEditorAdapter> implements OnAdapterClickListener {

    private final int KTheme = 0;
    private final int KSave = 1;
    private final int KSaveBook = 2;
    private final int KMusic = 10;

    private ImageView mIv;
    private EditText mEt;
    private TextView mTvSave;
    private TextView mTvSaveBook;
    private TextView mTvVideo;
    private TextView mTvMusicName;
    private TextView mTvMusicTime;

    private View mLayoutMusic;//背景音乐layout
    private View mLayoutVideo;//底部按钮为保存和录音

    private int mThemePosition; //主题标识
    private int mVideoPosition; //保存按钮或继续录音标识
    private int mImgId; //主题图片id
    private int mMusicId;   //音乐id
    private int mMeetId;    //会议id
    private String mTitle;  //会议标题

    @Arg
    boolean mFlag;  //从哪里进入的标识
    @Arg(opt = true)
    Meet mMeet;
    @Arg(opt = true)
    String mPictureSort;  //上传的图片


    @Override
    public void initData() {
        mThemePosition = ConstantsEx.KInvalidValue;
        mVideoPosition = ConstantsEx.KInvalidValue;
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_editor;
    }

    @Nullable
    @Override
    public int getContentFooterViewId() {
        if (mFlag) {
            return R.layout.layout_editor_footer;
        } else {
            return R.layout.layout_editor_footer_video;
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.editor, this);
    }

    @Override
    public void findViews() {
        super.findViews();
        mEt = findView(R.id.editor_et);
        mIv = findView(R.id.form_iv_clean);

        //背景音乐的id
        mLayoutMusic = findView(R.id.editor_music_layout);
        mTvMusicName = findView(R.id.editor_music_name);
        mTvMusicTime = findView(R.id.editor_music_time);

        //footer的id
        mTvSave = findView(R.id.editor_tv_save);
        mTvSaveBook = findView(R.id.editor_tv_save_book);
        mTvVideo = findView(R.id.editor_tv_video);
        mLayoutVideo = findView(R.id.editor_footer_layout);
    }

    @Override
    public void setViews() {
        super.setViews();
        getClickListener();
        getAdapter().setOnAdapterClickListener(this);

        //获取主题皮肤
        refresh(RefreshWay.embed);
        exeNetworkReq(KTheme, MeetingAPI.editor(MusicType.theme).build());

        //设置标题
        if (mFlag) {
            mEt.setText(mMeet.getString(TMeet.title));
        } else {
            mEt.setText(ConstantsEx.KEmpty);
            mTvSaveBook.setEnabled(false);
            mTvVideo.setEnabled(false);
        }

        mEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEt.hasFocus() && TextUtil.isNotEmpty(s)) {
                    showView(mIv);
                    if (mFlag) {
                        mTvSave.setEnabled(true);
                    } else {
                        mTvSaveBook.setEnabled(true);
                        mTvVideo.setEnabled(true);
                    }
                } else {
                    hideView(mIv);
                    if (mFlag) {
                        mTvSave.setEnabled(false);
                    } else {
                        mTvSaveBook.setEnabled(false);
                        mTvVideo.setEnabled(false);
                    }
                }
            }
        });

        if (mMeet != null) {
            mMeetId = mMeet.getInt(TMeet.id);
        }
        mTitle = mEt.getText().toString();
    }

    @Override
    protected LayoutManager initLayoutManager() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        return manager;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.form_iv_clean: {
                mEt.setText("");
            }
            break;
            case R.id.editor_music_clean: {
                goneView(mLayoutMusic);
                mMusicId = 0;
            }
            break;
            case R.id.editor_select_music: {
                startActivityForResult(MusicActivity.class, KMusic);
            }
            break;
            case R.id.editor_tv_save: {
                //分享进入的保存按钮
                refresh(RefreshWay.dialog);
                exeNetworkReq(KSave, MeetingAPI.updateMini(mMeetId, mTitle, mImgId, mMusicId).build());
                mVideoPosition = 0;
            }
            break;
            case R.id.editor_tv_save_book: {
                //新建讲本进入的保存按钮,创建课件接口
                refresh(RefreshWay.dialog);
                exeNetworkReq(KSaveBook, MeetingAPI.picture(0, 0, 0).build());
                mVideoPosition = 0;
            }
            break;
            case R.id.editor_tv_video: {
                //新建讲本进入的继续录音按钮,创建课件接口
                // FIXME: 2018/2/2 未完成,暂无图片
                refresh(RefreshWay.dialog);
                exeNetworkReq(KSaveBook, MeetingAPI.picture(0, 0, 0).build());
                mVideoPosition = 1;
            }
            break;
        }
    }

    @Override
    public void onAdapterClick(int position, View v) {
        switch (v.getId()) {
            case R.id.editor_theme_layout: {
                if (mThemePosition != position) {
                    if (mThemePosition != ConstantsEx.KInvalidValue) {
                        //再次点击取消
                        getItem(mThemePosition).put(TTheme.select, false);
                        mImgId = 0;
                    }
                    getItem(position).put(TTheme.select, true);
                    mThemePosition = position;
                    mImgId = getItem(position).getInt(TTheme.id);
                } else {
                    boolean select = getItem(position).getBoolean(TTheme.select);
                    getItem(position).put(TTheme.select, !select);
                    mImgId = 0;
                }
                invalidate();
            }
            break;
            case R.id.editor_preview_tv_item: {
                //预览主题皮肤
                String coverUrl;
                if (mFlag) {
                    coverUrl = mMeet.getString(TMeet.coverUrl);
                } else {
                    coverUrl = mPictureSort;
                }
                PreviewDialog dialog = new PreviewDialog(this, getAdapter().getItem(position).getString(TTheme.imgUrl), coverUrl);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KTheme) {
            return JsonParser.ev(resp.getText(), Editor.class);
        } else if (id == KSaveBook) {
            return JsonParser.ev(resp.getText(), Picture.class);
        }
        {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        switch (id) {
            case KTheme: {
                setViewState(ViewState.normal);
                Editor editor = (Editor) r.getData();
                if (editor != null) {
                    List<Theme> list = editor.getList(TEditor.imageList);
                    setData(list);
                }
            }
            break;
            case KSave: {
                if (r.isSucceed()) {
                    if (mVideoPosition == 0) {
                        finish();
                    } else {
                        //带图片跳转录音页面
                        // FIXME: 2018/2/3 未完成
//                    RecordActivityRouter.create(String.valueOf(mMeetId)).route(this);
                    }
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
            case KSaveBook: {
                if (r.isSucceed()) {
                    Picture picture = (Picture) r.getData();
                    if (picture != null) {
                        mMeetId = picture.getInt(TPicture.id);
                    }
                    exeNetworkReq(KSave, MeetingAPI.updateMini(mMeetId, mTitle, mImgId, mMusicId).build());
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);
        if (id == KTheme) {
            setViewState(ViewState.error);
            if (mFlag) {
                goneView(mTvSave);
            } else {
                goneView(mLayoutVideo);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        //背景音乐的返回
        if (data != null) {
            mTvMusicName.setText(data.getStringExtra(Extra.KData));
            long time = Long.parseLong(data.getStringExtra(Extra.KLimit));
            mTvMusicTime.setText(Util.getSpecialTimeFormat(time, "'", "''"));
            mMusicId = Integer.parseInt(data.getStringExtra(Extra.KId));
            showView(mLayoutMusic);
            invalidate();
        }
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            //获取主题皮肤
            refresh(RefreshWay.embed);
            exeNetworkReq(KTheme, MeetingAPI.editor(MusicType.theme).build());
        }
        return true;
    }

    private void getClickListener() {
        setOnClickListener(R.id.form_iv_clean);
        setOnClickListener(R.id.editor_music_clean);

        setOnClickListener(R.id.editor_select_music);
        setOnClickListener(R.id.editor_tv_save);
        setOnClickListener(R.id.editor_tv_save_book);
        setOnClickListener(R.id.editor_tv_video);
    }
}
