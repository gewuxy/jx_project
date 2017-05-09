package lib.network.provider.ok.request;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;

import lib.network.NetworkUtil;
import lib.network.model.OnNetworkListener;
import lib.network.model.NetworkMethod;
import lib.network.model.NetworkRequest;

/**
 * @author yuansui
 */
public class GetBuilder extends BaseBuilder {

    public GetBuilder(NetworkRequest request, Object tag, int id, OnNetworkListener listener) {
        super(request, tag, id, listener);
    }

    @Override
    protected OkHttpRequestBuilder initBuilder() {
        String url = NetworkUtil.generateGetUrl(request().getUrl(), request().getParams());
        return OkHttpUtils.get().url(url);
    }

    @Override
    @NetworkMethod
    public int method() {
        return NetworkMethod.get;
    }
}
