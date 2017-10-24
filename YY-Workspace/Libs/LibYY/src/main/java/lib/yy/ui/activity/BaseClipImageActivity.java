package lib.yy.ui.activity;

import android.graphics.Bitmap;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import inject.annotation.router.Arg;
import lib.ys.fitter.DpFitter;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.view.photoViewer.NetworkPhotoView;
import lib.yy.R;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * 裁剪图片
 *
 * @auther HuoXuYu
 * @since 2017/9/22
 */
abstract public class BaseClipImageActivity extends BaseActivity {

    private static final int KBmpSize = DpFitter.dp(280);

    public static Bitmap mBmp;
    private NetworkPhotoView mPv;

    @Arg
    public String mPath;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_clip_image;
    }

    protected void clip() {
        int screenW = mPv.getWidth();
        int screenH = mPv.getHeight();
        int startX = (screenW - KBmpSize) / 2;
        int startY = (screenH - KBmpSize) / 2;

        mPv.setDrawingCacheEnabled(true);
        mPv.buildDrawingCache();
        Bitmap bmp = mPv.getDrawingCache();
        if (bmp != null) {
            mBmp = Bitmap.createBitmap(bmp, startX, startY, KBmpSize, KBmpSize, null, false);
            afterClip();
        } else {
            showToast("未知错误");
        }
        mPv.destroyDrawingCache();
    }

    abstract protected void afterClip();

    @CallSuper
    @Override
    public void findViews() {
        mPv = findView(R.id.clip_image_pv);
    }

    @CallSuper
    @Override
    public void setViews() {
        mPv.storage(mPath).load();
    }

    public static void recycleBmp() {
        BmpUtil.recycle(mBmp);
    }
}
