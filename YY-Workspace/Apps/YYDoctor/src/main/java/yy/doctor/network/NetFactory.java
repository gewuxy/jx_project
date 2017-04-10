package yy.doctor.network;

import java.util.List;

import lib.network.model.NetworkRequest;
import lib.network.model.NetworkRequest.Builder;
import lib.network.param.NameValuePair;
import yy.doctor.network.UrlUtil.UrlMain;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class NetFactory {

    public static NetworkRequest home() {
        return newPost(UrlMain.KTttttt)
                .param("key", "val")
                .build();
    }

    private static Builder newPost(String url) {
        return new Builder(url)
                .post()
                .param(getBaseParams());
    }

    private static Builder newGet(String url) {
        return new Builder(url)
                .get()
                .param(getBaseParams());
    }

    private static List<NameValuePair> getBaseParams() {
        return null;
    }
}
