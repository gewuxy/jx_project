package lib.ys.network.image.provider;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.StringDef;
import android.widget.ImageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.ConstantsEx;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.network.image.renderer.BaseRenderer;

/**
 * @author yuansui
 */
abstract public class BaseProvider {

    @StringDef({
            Scheme.http,
            Scheme.storage,
            Scheme.res,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Scheme {
        String http = "http";
        String storage = "/storage";
        String res = "res://";
    }


    protected Context mContext;
    protected ImageView mIv;

    /**
     * 以下是详细属性
     */
    private String mUrl;
    private int mW;
    private int mH;
    @DrawableRes
    private int mPlaceHolder = ConstantsEx.KInvalidValue;
    private int mFade = ConstantsEx.KInvalidValue;
    private BaseRenderer mRenderer;

    private NetworkImageListener mListener;


    public BaseProvider(Context context, ImageView iv) {
        mContext = context;
        mIv = iv;
    }

    public void url(String url) {
        mUrl = url;
    }

    public void renderer(BaseRenderer renderer) {
        mRenderer = renderer;
    }

    public void listener(NetworkImageListener listener) {
        mListener = listener;
    }

    public void placeHolderRid(@DrawableRes int id) {
        mPlaceHolder = id;
    }

    public void fade(int duration) {
        mFade = duration;
    }

    public void resize(@IntRange(from = 1, to = Integer.MAX_VALUE) int w, @IntRange(from = 1, to = Integer.MAX_VALUE) int h) {
        mW = w;
        mH = h;
    }

    protected String getUrl() {
        return mUrl;
    }

    protected int getW() {
        return mW;
    }

    protected int getH() {
        return mH;
    }

    protected int getPlaceHolder() {
        return mPlaceHolder;
    }

    protected int getFade() {
        return mFade;
    }

    protected BaseRenderer getRenderer() {
        return mRenderer;
    }

    protected NetworkImageListener getListener() {
        return mListener;
    }

    abstract public void clearFromCache(String url);

    abstract public void load();
}
