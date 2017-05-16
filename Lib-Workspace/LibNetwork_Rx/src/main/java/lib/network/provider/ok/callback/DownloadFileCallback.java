package lib.network.provider.ok.callback;

import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import lib.network.LogNetwork;
import lib.network.model.err.CallbackEmptyError;
import lib.network.model.err.ConnectionNetError;
import lib.network.model.err.ParseNetError;
import lib.network.provider.Delivery;
import lib.network.provider.IBuilder;
import okhttp3.Call;

/**
 * @author yuansui
 */
public class DownloadFileCallback extends FileCallBack {

    private IBuilder mBuilder;
    private Delivery mDelivery;

    public DownloadFileCallback(IBuilder builder, Delivery delivery) {
        super(builder.getReq().getDir(), builder.getReq().getFileName());

        mBuilder = builder;
        mDelivery = delivery;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        mDelivery.deliverError(mBuilder, new ConnectionNetError(e.getMessage()));
    }

    @Override
    public void onResponse(File response, int id) {
        if (mBuilder.getListener() != null) {
            if (response != null) {
                mDelivery.deliverSuccess(mBuilder, response);
            } else {
                LogNetwork.d("数据解析错误");
                mDelivery.deliverError(mBuilder, new ParseNetError("数据解析错误"));
            }
        } else {
            LogNetwork.d("没有回调");
            mDelivery.deliverError(mBuilder, new CallbackEmptyError());
        }
    }

    @Override
    public void inProgress(float progress, long total, int id) {
        mDelivery.deliverProgress(mBuilder, progress * 100, total);
    }
}
