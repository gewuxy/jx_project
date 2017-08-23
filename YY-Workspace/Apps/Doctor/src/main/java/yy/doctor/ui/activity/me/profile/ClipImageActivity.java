package yy.doctor.ui.activity.me.profile;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.view.photoViewer.NetworkPhotoView;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.me.UpHeadImage;
import yy.doctor.model.me.UpHeadImage.TUpHeadImage;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.UserAPI;
import yy.doctor.util.Util;

/**
 * 裁剪图片
 *
 * @author CaiXiang
 * @since 2017/5/2
 */
@Route
public class ClipImageActivity extends BaseActivity {

    public static Bitmap mBmp;
    private final int KBmpSize = fitDp(280);

    private NetworkPhotoView mPv;

    @Arg
    String mPath;

    @Override
    public void initData() {
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

                // 网络上传图片
                refresh(RefreshWay.dialog);
                exeNetworkReq(UserAPI.upload(BmpUtil.toBytes(mBmp)).build());
            } else {
                // FIXME: 产品，这里如何处理？
                showToast("未知错误");
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

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), UpHeadImage.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<UpHeadImage> r = (Result<UpHeadImage>) result;
        if (r.isSucceed()) {
            stopRefresh();

            UpHeadImage upHeadImage = r.getData();
            //头像路径保存到本地
            Profile.inst().update(Profile.inst().put(TProfile.headimg, upHeadImage.getString(TUpHeadImage.url)));
            Profile.inst().saveToSp();

            setResult(RESULT_OK, getIntent());
            finish();
            showToast(R.string.user_save_success);
        } else {
            onNetworkError(id, r.getError());
        }
    }
}
