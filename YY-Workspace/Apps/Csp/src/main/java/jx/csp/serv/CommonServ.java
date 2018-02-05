package jx.csp.serv;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.model.Profile;
import jx.csp.model.RecordUnusualState;
import jx.csp.model.RecordUnusualState.TRecordUnusualState;
import jx.csp.model.editor.Picture;
import jx.csp.model.editor.Picture.TPicture;
import jx.csp.model.login.Advert;
import jx.csp.model.meeting.Course.CourseType;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.AdvertAPI;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.util.CacheUtil;
import lib.jg.jpush.SpJPush;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ConstantsEx;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.ys.util.FileUtil;

/**
 * @auther Huoxuyu
 * @since 2017/10/16
 */
@Route
public class CommonServ extends ServiceEx {

    @Arg
    @ReqType
    int mType;

    @Arg(opt = true)
    String mToken;

    @Arg(opt = true)
    String mJPushRegisterId;

    @Arg(opt = true)
    String mCourseId;

    @Arg(opt = true)
    int mPageNum;

    @Arg(opt = true)
    int mOverType;

    @Arg(opt = true)
    String mUrl;

    @Arg(opt = true)
    String mFileName;

    @Arg(opt = true)
    int mNewVersion;

    @Arg(opt = true)
    String mTitle;

    @Arg(opt = true)
    ArrayList<String> mPhoto;

    @IntDef({
            ReqType.logout,
            ReqType.j_push,
            ReqType.exit_record,
            ReqType.share_delete_meet,
            ReqType.advert,
            ReqType.over_live,
            ReqType.upload_audio,
            ReqType.upload_photo,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReqType {
        int logout = 1;
        int j_push = 2;
        int exit_record = 3;
        int share_delete_meet = 4;
        int advert = 5;
        int over_live = 6;
        int upload_audio = 7;
        int upload_photo = 8;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch (mType) {
            case ReqType.logout: {
                exeNetworkReq(mType, UserAPI.logout().build());
            }
            break;
            case ReqType.j_push: {
                exeNetworkReq(mType, UserAPI.bindJPush(mJPushRegisterId).build());
            }
            break;
            case ReqType.exit_record: {
                exeNetworkReq(mType, MeetingAPI.exitRecord(mCourseId, mPageNum, mOverType).build());
            }
            break;
            case ReqType.share_delete_meet: {
                exeNetworkReq(mType, MeetingAPI.delete(mCourseId).build());
            }
            break;
            case ReqType.advert: {
                exeNetworkReq(mType, AdvertAPI.advert().build());
            }
            break;
            case ReqType.over_live: {
                exeNetworkReq(mType, MeetingAPI.overLive(mCourseId).build());
            }
            break;
            case ReqType.upload_audio: {
                String filePath = CacheUtil.getExistAudioFilePath(RecordUnusualState.inst().getString(TRecordUnusualState.courseId),
                        RecordUnusualState.inst().getInt(TRecordUnusualState.pageId));
                if ((new File(filePath)).exists()) {
                    byte[] bytes = FileUtil.fileToBytes(filePath);
                    exeNetworkReq(mType, MeetingAPI.uploadAudio()
                            .courseId(RecordUnusualState.inst().getString(TRecordUnusualState.courseId))
                            .pageNum(RecordUnusualState.inst().getInt(TRecordUnusualState.page))
                            .detailId(RecordUnusualState.inst().getString(TRecordUnusualState.pageId))
                            .playType(CourseType.reb)
                            .file(bytes)
                            .build());
                } else {
                    YSLog.d(TAG, "上传音频不存在");
                }
            }
            break;
            case ReqType.upload_photo: {
                for (int i = 0; i < mPhoto.size(); ++i) {
                    // FIXME: 2018/2/5 未完成
                    String photo = mPhoto.get(i);
                    exeNetworkReq(mType, MeetingAPI.picture(photo, i).build());
                }
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws JSONException {
        if (id == ReqType.advert) {
            return JsonParser.ev(resp.getText(), Advert.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        switch (id) {
            case ReqType.logout: {
                if (r.isSucceed()) {
                    //清空个人信息，把极光绑定改为false 登录后需要重新绑定
                    SpUser.inst().clear();
                    SpJPush.inst().jPushIsRegister(false);
                    Profile.inst().clear();

                    Notifier.inst().notify(NotifyType.logout);
                    YSLog.d(TAG, "退出账号成功");
                } else {
                    retryNetworkRequest(id);
                    YSLog.d(TAG, "退出账号失败");
                }
            }
            break;
            case ReqType.j_push: {
                if (r.isSucceed()) {
                    YSLog.d(TAG, "极光推送绑定成功");
                    SpJPush.inst().jPushRegisterId(mJPushRegisterId);
                    SpJPush.inst().jPushIsRegister(true);
                } else {
                    YSLog.d(TAG, "极光推送绑定失败");
                    retryNetworkRequest(id);
                    SpJPush.inst().jPushIsRegister(false);
                }
            }
            break;
            case ReqType.exit_record: {
                if (r.isSucceed()) {
                    YSLog.d(TAG, "退出录音成功");
                } else {
                    YSLog.d(TAG, "退出录音失败");
                }
            }
            break;
            case ReqType.share_delete_meet: {
                if (r.isSucceed()) {
                    Notifier.inst().notify(NotifyType.delete_meeting_success, mCourseId);
                } else {
                    Notifier.inst().notify(NotifyType.delete_meeting_fail, r.getError().getMessage());
                }
            }
            break;
            case ReqType.advert: {
                if (r.isSucceed()) {
                    Advert data = (Advert) r.getData();
                    SpApp.inst().saveAdvert(data);
                }
            }
            break;
            case ReqType.over_live: {
                if (r.isSucceed()) {
                    YSLog.d(TAG, "结束直播成功");
                    Notifier.inst().notify(NotifyType.over_live, mCourseId);
                } else {
                    YSLog.d(TAG, "结束直播失败重试");
                    retryNetworkRequest(id);
                }
            }
            break;
            case ReqType.upload_audio: {
                if (r.isSucceed()) {
                    YSLog.d(TAG, "音频上传成功");
                } else {
                    YSLog.d(TAG, "音频上传失败");
                    // 上传失败就重试
                    retryNetworkRequest(id);
                }
            }
            break;
            case ReqType.upload_photo: {
                if (r.isSucceed()) {
                    YSLog.d(TAG, "上传图片成功");
                    Picture picture = (Picture) r.getData();
                    int meetId = ConstantsEx.KInvalidValue;
                    if (picture != null) {
                        meetId = picture.getInt(TPicture.id);
                    }
                    Notifier.inst().notify(NotifyType.update_photo, meetId);
                } else {
                    YSLog.d(TAG, "上传图片失败");
                    // 上传失败就重试
                    retryNetworkRequest(id);
                }
            }
            break;
        }
    }
}
