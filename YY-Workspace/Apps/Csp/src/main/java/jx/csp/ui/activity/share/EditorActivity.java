package jx.csp.ui.activity.share;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.Extra;
import jx.csp.R;
import jx.csp.adapter.share.EditorAdapter;
import jx.csp.constant.Constants;
import jx.csp.model.editor.Editor;
import jx.csp.model.editor.Editor.TEditor;
import jx.csp.model.editor.Theme;
import jx.csp.model.editor.Upload;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.network.JsonParser;
import jx.csp.network.NetFactory;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.ui.activity.main.SelectBgMusicActivityRouter;
import jx.csp.util.UISetter;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseRecyclerActivity;
import lib.network.model.NetworkError;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.ys.util.TextUtil;

/**
 * 编辑
 *
 * @auther HuoXuYu
 * @since 2018/2/1
 */
@Route
public class EditorActivity extends BaseRecyclerActivity<Theme, EditorAdapter> implements OnAdapterClickListener {

    private final int KTheme = 0;
    private final int KSave = 1;
    private final int KCreate = 2;
    private final int KUpload = 3;
    private final int KMusic = 10;

    private ImageView mIv;
    private EditText mEt;
    private TextView mTvSave;
    private TextView mTvSaveBook;
    private TextView mTvVideo;
    private TextView mTvMusicName;
    private TextView mTvMusicTime;

    private View mLayoutMusic; // 背景音乐layout

    private int mImgId; // 主题图片id
    private int mMusicId;   // 音乐id
    private String mMeetId;    // 会议id

    @Arg
    boolean mIsShare;  //从哪里进入的标识,true为分享,false为新建

    @Arg(opt = true)
    Meet mMeet;

    @Arg(opt = true)
    String mPreviewUrl; // 预览的图片

    @Arg(opt = true)
    ArrayList<String> mPicture;  //上传的图片

    private LinkedList<NetworkReq> mUploadList;  // 上传图片队列
    private boolean mUploadState = false; // 是否在上传
    private boolean mUploadFirstSuccess = false;

    @Override
    public void initData() {
        mUploadList = new LinkedList<>();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_editor;
    }

