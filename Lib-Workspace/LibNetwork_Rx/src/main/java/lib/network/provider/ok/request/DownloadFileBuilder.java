package lib.network.provider.ok.request;

import lib.network.model.OnNetworkListener;
import lib.network.model.NetworkMethod;
import lib.network.model.NetworkRequest;

/**
 * @author yuansui
 */
public class DownloadFileBuilder extends GetBuilder {

    public DownloadFileBuilder(NetworkRequest request, Object tag, int id, OnNetworkListener listener) {
        super(request, tag, id, listener);
    }

    @Override
    @NetworkMethod
    public int method() {
        return NetworkMethod.download_file;
    }
}
