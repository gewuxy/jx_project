package lib.zego;

/**
 * @author CaiXiang
 * @since 2017/10/26
 */
abstract public class IZegoCallback {

    /**
     * 登陆房间回掉
     *
     * @param i
     * @param stream
     */
    abstract public void onLoginCompletion(int i, String stream);

    /**
     * 推流状态更新  i  0:成功, 其它:失败
     *
     * @param i
     */
    public void onPublishStateUpdate(int i) {}

    /**
     * 因为登陆抢占原因等被挤出房间
     */
    public void onKickOut() {}

    /**
     * 直播间的观众人数获取
     *
     * @param number
     */
    abstract public void onUserUpdate(int number);
}