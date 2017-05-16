package lib.network.provider;

import lib.network.model.NetworkMethod;
import lib.network.model.NetworkReq;
import lib.network.model.interfaces.OnNetworkListener;

/**
 * @author yuansui
 */
public interface IBuilder {
    /**
     * 请求的id
     *
     * @return
     */
    int id();

    /**
     * 绑定的tag(class name)
     *
     * @return
     */
    Object tag();

    @NetworkMethod
    int getMethod();

    NetworkReq getReq();

    OnNetworkListener getListener();

    <T> T build();
}
