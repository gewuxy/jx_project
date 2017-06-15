package lib.network.provider.ok.task;

import lib.network.NetworkLog;
import lib.network.NetworkUtil;
import lib.network.model.NetworkReq;
import lib.network.provider.ok.callback.OkCallback;
import okhttp3.Request;

/**
 * @auther yuansui
 * @since 2017/6/10
 */

public class GetTask extends Task {


    public GetTask(int id, NetworkReq req, OkCallback callback) {
        super(id, req, callback);
    }

    @Override
    public Request buildRealReq() {
        String url = NetworkUtil.generateGetUrl(getReq().getUrl(), getReq().getParams());
        NetworkLog.d("url_get = " + url);

        Request.Builder reqBuilder = new Request.Builder()
                .tag(getId())
                .url(url)
                .get();

        addHeaders(reqBuilder);

        return reqBuilder.build();
    }
}
