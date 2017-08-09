package lib.network.provider.ok.callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import lib.network.NetworkLog;
import lib.network.model.NetworkErrorBuilder;
import lib.network.model.interfaces.OnNetworkListener;
import lib.network.provider.NativeListener;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 返回的bytes直接存为文件
 *
 * @auther yuansui
 * @since 2017/6/11
 */

public class DownloadFileCallback extends OkCallback {

    private String mFileName;
    private String mFileDir;

    public DownloadFileCallback(OnNetworkListener l, String dir, String fileName) {
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
            NetworkLog.e(e);
            NativeListener.inst().onError(id,
                    NetworkErrorBuilder.create().code(id).exception(e).message(e.getMessage()).build(),
                    getListener());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    NetworkLog.e(e);
                }
            }
        }
    }
}
