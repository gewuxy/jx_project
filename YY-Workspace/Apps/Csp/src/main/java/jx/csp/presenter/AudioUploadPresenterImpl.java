package jx.csp.presenter;

import android.util.SparseArray;

import java.io.File;
import java.util.LinkedList;

import jx.csp.constant.Constants;
import jx.csp.contact.AudioUploadContract;
import jx.csp.contact.AudioUploadContract.V;
import jx.csp.model.meeting.Course.CourseType;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import lib.jx.contract.BasePresenterImpl;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.util.FileUtil;

/**
 * @author CaiXiang
 * @since 2017/11/16
 */

public class AudioUploadPresenterImpl extends BasePresenterImpl<V> implements AudioUploadContract.P {

    private SparseArray<String> mCourseDetailIdArray;
    private LinkedList<NetworkReq> mUploadList;  // 上传音频队列
    private LinkedList<String> mUploadFilePathList; // 直播时上传音频地址列表，上传完删除
    private boolean mUploadState = false; // 是否在上传音频

    public AudioUploadPresenterImpl(V v) {
        super(v);

        mUploadList = new LinkedList<>();
        mUploadFilePathList = new LinkedList<>();
    }

    @Override
    public void setCourseDetailIdArray(SparseArray<String> courseDetailIdArray) {
        mCourseDetailIdArray = courseDetailIdArray;
    }

    @Override
    public void uploadAudioFile(String courseId, int page, int type, String audioFilePath, int time) {
        File file = new File(audioFilePath);
        if (file.exists()) {
            byte[] bytes = FileUtil.fileToBytes(audioFilePath);
            YSLog.d(TAG, "upload audioFilePath = " + audioFilePath);
            YSLog.d(TAG, "upload audioFile bytes = " + bytes.length);
            // 直播时小于三秒的音频不上传并且删除文件
            YSLog.d(TAG, "音频文件的时间 = " + time + "秒");
            if (type == CourseType.ppt_live && time < 3) {
                YSLog.d(TAG, "直播 小于三秒的音频不上传且删除对应文件");
                FileUtil.delFile(file);
                return;
            }
            NetworkReq req = MeetingAPI.uploadAudio()
                    .courseId(courseId)
                    .detailId(mCourseDetailIdArray.get(page))
                    .pageNum(page)
                    .playType(type)
                    .file(bytes)
                    .build();
            mUploadList.addLast(req);
            if (type == CourseType.ppt_live || type == CourseType.ppt_video_live) {
                mUploadFilePathList.addLast(audioFilePath);
            }
            upload();
        } else {
            YSLog.d(TAG, "上传音频文件不存在");
        }
    }

    private void upload() {
        if (mUploadList.isEmpty()) {
            YSLog.d(TAG, "上传列表为空");
            return;
        }
        if (!mUploadState) {
            YSLog.d(TAG, "开始上传任务");
            exeNetworkReq(mUploadList.getFirst());
            mUploadState = true;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.error(resp.getText());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            YSLog.d(TAG, "直播音频文件上传成功，从上传列表这个移除任务");
            if (mUploadFilePathList != null && mUploadFilePathList.size() > 0) {
                YSLog.d(TAG, "直播音频文件上传成功，删除对应文件路径 = " + mUploadFilePathList.getFirst());
                boolean b = FileUtil.delFile(new File(mUploadFilePathList.getFirst()));
                YSLog.d(TAG, "删除对应文件成功？ = " + b);
                mUploadFilePathList.removeFirst();
            }
            mUploadList.removeFirst();
            mUploadState = false;
            upload();
        } else {
            // 如果code = 401 就不再重试
            if (r.getCode() == Constants.KCourseDelete) {
                Notifier.inst().notify(NotifyType.course_already_delete_record);
            } else {
                // 上传失败就重试
                retryNetworkRequest(id);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mUploadList.isEmpty()) {
            super.onDestroy();
        }
    }
}
