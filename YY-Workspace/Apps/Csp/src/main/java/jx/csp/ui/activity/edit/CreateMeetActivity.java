package jx.csp.ui.activity.edit;

import android.support.annotation.CallSuper;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.model.editor.Editor;
import jx.csp.model.editor.Theme;
import jx.csp.model.editor.Upload;
import jx.csp.network.JsonParser;
import jx.csp.network.NetFactory;
import jx.csp.network.NetworkApiDescriptor;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.config.AppConfig;
import lib.ys.ui.decor.DecorViewEx;
import lib.ys.util.FileUtil;

/**
 * 创建会议
 *
 * @auther : GuoXuan
 * @since : 2018/2/7
 */
@Route
public class CreateMeetActivity extends BaseEditActivity {

    private final int KCreate = 11;
    private final int KUpload = 12;

    private TextView mTvCreate;
    private TextView mTvRecord;

    private String mMeetId;    // 会议id

    @Arg(opt = true)
    ArrayList<String> mPicture;  //上传的图片

    private LinkedList<NetworkReq> mUploadList;  // 上传图片队列
    private boolean mUploadState = false; // 是否在上传
    private boolean mUploadFirstSuccess = false;

    @Override
    public void initData() {
        super.initData();

        mUploadList = new LinkedList<>();
    }

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();

        mTvCreate = findView(R.id.editor_tv_save);
        mTvRecord = findView(R.id.editor_tv_record);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(mTvCreate);
        setOnClickListener(mTvRecord);

        mTvCreate.setEnabled(false);
        mTvRecord.setEnabled(false);
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KUpload) {
            return JsonParser.ev(resp.getText(), Upload.class);
        } else {
            return super.onNetworkResponse(id, resp);
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            switch (id) {
                case KReqTheme: {
                    setViewState(DecorViewEx.ViewState.normal);
                    Editor editor = (Editor) r.getData();
                    if (editor != null) {
                        List<Theme> list = editor.getList(Editor.TEditor.imageList);
                        setData(list);
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
                                    NetworkReq req = NetworkApiDescriptor.MeetingAPI.picture(bytes, 0)
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
    protected int getFooterId() {
        return R.layout.layout_editor_footer_create;
    }

    @Override
    protected void onClick(int id) {
        switch (id) {
            case R.id.editor_tv_save:
            case R.id.editor_tv_record: {
                //新建讲本进入的继续录音按钮,创建课件接口
                refresh(AppConfig.RefreshWay.dialog);

                String path = mPicture.get(0);
                File file = new File(path);
                if (file.exists()) {
                    byte[] bytes = FileUtil.fileToBytes(path);
                    NetworkReq req = NetworkApiDescriptor.MeetingAPI.picture(bytes, 0)
                            .build();
                    mUploadList.addLast(req);
                }
                upload();
            }
            break;
        }
    }

    @Override
    protected void titleState(boolean notEmpty) {
        mTvCreate.setEnabled(notEmpty);
        mTvRecord.setEnabled(notEmpty);
    }

    private void upload() {
        if (mUploadList.isEmpty()) {
            YSLog.d(TAG, "上传列表为空");
            exeNetworkReq(KCreate,
                    NetFactory.update(mMeetId, getTitleText(), mThemeId, mMusicId));
            return;
        }
        if (!mUploadState) {
            YSLog.d(TAG, "开始上传任务");
            exeNetworkReq(KUpload, mUploadList.getFirst());
            mUploadState = true;
        }
    }

}
