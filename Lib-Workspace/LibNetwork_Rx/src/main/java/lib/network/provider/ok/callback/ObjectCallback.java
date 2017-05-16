package lib.network.provider.ok.callback;

import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;

import java.io.IOException;

import lib.network.LogNetwork;
import lib.network.model.NetworkMethod;
import lib.network.model.NetworkResp;
import lib.network.model.err.CallbackEmptyError;
import lib.network.model.err.CancelError;
import lib.network.model.err.ConnectionNetError;
import lib.network.model.err.ParseNetError;
import lib.network.provider.Delivery;
import lib.network.provider.IBuilder;
import lib.network.provider.ok.request.UploadBuilder.DeleteOnExit;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @author yuansui
 */
public class ObjectCallback extends Callback<Object> {

    private IBuilder mBuilder;
    private Delivery mDelivery;

    public ObjectCallback(IBuilder builder, Delivery delivery) {
        mBuilder = builder;
        mDelivery = delivery;
    }

    @Override
    public void inProgress(float progress, long total, int id) {
        if (mBuilder.getMethod() == NetworkMethod.upload) {
            mDelivery.deliverProgress(mBuilder, progress * 100, total);
        }
    }

    @Override
    public Object parseNetworkResponse(Response response, int id) throws Exception {
        if (response.isSuccessful()) {
            // 直接在子线程调用, 在子线程解析
            return mBuilder.getListener().onNetworkResponse(id, toNetworkResp(response));
        } else {
            return null;
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (call.isCanceled()) {
            LogNetwork.e("cancel call = " + id);
            mDelivery.deliverError(mBuilder, new CancelError());
        } else {
            if (e instanceof JSONException || e instanceof NullPointerException) {
                mDelivery.deliverError(mBuilder, new ParseNetError(id, "数据解析错误", e));
            } else {
                mDelivery.deliverError(mBuilder, new ConnectionNetError(e.getMessage()));
            }
        }

        DeleteOnExit.inst().delete(mBuilder.tag(), mBuilder.id());
    }

    @Override
    public void onResponse(Object response, int id) {
        if (mBuilder.getListener() != null) {
            if (response != null) {
                mDelivery.deliverSuccess(mBuilder, response);
            } else {
                mDelivery.deliverError(mBuilder, new ParseNetError("数据解析错误"));
            }
        } else {
            LogNetwork.e("没有回调");
            mDelivery.deliverError(mBuilder, new CallbackEmptyError());
        }

        DeleteOnExit.inst().delete(mBuilder.tag(), mBuilder.id());
    }

    private NetworkResp toNetworkResp(Response response) {
        NetworkResp r = new NetworkResp();
        String text = null;
        try {
            text = response.body().string();
            r.setText(text);
            LogNetwork.d("response body = " + text);
        } catch (IOException e) {
        }

        return r;
    }
}
