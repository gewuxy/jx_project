package lib.network.provider;

import lib.network.model.NetworkListener;
import lib.network.model.NetworkMethod;
import lib.network.model.NetworkRequest;

/**
 * @author yuansui
 */
public interface IRequestBuilder {
    /**
     * 请求的id(what)
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
    int method();

    NetworkRequest request();

    NetworkListener listener();

    <T> T build();
}
