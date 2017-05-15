package lib.network.provider.ok.request;

import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.List;

import lib.network.LogNetwork;
import lib.network.NetworkUtil;
import lib.network.model.NetworkReq;
import lib.network.model.OnNetworkListener;
import lib.network.param.CommonPair;
import lib.network.provider.IRequestBuilder;

/**
 * @author yuansui
 */
abstract public class BaseBuilder implements IRequestBuilder {

    private OkHttpRequestBuilder mBuilder;
    private NetworkReq mNetRequest;
    private int mId;
    private Object mTag;
    private OnNetworkListener mListener;

    public BaseBuilder(NetworkReq request, Object tag, int id, OnNetworkListener listener) {
        mNetRequest = request;
        mId = id;
        mTag = tag;
        mListener = listener;

        mBuilder = initBuilder();
        mBuilder.id(id);
        mBuilder.tag(tag);

        if (LogNetwork.isDebug()) {
            String logUrl = NetworkUtil.generateGetUrl(request.getUrl(), request.getParams());
            LogNetwork.d(String.valueOf(method()) + " = " + logUrl);
        }

        /**
         * 添加header
         */
        List<CommonPair> headers = request.getHeaders();
        if (headers != null) {
            for (CommonPair header : headers) {
                mBuilder.addHeader(header.getName(), header.getValue());
            }
        }
    }

    abstract protected OkHttpRequestBuilder initBuilder();

    @Override
    public NetworkReq request() {
        return mNetRequest;
    }

    @Override
    public RequestCall build() {
        return mBuilder.build();
    }

    @Override
    public int id() {
        return mId;
    }

    public Object tag() {
        return mTag;
    }

    @Override
    public OnNetworkListener listener() {
        return mListener;
    }
}
