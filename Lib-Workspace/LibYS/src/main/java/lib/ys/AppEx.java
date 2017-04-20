package lib.ys;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import lib.network.model.NetworkRequest;
import lib.ys.network.image.NetworkImageView;
import lib.ys.util.DeviceUtil;
import lib.ys.util.TextUtil;

/**
 * @author yuansui
 */
abstract public class AppEx extends Application {

    protected String TAG = getClass().getSimpleName();

    protected static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

        mToast = initToast();

        NetworkImageView.init(this, getNetworkImageCacheDir(), (int) (DeviceUtil.getRuntimeMaxMemory() / 8));

        NetworkRequest.init(this);

        setParams();
    }

    /**
     * 设置配置参数, 包括标题栏等
     */
    abstract protected void setParams();

    /**
     * 返回getApplicationContext()
     *
     * @return sContext
     */
    public static Context ct() {
        return sContext;
    }

    /**
     * 返回getApplicationContext()
     *
     * @return sContext
     */
    public static Context getContext() {
        return sContext;
    }

    public static ContentResolver getExContentResolver() {
        return sContext.getContentResolver();
    }

    private static Toast mToast = null;

    protected Toast initToast() {
        return Toast.makeText(sContext, "", Toast.LENGTH_SHORT);
    }

    public static void showToast(String content) {
        if (TextUtil.isEmpty(content)) {
            return;
        }
        mToast.setText(content);
        mToast.show();
    }

    public static void showToast(@StringRes int... ids) {
        if (ids == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ids.length; ++i) {
            builder.append(sContext.getString(ids[i]));
        }
        mToast.setText(builder.toString());
        mToast.show();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        NetworkImageView.clearMemoryCache(sContext);
        System.gc();
    }

    abstract protected String getNetworkImageCacheDir();

    public void doDestroy() {
    }
}
