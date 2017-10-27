package lib.zego;

import android.support.annotation.IntDef;

import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.constants.ZegoVideoViewMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author CaiXiang
 * @since 2017/10/26
 */
abstract public class IZegoCallback {

    public interface Constants{
        int KSingleAnchor = ZegoConstants.PublishFlag.SingleAnchor;
        int KAspectFill = ZegoVideoViewMode.ScaleAspectFill;
        int KStreamAdd = 2001; // 流新增
        int KStreamDel = 2002; // 流删除
    }

    @IntDef({
            UserType.audience,
            UserType.anchor,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface UserType {
        int anchor = ZegoConstants.RoomRole.Anchor;
        int audience = ZegoConstants.RoomRole.Audience;
    }

    /**
     * 登陆房间回掉
     */
    abstract public void onLoginCompletion(int i, String stream);

    /**
     * 直播间的观众人数获取
     */
    abstract public void onUserUpdate(int number);

    /**
     * 推流状态更新  i  0:成功, 其它:失败
     */
    public void onPublishStateUpdate(int i) {}

    /**
     * 因为登陆抢占原因等被挤出房间
     */
    public void onKickOut() {}

    /**
     * 房间流列表更新
     */
    public void onStreamUpdated(int i,String stream) {}
}