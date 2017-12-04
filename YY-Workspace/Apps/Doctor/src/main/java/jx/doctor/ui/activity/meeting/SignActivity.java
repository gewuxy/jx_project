package jx.doctor.ui.activity.meeting;

import android.os.Bundle;
import android.widget.ImageView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkReq;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.jx.notify.Notifier.NotifyType;
import jx.doctor.R;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.util.Util;

/**
 * 签到界面
 *
 * @author : GuoXuan
 * @since : 2017/5/2
 */
@Route
public class SignActivity extends BaseResultActivity {

    private final int KErrorResId = R.drawable.result_ic_defeat; // 失败的图片
    private final int KErrorColId = R.color.text_333; // 失败的颜色

    @Arg(opt = true)
    String mLongitude; // 经度
    @Arg(opt = true)
    String mLatitude; // 维度
    @Arg(opt = true)
    String mSignId; // 签到id

    private ImageView mIvResult;//结果图标

    @Override
    public void initData() {
        super.initData();

        notify(NotifyType.study_start);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.sign, this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mIvResult = findView(R.id.result_iv_result);
    }

    @Override
    protected void getDataFromNet() {
        refresh(RefreshWay.embed);
        NetworkReq r = MeetAPI.sign()
                .meetId(mMeetId)
                .moduleId(mModuleId)
                .positionId(mSignId)
                .signLat(mLatitude)
                .signLng(mLongitude)
                .build();
        exeNetworkReq(r);
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
