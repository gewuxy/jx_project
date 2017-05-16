package lib.network.provider.ok.request;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import java.util.List;

import io.reactivex.Observable;
import lib.network.model.NetworkMethod;
import lib.network.model.NetworkReq;
import lib.network.model.interfaces.OnNetworkListener;
import lib.network.model.param.CommonPair;

/**
 * @author yuansui
 */
public class PostBuilder extends BaseBuilder {

    public PostBuilder(NetworkReq request, Object tag, int id, OnNetworkListener listener) {
        super(request, tag, id, listener);
    }

    @Override
    protected OkHttpRequestBuilder initBuilder() {
        String url = getReq().getUrl();
        List<CommonPair> pairs = getReq().getParams();

        PostFormBuilder builder = OkHttpUtils.post().url(url);

        if (pairs != null) {
            Observable.fromIterable(pairs)
                    .subscribe(p -> builder.addParams(p.getName(), p.getVal()));
        }

        return builder;
    }

    @Override
    @NetworkMethod
    public int getMethod() {
        return NetworkMethod.post;
    }
}
