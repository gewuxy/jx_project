package yy.doctor.activity.me;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import lib.network.model.NetworkResponse;
import lib.ys.LogMgr;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.view.photoViewer.NetworkPhotoView;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Response;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.me.ClipImage;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 裁剪图片
 *
 * @author CaiXiang
 * @since 2017/5/2
 */
public class ClipImageActivity extends BaseActivity {

    private final int KBmpSize = fitDp(280);

    private NetworkPhotoView mPv;

    private String mPath;

    private Bitmap mBmp;

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

        Util.addBackIcon(bar, "头像", this);
        bar.addTextViewRight("确定", new OnClickListener() {

            @Override
            public void onClick(View v) {

                int screenW = mPv.getWidth();
                int screenH = mPv.getHeight();

                int startX = (screenW - KBmpSize) / 2;
                int startY = (screenH - KBmpSize) / 2;

                mPv.setDrawingCacheEnabled(true);
                mPv.buildDrawingCache();
                Bitmap bmp = mPv.getDrawingCache();

                if (bmp != null) {
                    mBmp = Bitmap.createBitmap(bmp, startX, startY, KBmpSize, KBmpSize, null, false);
                    ImageView img = findView(R.id.img);
                    img.setImageBitmap(mBmp);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    mBmp.compress(Bitmap.CompressFormat.PNG, 100, baos);

                    refresh(RefreshWay.dialog);
                    exeNetworkRequest(0 , NetFactory.upheadimg(baos.toByteArray()));
                }



                mPv.destroyDrawingCache();
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBmp != null) {
            mBmp.recycle();
            mBmp = null;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {

        LogMgr.d("www01" , nr.getText());
        return JsonParser.ev(nr.getText() , ClipImage.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        stopRefresh();

        Response<ClipImage> r = (Response<ClipImage>) result;
        if (r.isSucceed()) {
            showToast("头像修改成功");
        } else {
            showToast("头像修改失败");
        }

    }

}
