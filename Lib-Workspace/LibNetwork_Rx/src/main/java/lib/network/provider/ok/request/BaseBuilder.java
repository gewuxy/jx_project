package lib.network.provider.ok.request;

import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.List;

import lib.network.LogNetwork;
import lib.network.NetworkUtil;
import lib.network.model.NetworkReq;
import lib.network.model.interfaces.OnNetworkListener;
import lib.network.model.param.CommonPair;
import lib.network.provider.IBuilder;

/**
 * @author yuansui
 */
abstract public class BaseBuilder implements IBuilder {

    private OkHttpRequestBuilder mBuilder;
    private NetworkReq mReq;
    private int mId;
    private Object mTag;
    private OnNetworkListener mListener;

    public BaseBuilder(NetworkReq req, Object tag, int id, OnNetworkListener l) {
        mReq = req;
        mId = id;
        mTag = tag;
        mListener = l;

        mBuilder = initBuilder();
        mBuilder.id(id);
        mBuilder.tag(tag);

        if (LogNetwork.isDebug()) {
            String logUrl = NetworkUtil.generateGetUrl(req.getUrl(), req.getParams());
            LogNetwork.d(String.valueOf(getMethod()) + " = " + logUrl);
        }

        /**
         * 添加header
         */
        List<CommonPair> headers = req.getHeaders();
        if (headers != null) {
            for (CommonPair header : headers) {
                mBuilder.addHeader(header.getName(), header.getVal());
            }
        }
    }

    abstract protected OkHttpRequestBuilder initBuilder();

    @Override
    public NetworkReq getReq() {
        return mReq;
    }

    @Override
    public RequestCall build() {
        return mBuilder.build();
    }

    @Override
    public int getId() {
        return mId;
    }

    public Object getTag() {
        return mTag;
    }

    @Override
    public OnNetworkListener getListener() {
        return mListener;
    }
}
