package yy.doctor.ui.activity.me.profile;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;

import lib.ys.ui.other.NavBar;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.view.photoViewer.NetworkPhotoView;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 裁剪图片
 *
 * @author CaiXiang
 * @since 2017/5/2
 */
public class ClipImageActivity extends BaseActivity {

    public static Bitmap mBmp;
    private final int KBmpSize = fitDp(280);

    private NetworkPhotoView mPv;
    private String mPath;

    @Override
    public void initData() {
        mPath = getIntent().getStringExtra(Extra.KData);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_clip_image;
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, R.string.avatar, this);
        bar.addTextViewRight(R.string.confirm, v -> {

            int screenW = mPv.getWidth();
            int screenH = mPv.getHeight();
            int startX = (screenW - KBmpSize) / 2;
            int startY = (screenH - KBmpSize) / 2;

            mPv.setDrawingCacheEnabled(true);
            mPv.buildDrawingCache();
            Bitmap bmp = mPv.getDrawingCache();
            if (bmp != null) {
                mBmp = Bitmap.createBitmap(bmp, startX, startY, KBmpSize, KBmpSize, null, false);
                setResult(RESULT_OK, getIntent());
                finish();
                //ImageView img = findView(R.id.img);
                //img.setImageBitmap(mBmp);
            }
            mPv.destroyDrawingCache();
        });
    }

    @Override
    public void findViews() {
        mPv = findView(R.id.clip_image_pv);
    }

    @Override
    public void setViews() {
        setBackgroundColor(Color.BLACK);
        mPv.storage(mPath).load();
    }

    public static void recycleBmp() {
        BmpUtil.recycle(mBmp);
    }

}
