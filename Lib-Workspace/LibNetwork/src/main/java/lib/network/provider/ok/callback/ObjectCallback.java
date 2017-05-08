package lib.network.provider.ok.callback;

import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;

import java.io.IOException;

import lib.network.LogNetwork;
import lib.network.error.CallbackEmptyError;
import lib.network.error.CancelError;
import lib.network.error.ConnectionNetError;
import lib.network.error.ParseNetError;
import lib.network.model.NetworkMethod;
import lib.network.model.NetworkResponse;
import lib.network.provider.Delivery;
import lib.network.provider.IRequestBuilder;
import lib.network.provider.ok.request.UploadBuilder.DeleteOnExit;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @author yuansui
 */
public class ObjectCallback extends Callback<Object> {

    private IRequestBuilder mBuilder;
    private Delivery mDelivery;

    public ObjectCallback(IRequestBuilder builder, Delivery delivery) {
        mBuilder = builder;
        mDelivery = delivery;
    }

    @Override
    public void inProgress(float progress, long total, int id) {
        if (builder().method() == NetworkMethod.upload) {
            mDelivery.deliverProgress(builder(), progress * 100, total);
        }
    }

    @Override
    public Object parseNetworkResponse(Response response, int id) throws Exception {
        if (response.isSuccessful()) {
            // 直接在子线程调用, 在子线程解析
            return builder().listener().onNetworkResponse(id, getNetworkResponse(response));
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
        if (mBuilder.listener() != null) {
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

    private NetworkResponse getNetworkResponse(Response response) {
        NetworkResponse r = new NetworkResponse();
        String text = null;
        try {
            text = response.body().string();
            r.setText(text);
            LogNetwork.d("response body = " + text);
        } catch (IOException e) {
        }

        return r;
    }

    protected IRequestBuilder builder() {
        return mBuilder;
    }
}
