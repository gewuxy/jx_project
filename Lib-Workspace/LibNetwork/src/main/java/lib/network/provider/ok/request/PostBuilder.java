package lib.network.provider.ok.request;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import java.util.List;

import java8.lang.Iterables;
import lib.network.model.OnNetworkListener;
import lib.network.model.NetworkMethod;
import lib.network.model.NetworkRequest;
import lib.network.param.NameValuePair;

/**
 * @author yuansui
 */
public class PostBuilder extends BaseBuilder {

    public PostBuilder(NetworkRequest request, Object tag, int id, OnNetworkListener listener) {
        super(request, tag, id, listener);
    }

    @Override
    protected OkHttpRequestBuilder initBuilder() {
        String url = request().getUrl();
        List<NameValuePair> pairs = request().getParams();

        PostFormBuilder builder = OkHttpUtils.post().url(url);

        if (pairs != null) {
            Iterables.forEach(pairs, p -> builder.addParams(p.getName(), p.getValue()));
        }

        return builder;
    }

    @Override
    @NetworkMethod
    public int method() {
        return NetworkMethod.post;
    }
}
