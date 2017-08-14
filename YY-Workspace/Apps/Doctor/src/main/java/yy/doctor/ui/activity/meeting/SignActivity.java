package yy.doctor.ui.activity.meeting;

import android.widget.ImageView;

import lib.processor.annotation.AutoIntent;
import lib.processor.annotation.Extra;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 签到界面
 *
 * @author : GuoXuan
 * @since : 2017/5/2
 */
@AutoIntent
public class SignActivity extends BaseResultActivity {

    private final int KErrorResId = R.mipmap.result_ic_defeat; // 失败的图片
    private final int KErrorColId = R.color.text_333; // 失败的颜色

    @Extra(optional = true)
    String mLongitude; // 经度
    @Extra(optional = true)
    String mLatitude; // 维度
    @Extra(optional = true)
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
    public void setViews() {
        super.setViews();

        getDataFromNet();
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

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            getDataFromNet();
        }
        return true;
    }

    private void getDataFromNet() {
        refresh(RefreshWay.embed);
        exeNetworkReq(NetFactory.sign()
                .meetId(mMeetId)
                .moduleId(mModuleId)
                .positionId(mSignId)
                .signLat(mLatitude)
                .signLng(mLongitude)
                .builder());
    }
}
