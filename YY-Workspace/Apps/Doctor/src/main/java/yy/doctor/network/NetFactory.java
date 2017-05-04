package yy.doctor.network;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkRequest.Builder;
import lib.network.param.NameValuePair;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class NetFactory {

    private interface BaseParam {
        String device_os = "device_os";
    }

    private interface CommonParam {

    }


    /*********************************
     * 以下是工具
     */

    /**
     * 获取post请求
     *
     * @param url
     * @return
     */
    private static Builder newPost(String url) {
        return new Builder(UrlUtil.getBaseUrl() + url)
                .post()
                .param(getBaseParams());
    }

    /**
     * 获取get请求
     *
     * @param url
     * @return
     */
    private static Builder newGet(String url) {
        return new Builder(UrlUtil.getBaseUrl() + url)
                .get()
                .param(getBaseParams());
    }

    /**
     * 获取upload请求
     *
     * @param url
     * @return
     */
    private static Builder newUpload(String url) {
        return new Builder(UrlUtil.getBaseUrl() + url)
                .upload()
                .param(getBaseParams());
    }

    private static List<NameValuePair> getBaseParams() {
        List<NameValuePair> ps = new ArrayList<>();

        ps.add(newPair(BaseParam.device_os, "android"));

        return ps;
    }

    private static NameValuePair newPair(String key, Object value) {
        return new NameValuePair(key, value);
    }
}
