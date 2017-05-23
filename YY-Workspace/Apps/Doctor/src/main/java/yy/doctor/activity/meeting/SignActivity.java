package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TimeUtil;
import lib.ys.util.TimeUtil.TimeFormat;
import lib.ys.util.res.ResLoader;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 签到界面
 *
 * @author : GuoXuan
 * @since : 2017/5/2
 */
public class SignActivity extends BaseActivity {

    private static final int KSecond = 1;//间隔
    private static final int KReturnTime = 3;//关闭倒计时

    private final int KSucceedResId = R.mipmap.sign_ic_success;//成功的图片
    private final int KErrorResId = R.mipmap.sign_ic_defeat;//失败的图片
    private final int KSucceedColId = R.color.text_0882e7;//成功的颜色
    private final int KErrorColId = R.color.text_333;//失败的颜色

    private String mMeetId;//会议id
    private String mModuleId;//模块id
    private String mLongitude;//经度
    private String mLatitude;//维度
    private String mId;//签到id

    private ImageView mIvResult;//结果图标
    private TextView mTvResult;//签到结果
    private TextView mTvResultMsg;//成功的时间/失败的原因
    private TextView mTvReturn;//返回

    @Override
    public void initData() {
        mLatitude = getIntent().getStringExtra(Extra.KLatitude);
        mLongitude = getIntent().getStringExtra(Extra.KLongitude);
        mId = getIntent().getStringExtra(Extra.KData);
        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
        mModuleId = getIntent().getStringExtra(Extra.KModuleId);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_sign;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "签到", this);
    }

    @Override
    public void findViews() {
        mIvResult = findView(R.id.sign_iv_result);
        mTvResult = findView(R.id.sign_tv_result);
        mTvResultMsg = findView(R.id.sign_tv_result_msg);
        mTvReturn = findView(R.id.sign_tv_return);
    }

    @Override
    public void setViews() {
        setOnClickListener(mTvReturn);

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
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        Result r = (Result) result;
        if (r.isSucceed()) {
            //签到成功
            mIvResult.setImageResource(KSucceedResId);
            mTvResult.setTextColor(ResLoader.getColor(KSucceedColId));
            mTvResult.setText("签到已成功");

            long signTime = System.currentTimeMillis();
            mTvResultMsg.setText("时间 " + TimeUtil.formatMilli(signTime, TimeFormat.from_h_to_m_24));
            onFinish();
        } else {
            //签到失败
            mIvResult.setImageResource(KErrorResId);
            mTvResult.setTextColor(ResLoader.getColor(KErrorColId));
            mTvResult.setText("签到失败");
            mTvResultMsg.setText(r.getError());
            onFinish();
        }
    }

    /**
     * 倒计时
     */
    private void onFinish() {
        Observable.intervalRange(0, KReturnTime + 1, 0, KSecond, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        mTvReturn.setText((KReturnTime - aLong) + "秒后返回");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {
                        finish();
                    }
                });
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_tv_return:
                finish();
                break;
        }
    }

}