    @Override
    public int getContentFooterViewId() {
        if (mIsShare) {
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

        setOnClickListener(mTvMusicName);
        setOnClickListener(mTvMusicTime);

        setOnClickListener(R.id.editor_select_music);
        setOnClickListener(R.id.editor_tv_save);
        setOnClickListener(R.id.editor_tv_save_book);
        setOnClickListener(R.id.editor_tv_video);

        setOnAdapterClickListener(this);

        //分享进入的
        if (mMeet != null) {
            mMeetId = mMeet.getString(TMeet.id);
        }

        refresh(RefreshWay.embed);
        exeNetworkReq(KTheme, MeetingAPI.editMeet().courseId(mMeetId).build());

        //设置标题
        if (mIsShare) {
            showView(mIv);
        } else {
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
                    if (mIsShare) {
                        mTvSave.setEnabled(true);
                    } else {
                        mTvSaveBook.setEnabled(true);
                        mTvVideo.setEnabled(true);
                    }
                } else {
                    hideView(mIv);
                    if (mIsShare) {
                        mTvSave.setEnabled(false);
                    } else {
                        mTvSaveBook.setEnabled(false);
                        mTvVideo.setEnabled(false);
                    }
                }
            }
        });
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
                mMusicId = Constants.KInvalidValue;
            }
            break;
            case R.id.editor_select_music: {
                SelectBgMusicActivityRouter.create().route(this, KMusic);
            }
            break;
            case R.id.editor_tv_save: {
                //分享进入的保存按钮
                refresh(RefreshWay.dialog);
                exeNetworkReq(KSave, NetFactory.update(mMeetId, getEt(), mImgId, mMusicId));
            }
            break;
            case R.id.editor_tv_save_book:
            case R.id.editor_tv_video: {
                //新建讲本进入的继续录音按钮,创建课件接口
                refresh(RefreshWay.dialog);

                String path = mPicture.get(0);
                File file = new File(path);
                if (file.exists()) {
                    byte[] bytes = FileUtil.fileToBytes(path);
                    NetworkReq req = MeetingAPI.picture(bytes, 0)
                            .build();
                    mUploadList.addLast(req);
                }
                upload();
            }
            break;
        }
    }

    @Override
    public void onAdapterClick(int position, View v) {
        switch (v.getId()) {
            case R.id.editor_theme_layout: {
                boolean select = getItem(position).getBoolean(Theme.TTheme.select);
                mImgId = select ? getItem(position).getInt(Theme.TTheme.id) : Constants.KInvalidValue;
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
        if (id == KTheme) {
            return JsonParser.ev(resp.getText(), Editor.class);
        }
        if (id == KUpload) {
            return JsonParser.ev(resp.getText(), Upload.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            switch (id) {
                case KTheme: {
                    setViewState(ViewState.normal);
                    Editor editor = (Editor) r.getData();
                    if (editor != null) {
                        List<Theme> list = editor.getList(TEditor.imageList);
                        setData(list);
                        Meet m = editor.get(TEditor.course);
                        if (m != null) {
                            String title = m.getString(TMeet.title);
                            mEt.setText(title);
                            boolean enabled = TextUtil.isNotEmpty(title);
                            if (mIsShare) {
                                mTvSave.setEnabled(enabled);
                            } else {
                                mTvSaveBook.setEnabled(enabled);
                                mTvVideo.setEnabled(enabled);
                            }
                        }
                        Theme t = editor.get(TEditor.theme);
                        if (t != null) {
                            mMusicId = t.getInt(Theme.TTheme.musicId);
                            mImgId = t.getInt(Theme.TTheme.imgUrl);
                            mMeetId = t.getString(Theme.TTheme.courseId);
                            String name = t.getString(Theme.TTheme.name);
                            if (TextUtil.isNotEmpty(name)) {
                                showView(mLayoutMusic);
                                mTvMusicName.setText(name);
                                mTvMusicName.setText(Util.getSpecialTimeFormat(t.getLong(Theme.TTheme.duration), "'", "''"));
                            }
                        }
                    }
                }
                break;
                case KCreate: {
                    stopRefresh();
                    finish();
                }
                break;
                case KUpload: {
                    Upload upload = (Upload) r.getData();
                    if (upload != null) {
                        mMeetId = upload.getString(Upload.TUpload.id);
                        if (!mUploadFirstSuccess) {
                            for (int i = 1; i < mPicture.size(); i++) {
                                String path = mPicture.get(i);
                                File file = new File(path);
                                if (file.exists()) {
                                    byte[] bytes = FileUtil.fileToBytes(path);
                                    NetworkReq req = MeetingAPI.picture(bytes, 0)
                                            .courseId(mMeetId)
                                            .build();
                                    mUploadList.addLast(req);
                                }
                            }
                            mUploadFirstSuccess = true;
                        }
                    }
                    mUploadList.removeFirst();
                    mUploadState = false;
                    upload();
                }
                break;
            }
        } else {
            onNetworkError(id, r.getError());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        // 背景音乐的返回
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
            // 获取主题皮肤
            refresh(RefreshWay.embed);
            exeNetworkReq(KTheme, MeetingAPI.editMeet().courseId(mMeetId).build());
        }
        return true;
    }

    private void upload() {
        if (mUploadList.isEmpty()) {
            YSLog.d(TAG, "上传列表为空");
            exeNetworkReq(KCreate, MeetingAPI.update(mMeetId)
                    .title(getEt())
                    .imgId(mImgId)
                    .musicId(mMusicId)
                    .build());
            return;
        }
        if (!mUploadState) {
            YSLog.d(TAG, "开始上传任务");
            exeNetworkReq(KUpload, mUploadList.getFirst());
            mUploadState = true;
        }
    }

    private String getEt() {
        return mEt.getText().toString();
    }
}
