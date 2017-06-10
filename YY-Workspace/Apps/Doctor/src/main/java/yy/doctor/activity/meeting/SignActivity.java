package yy.doctor.activity.meeting;

import android.widget.ImageView;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
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

    private final int KErrorResId = R.mipmap.result_ic_defeat;//失败的图片
    private final int KErrorColId = R.color.text_333;//失败的颜色

    private String mLongitude;//经度
    private String mLatitude;//维度
    private String mId;//签到id

    private ImageView mIvResult;//结果图标

    @Override
    public void initData() {
        super.initData();

        mLatitude = getIntent().getStringExtra(Extra.KLatitude);
        mLongitude = getIntent().getStringExtra(Extra.KLongitude);
        mId = getIntent().getStringExtra(Extra.KData);
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
                .positionId(mId)
                .signLat(mLatitude)
                .signLng(mLongitude)
                .builder());
    }

    @Override
    protected void successResult() {
        mTvResult.setText("签到已成功");
    }

    @Override
    protected void errorResult(String error) {
        mIvResult.setImageResource(KErrorResId);
        mTvResult.setTextColor(ResLoader.getColor(KErrorColId));
        mTvResult.setText("签到失败");
        mTvResultMsg.setText(error);
    }

}
