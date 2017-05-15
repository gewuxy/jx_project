package lib.network.provider.ok.request;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;

import lib.network.NetworkUtil;
import lib.network.model.NetworkMethod;
import lib.network.model.NetworkReq;
import lib.network.model.OnNetworkListener;

/**
 * @author yuansui
 */
public class GetBuilder extends BaseBuilder {

    public GetBuilder(NetworkReq request, Object tag, int id, OnNetworkListener l) {
        super(request, tag, id, l);
    }

    @Override
    protected OkHttpRequestBuilder initBuilder() {
        String url = NetworkUtil.generateGetUrl(getReq().getUrl(), getReq().getParams());
        return OkHttpUtils.get().url(url);
    }

    @Override
    @NetworkMethod
    public int getMethod() {
        return NetworkMethod.get;
    }
}
