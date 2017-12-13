package lib.live;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author CaiXiang
 * @since 2017/10/26
 */
abstract public class ILiveCallback {

    /*public interface Constants{
        int KSingleAnchor = ZegoConstants.PublishFlag.SingleAnchor; // 单主播
        int KAspectFill = ZegoVideoViewMode.ScaleAspectFill; // 全屏
        int KStreamAdd = 2001; // 流新增
        int KStreamDel = 2002; // 流删除
    }

    @IntDef({
            UserType.anchor,
            UserType.audience,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface UserType {
        int anchor = ZegoConstants.RoomRole.Anchor; // 主播
        int audience = ZegoConstants.RoomRole.Audience; // 观众
    }

    *//**
     * 登陆房间回调
     *//*
    abstract public void onLoginCompletion(int i, String stream);

    *//**
     * 直播间人数获取
     *//*
    abstract public void onUserUpdate(int number);

    *//**
     * 推流状态更新
     * @param i 0:成功, 其它:失败
     *//*
    public void onPublishStateUpdate(int i) {}

    *//**
     * 被挤出房间
     *//*
    public void onKickOut() {}

    *//**
     * 流列表更新
     *//*
    public void onStreamUpdated(int i,String stream) {}*/
}