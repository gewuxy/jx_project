package jx.csp.ui.activity.me.profile;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.me.UpHeadImage;
import jx.csp.model.me.UpHeadImage.TUpHeadImage;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.view.photoViewer.NetworkPhotoView;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * 裁剪图片
 *
 * @auther HuoXuYu
 * @since 2017/9/22
 */
@Route
public class ClipImageActivity extends BaseActivity {

    public static Bitmap mBmp;
    private final int KBmpSize = fitDp(280);
    private NetworkPhotoView mPv;

    @Arg
    String mPath;

    private TextView mTV;

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
        bar.setBackgroundResource(R.color.white);
        Util.addBackIcon(bar, R.string.person_center_avatar, this);
        mTV = bar.addTextViewRight(R.string.confirm, v -> {

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
                // FIXME: 2017/9/25 头像接口
                refresh(RefreshWay.dialog);
                exeNetworkReq(UserAPI.upload(BmpUtil.toBytes(mBmp)).build());
            }else {
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
        mTV.setTextColor(ResLoader.getColor(R.color.text_167afe));
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
            showToast(R.string.my_message_save_success);
        } else {
            onNetworkError(id, r.getError());
        }
    }
}
