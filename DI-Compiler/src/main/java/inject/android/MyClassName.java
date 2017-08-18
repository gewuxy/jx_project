package inject.android;

import com.squareup.javapoet.ClassName;

/**
 * @auther yuansui
 * @since 2017/8/16
 */

public interface MyClassName {
    ClassName KNetwork = ClassName.get("lib.network", "Network");
    ClassName KNetworkReq = ClassName.get("lib.network.model", "NetworkReq");
    ClassName KNetworkReqBuilder = ClassName.get("lib.network.model.NetworkReq", "Builder");
}
