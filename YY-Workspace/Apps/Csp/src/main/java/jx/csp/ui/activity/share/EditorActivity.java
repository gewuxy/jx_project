package jx.csp.ui.activity.share;

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
import jx.csp.R;
import jx.csp.adapter.share.ShareEditorAdapter;
import jx.csp.dialog.PreviewDialog;
import jx.csp.model.editor.Editor;
import jx.csp.model.editor.Editor.TEditor;
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
    private final int KVideo = 3;

    private ImageView mIv;
    private EditText mEt;
    private TextView mTvSave;
    private TextView mTvSaveBook;
    private TextView mTvVideo;
    private TextView mTvMusicName;
    private TextView mTvMusicTime;

    private View mLayoutMusic;

    private int mThemePosition;
    private int mImgId;

    @Arg
    boolean mFlag;
    @Arg(opt = true)
    Meet mMeet;
    @Arg(opt = true)
    String mPictureSort;


    @Override
    public void initData() {
        mThemePosition = ConstantsEx.KInvalidValue;
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
    }

    @Override
    public void setViews() {
        super.setViews();
        getClickListener();
        getAdapter().setOnAdapterClickListener(this);

        refresh(RefreshWay.embed);
        exeNetworkReq(KTheme, MeetingAPI.editor(0).build());

        if (mFlag) {
            mEt.setText(mMeet.getString(TMeet.title));
        } else {
            mEt.setText(ConstantsEx.KEmpty);
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
    }

    private void getClickListener() {
        setOnClickListener(R.id.form_iv_clean);
        setOnClickListener(R.id.editor_music_clean);

        setOnClickListener(R.id.editor_select_music);
        setOnClickListener(R.id.editor_tv_save);
        setOnClickListener(R.id.editor_tv_save_book);
        setOnClickListener(R.id.editor_tv_video);
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
            }
            break;
            case R.id.editor_select_music: {
                // FIXME: 2018/2/2 测试
                showView(mLayoutMusic);
            }
            break;
            case R.id.editor_tv_save: {
                refresh(RefreshWay.dialog);
                exeNetworkReq(KSave, MeetingAPI.updateMini(mMeet.getInt(TMeet.id), mEt.getText().toString(), mImgId, 0).build());
            }
            break;
            case R.id.editor_tv_save_book: {
                // FIXME: 2018/2/2 未完成
                refresh(RefreshWay.dialog);
                exeNetworkReq(KSaveBook, MeetingAPI.updateMini(mMeet.getInt(TMeet.id), mEt.getText().toString(), mImgId, 0).build());
            }
            break;
            case R.id.editor_tv_video: {
                // FIXME: 2018/2/2 未完成
                refresh(RefreshWay.dialog);
                exeNetworkReq(KVideo, MeetingAPI.picture(0, 0, 0).build());
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KTheme) {
            return JsonParser.ev(resp.getText(), Editor.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        switch (id) {
            case KTheme: {
                if (r.isSucceed()) {
                    setViewState(ViewState.normal);
                    Editor editor = (Editor) r.getData();
                    if (editor != null) {
                        List<Theme> list = editor.getList(TEditor.imageList);
                        setData(list);

                    }
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
            case KSave: {
                stopRefresh();
                if (r.isSucceed()) {
                    finish();
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
            case KSaveBook: {
                stopRefresh();
                if (r.isSucceed()) {
                    finish();
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
            case KVideo: {

            }
            break;
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);
        if (id == KTheme) {
            setViewState(ViewState.error);
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
                    mImgId = mMeet.getInt(TMeet.id);
                } else {
                    boolean select = getItem(position).getBoolean(TTheme.select);
                    getItem(position).put(TTheme.select, !select);
                    mImgId = 0;
                }
                invalidate();
            }
            break;
            case R.id.editor_preview_tv_item: {
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
}
