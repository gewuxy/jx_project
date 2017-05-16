package lib.network.model.interfaces;


import lib.network.model.NetworkResp;
import lib.network.model.err.CallbackEmptyError;
import lib.network.model.err.CancelError;
import lib.network.model.err.ConnectionNetError;
import lib.network.model.err.NetError;
import lib.network.model.err.ParseNetError;

/**
 * 网络任务监听
 *
 * @author yuansui
 */
public interface OnNetworkListener {
    /**
     * 任务数据回调
     *
     * @param id 编号
     * @param r  返回的数据
     * @return
     */
    Object onNetworkResponse(int id, NetworkResp r) throws Exception;

    /**
     * 任务成功
     *
     * @param id     编号
     * @param result 解析后的数据
     */
    void onNetworkSuccess(int id, Object result);

    /**
     * 任务错误, 类型如下:
     * <pre>
     * {@link ConnectionNetError} 网络错误
     * {@link ParseNetError} 网络错误
     * {@link CancelError} 取消任务
     * {@link CallbackEmptyError} 没有设置回调
     * <pre/>
     * @param id  编号
     * @param error 错误
     */
    void onNetworkError(int id, NetError error);

    /**
     * 任务下载或上传进度
     *
     * @param id        编号
     * @param progress  进度
     * @param totalSize 总大小
     */
    void onNetworkProgress(int id, float progress, long totalSize);
}
