package lib.ys.network.image;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.util.AttributeSet;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultBitmapMemoryCacheParamsSupplier;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.io.File;

import lib.ys.network.image.interceptor.Interceptor;
import lib.ys.network.image.provider.BaseProvider;
import lib.ys.network.image.provider.FrescoProvider;
import lib.ys.network.image.renderer.Renderer;

public class NetworkImageView extends SimpleDraweeView {

    private BaseProvider mProvider;


    public NetworkImageView(Context context) {
        this(context, null, 0);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            setBackgroundColor(Color.BLUE);
            return;
        }
        init(context);
    }

    private void init(Context context) {
        mProvider = new FrescoProvider(context, this);
    }

    public NetworkImageView url(String url) {
        mProvider.url(url);
        return this;
    }

    public NetworkImageView storage(String s) {
        mProvider.storage(s);
        return this;
    }

    public NetworkImageView res(@DrawableRes int id) {
        mProvider.res(id);
        return this;
    }

    public NetworkImageView id(String id) {
        mProvider.id(id);
        return this;
    }

    public NetworkImageView contentProvider(String path) {
        mProvider.contentProvider(path);
        return this;
    }

    public NetworkImageView uri(Uri u) {
        mProvider.uri(u);
        return this;
    }

    public NetworkImageView renderer(Renderer renderer) {
        mProvider.renderer(renderer);
        return this;
    }

    public NetworkImageView addInterceptor(Interceptor i) {
        mProvider.addInterceptor(i);
        return this;
    }

    /**
     * 设置图片加载监听
     *
     * @param listener
     */
    public NetworkImageView listener(NetworkImageListener listener) {
        mProvider.listener(listener);
        return this;
    }

    public NetworkImageView placeHolder(@DrawableRes int id) {
        mProvider.placeHolderRid(id);
        return this;
    }

    public NetworkImageView fade(int duration) {
        mProvider.fade(duration);
        return this;
    }

    public NetworkImageView resize(@IntRange(from = 1, to = Integer.MAX_VALUE) int w, @IntRange(from = 1, to = Integer.MAX_VALUE) int h) {
        mProvider.resize(w, h);
        return this;
    }

    public void load() {
        mProvider.load();
    }

    /**
     * 初始化, 直接调用对应provider的初始化方法
     *
     * @param context
     * @param cacheDir           文件保存路径
     * @param maxMemoryCacheSize 最大内存使用数, 单位: byte
     */
    public static void init(Context context, String cacheDir, final int maxMemoryCacheSize) {
        if (cacheDir == null) {
            return;
        }

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(new File(cacheDir))
                .build();

        Supplier<MemoryCacheParams> supplier = new DefaultBitmapMemoryCacheParamsSupplier((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)) {

            @Override
            public MemoryCacheParams get() {
                return new MemoryCacheParams(
                        maxMemoryCacheSize,
                        256,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE);
            }
        };

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(context)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setBitmapMemoryCacheParamsSupplier(supplier)
                .setDownsampleEnabled(true)
//                .setCacheKeyFactory(cacheDir)
//                .setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier)
//                .setExecutorSupplier(executorSupplier)
//                .setImageCacheStatsTracker(imageCacheStatsTracker)
//                .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
//                .setNetworkFetchProducer(networkFetchProducer)
//                .setPoolFactory(poolFactory)
//                .setProgressiveJpegConfig(progressiveJpegConfig)
//                .setRequestListeners(requestListeners)
//                .setSmallImageDiskCacheConfig(smallImageDiskCacheConfig)
                .build();
        Fresco.initialize(context, config);
    }

    /**
     * 清理内存中cache
     *
     * @param context
     */
    public static void clearMemoryCache(Context context) {
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    public void clearFromCache(String url) {
        mProvider.clearFromCache(url);
    }
}
