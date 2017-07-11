package yy.doctor.ui.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.res.ResLoader;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 签到界面
 *
 * @author : GuoXuan
 * @since : 2017/5/2
 */
public class SignActivity extends BaseResultActivity {

    private final int KErrorResId = R.mipmap.result_ic_defeat; // 失败的图片
    private final int KErrorColId = R.color.text_333; // 失败的颜色

    private String mLongitude; // 经度
    private String mLatitude; // 维度
    private String mSignId; // 签到id

    private ImageView mIvResult;//结果图标

    public static void nav(Context context, String meetId, String moduleId, String signId, String latitude, String longitude) {
        Intent i = new Intent(context, SignActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KModuleId, moduleId)
                .putExtra(Extra.KData, signId)
                .putExtra(Extra.KLatitude, latitude)
                .putExtra(Extra.KLongitude, longitude);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        super.initData();

        mLatitude = getIntent().getStringExtra(Extra.KLatitude);
        mLongitude = getIntent().getStringExtra(Extra.KLongitude);
        mSignId = getIntent().getStringExtra(Extra.KData);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "签到", this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mIvResult = findView(R.id.result_iv_result);
    }

    @Override
    public void setViews() {
        super.setViews();

        refresh(RefreshWay.embed);
        exeNetworkReq(NetFactory.sign()
                .meetId(mMeetId)
                .moduleId(mModuleId)
                .positionId(mSignId)
                .signLat(mLatitude)
                .signLng(mLongitude)
                .builder());
    }

    @Override
    protected void successResult() {
        mTvResult.setText("签到成功");
    }

    @Override
    protected void errorResult(String error) {
        mIvResult.setImageResource(KErrorResId);
        mTvResult.setTextColor(ResLoader.getColor(KErrorColId));
        mTvResult.setText("签到失败");
        mTvResultMsg.setText(error);
    }

}
