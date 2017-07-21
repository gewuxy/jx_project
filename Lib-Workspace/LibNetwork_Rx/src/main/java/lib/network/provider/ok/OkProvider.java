package lib.network.provider.ok;

import java.util.HashMap;
import java.util.Map;

import lib.network.NetworkLog;
import lib.network.NetworkUtil;
import lib.network.model.NetworkMethod;
import lib.network.model.NetworkReq;
import lib.network.model.interfaces.OnNetworkListener;
import lib.network.provider.BaseProvider;
import lib.network.provider.ok.callback.CommonCallback;
import lib.network.provider.ok.callback.DownloadCallback;
import lib.network.provider.ok.callback.DownloadFileCallback;
import lib.network.provider.ok.task.DownloadTask;
import lib.network.provider.ok.task.GetTask;
import lib.network.provider.ok.task.PostTask;
import lib.network.provider.ok.task.Task;
import lib.network.provider.ok.task.UploadTask;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @author yuansui
 */
public class OkProvider extends BaseProvider {

    private Map<Integer, Task> mTaskMap;

    public OkProvider(Object tag) {
        super(tag);

        mTaskMap = new HashMap<>();
    }

    @Override
    public void load(NetworkReq req, final int id, final OnNetworkListener lsn) {
        // FIXME: id的检测应该是在网络callback的时候进行, 暂时先放到这里, 如果出问题的话再更改
        if (getTask(id) != null) {
            if (getTask(id).isExecuted()) {
                mTaskMap.remove(id);
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
            case NetworkMethod.download: {
                task = new DownloadTask(id, req, new DownloadCallback(lsn));
            }
            break;
            case NetworkMethod.download_file: {
                task = new DownloadTask(id, req, new DownloadFileCallback(lsn, req.getDir(), req.getFileName()));
            }
            break;
        }

        if (task != null) {
            task.run();
            mTaskMap.put(id, task);
        }
    }

    @Override
    public WebSocket loadWebSocket(NetworkReq req, WebSocketListener lsn) {
        String url = NetworkUtil.generateGetUrl(req.getUrl(), req.getParams());
        NetworkLog.d("url_web socket = " + url);

        Request realReq = new Request.Builder()
                .url(url)
                .build();

        return OkClient.inst().newWebSocket(realReq, lsn);
    }

    @Override
    public void cancel(int id) {
        Task task = getTask(id);
        if (task != null) {
            task.cancel();
            mTaskMap.remove(id);
        }
    }

    @Override
    public void cancelAll() {
        for (Task task : mTaskMap.values()) {
            task.cancel();
        }
        mTaskMap.clear();
    }

    private Task getTask(int id) {
        return mTaskMap.get(id);
    }
}
