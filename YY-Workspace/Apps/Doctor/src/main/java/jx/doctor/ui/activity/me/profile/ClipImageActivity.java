package jx.doctor.ui.activity.me.profile;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.view.photoViewer.NetworkPhotoView;
import lib.jx.ui.activity.base.BaseActivity;
import jx.doctor.R;
import jx.doctor.model.Profile;
import jx.doctor.model.Profile.TProfile;
import jx.doctor.model.me.UpHeadImage;
import jx.doctor.model.me.UpHeadImage.TUpHeadImage;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.UserAPI;
import jx.doctor.util.Util;

/**
 * 裁剪图片
 *
 * @author CaiXiang
 * @since 2017/5/2
 */
@Route
public class ClipImageActivity extends BaseActivity {

    public static Bitmap mBmp;
    private final int KBmpSize = fit(280);

    private NetworkPhotoView mPv;

    @Arg
    String mPath;

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_clip_image;
    }

    @Override
    public void initNavBar(NavBar bar) {

        bar.setBackgroundResource(R.color.black);
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
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), UpHeadImage.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            stopRefresh();

            UpHeadImage upHeadImage = (UpHeadImage) r.getData();
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
