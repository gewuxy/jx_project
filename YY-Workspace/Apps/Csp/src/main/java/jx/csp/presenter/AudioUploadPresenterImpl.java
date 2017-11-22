package jx.csp.presenter;

import android.util.SparseArray;

import java.io.File;
import java.util.LinkedList;

import jx.csp.contact.AudioUploadContract;
import jx.csp.contact.AudioUploadContract.V;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.util.FileUtil;
import lib.yy.contract.BasePresenterImpl;

/**
 * @author CaiXiang
 * @since 2017/11/16
 */

public class AudioUploadPresenterImpl extends BasePresenterImpl<AudioUploadContract.V> implements AudioUploadContract.P {

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
    public void uploadAudioFile(String courseId, int page, int type, String audioFilePath) {
        File file = new File(audioFilePath);
        if (file.exists()) {
            byte[] bytes = FileUtil.fileToBytes(audioFilePath);
            YSLog.d(TAG, "upload audioFilePath = " + audioFilePath);
            YSLog.d(TAG, "audioFile bytes = " + bytes.length);
            // 直播时小于三秒的音频不上传并且删除文件 1s差不多1500 byte
            if (type == PlayType.live && bytes.length < 4500) {
                FileUtil.delFile(file);
                YSLog.d(TAG, "直播时小于三秒的音频不上传且删除对应文件");
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
            if (type == PlayType.live || type == PlayType.video) {
                mUploadFilePathList.addLast(audioFilePath);
            }
            upload();
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
            mUploadList.removeFirst();
            if (mUploadFilePathList != null && mUploadFilePathList.size() > 0) {
                boolean b = FileUtil.delFile(new File(mUploadFilePathList.getFirst()));
                YSLog.d(TAG, "直播音频文件上传成功，删除对应文件成功？ = " + b);
                mUploadFilePathList.removeFirst();
            }
            mUploadState = false;
            upload();
        } else {
            // 上传失败就重试
            retryNetworkRequest(id);
        }
    }
}
