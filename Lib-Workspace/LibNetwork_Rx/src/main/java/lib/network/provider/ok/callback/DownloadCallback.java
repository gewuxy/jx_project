package lib.network.provider.ok.callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import lib.network.LogNetwork;
import lib.network.model.err.NetError;
import lib.network.model.interfaces.OnNetworkListener;
import lib.network.provider.NativeListener;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @auther yuansui
 * @since 2017/6/11
 */

public class DownloadCallback extends OkCallback {

    private String mFileName;
    private String mFileDir;

    public DownloadCallback(OnNetworkListener l, String dir, String fileName) {
        super(l);

        mFileDir = dir;
        mFileName = fileName;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Integer id = (Integer) call.request().tag();

        FileOutputStream os = null;
        try {
            File dir = new File(mFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            os = new FileOutputStream(new File(mFileDir, mFileName));
            os.write(response.body().bytes());

            NativeListener.inst().onSuccess(id, null, getListener());
        } catch (Exception e) {
            LogNetwork.e(e);
            NativeListener.inst().onError(id, new NetError(id, e.getMessage()), getListener());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    LogNetwork.e(e);
                }
            }
        }
    }
}
