package lib.network.provider.ok;

import android.util.SparseArray;

import lib.network.LogNetwork;
import lib.network.NetworkUtil;
import lib.network.model.NetworkMethod;
import lib.network.model.NetworkReq;
import lib.network.model.interfaces.OnNetworkListener;
import lib.network.provider.BaseProvider;
import lib.network.provider.ok.callback.CommonCallback;
import lib.network.provider.ok.callback.DownloadCallback;
import lib.network.provider.ok.task.DownloadTask;
import lib.network.provider.ok.task.GetTask;
import lib.network.provider.ok.task.PostTask;
import lib.network.provider.ok.task.Task;
import lib.network.provider.ok.task.UploadTask;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.WebSocketListener;

/**
 * @author yuansui
 */
public class OkProvider extends BaseProvider {

    private SparseArray<Call> mCallMap;

    public OkProvider(Object tag) {
        super(tag);

        mCallMap = new SparseArray<>();
    }

    @Override
    public void load(NetworkReq req, final int id, final OnNetworkListener lsn) {
        // FIXME: id的检测应该是在网络callback的时候进行, 暂时先放到这里, 如果出问题的话再更改
        if (mCallMap.get(id) != null) {
            if (mCallMap.get(id).isExecuted()) {
                mCallMap.remove(id);
            } else {
                return;
            }
        }

        Task task = null;
        switch (req.getMethod()) {
            case NetworkMethod.get: {
                task = new GetTask(id, req, new CommonCallback(lsn));
            }
            break;
            case NetworkMethod.post: {
                task = new PostTask(id, req, new CommonCallback(lsn));
            }
            break;
            case NetworkMethod.upload: {
                task = new UploadTask(id, req, new CommonCallback(lsn));
            }
            break;
            case NetworkMethod.download_file: {
                task = new DownloadTask(id, req, new DownloadCallback(lsn, req.getDir(), req.getFileName()));
            }
            break;
        }

        task.run();
    }

    @Override
    public void loadWebSocket(NetworkReq req, WebSocketListener lsn) {
        String url = NetworkUtil.generateGetUrl(req.getUrl(), req.getParams());
        LogNetwork.d("url_web socket = " + url);

        Request realReq = new Request.Builder()
                .url(url)
                .build();

        OkClient.inst().newWebSocket(realReq, lsn);
    }

    @Override
    public void cancel(int id) {
        Call c = mCallMap.get(id);
        if (c != null) {
            c.cancel();
            mCallMap.remove(id);
        }
    }

    @Override
    public void cancelAll() {
    }
}
