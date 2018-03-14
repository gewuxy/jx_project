package jx.csp.ui.activity.me;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import jx.csp.R;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;

/**
 * 清理缓存
 *
 * @author CaiXiang
 * @since 2018/3/14
 */

public class ClearCacheActivity extends BaseActivity {

    private final String KM = "M";

    @IntDef({
            ClearType.img,
            ClearType.audio
    })
    public @interface ClearType {
        int img = 0;
        int audio = 1;
    }

    private TextView mTvImgSize;
    private TextView mTvAudioSize;

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_clear_cache;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.clear_cache, this);
    }

    @Override
    public void findViews() {
        mTvImgSize = findView(R.id.clear_cache_tv_img_size);
        mTvAudioSize = findView(R.id.clear_cache_tv_audio_size);
    }

    @Override
    public void setViews() {
        mTvImgSize.setText(getFolderSize(CacheUtil.getBmpCacheDir(), CacheUtil.getUploadCacheDir()));
        mTvAudioSize.setText(getFolderSize(CacheUtil.getAudioCacheDir()));
        setOnClickListener(R.id.clear_cache_tv_clear_img);
        setOnClickListener(R.id.clear_cache_tv_clear_audio);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_cache_tv_clear_img: {
                clearCache(ClearType.img, CacheUtil.getBmpCacheDir(), CacheUtil.getUploadCacheDir());
            }
            break;
            case R.id.clear_cache_tv_clear_audio: {
                clearCache(ClearType.audio, CacheUtil.getAudioCacheDir());
            }
            break;
        }
    }

    public String getFolderSize(String... path) {
        float size = 0.0f;
        try {
            for (String s : path) {
                size += FileUtil.getFolderSize(new File(s));
            }
        } catch (Exception e) {

        }
        size /= (2 << 19);
        if (size >= 0.1f) {
            return String.format("%.1f".concat(KM), size);
        } else {
            return 0.0 + KM;
        }
    }

    public void clearCache(@ClearType int type, String... folderPath) {
        Util.runOnSubThread(() -> {
            boolean result = true;
            for (int i = 0; i < folderPath.length; ++i) {
                if (!FileUtil.delFolder(folderPath[i])) {
                    result = false;
                    break;
                }
            }

            boolean r = result;
            Util.runOnUIThread(() -> {
                if (r) {
                    showToast(R.string.setting_clear_succeed);
                    if (type == ClearType.img) {
                        mTvImgSize.setText("0.0" + KM);
                    } else {
                        mTvAudioSize.setText("0.0" + KM);
                    }
                } else {
                    showToast(R.string.setting_clear_error);
                }
            });
        });
    }
}
