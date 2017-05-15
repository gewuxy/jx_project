package lib.network.provider.ok.request;

import lib.network.model.NetworkMethod;
import lib.network.model.NetworkReq;
import lib.network.model.OnNetworkListener;

/**
 * @author yuansui
 */
public class DownloadFileBuilder extends GetBuilder {

    public DownloadFileBuilder(NetworkReq request, Object tag, int id, OnNetworkListener l) {
        super(request, tag, id, l);
    }

    @Override
    @NetworkMethod
    public int getMethod() {
        return NetworkMethod.download_file;
    }
}
