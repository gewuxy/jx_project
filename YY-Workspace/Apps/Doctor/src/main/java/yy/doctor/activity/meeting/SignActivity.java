package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import lib.network.model.err.NetError;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TimeUtil;
import lib.ys.util.TimeUtil.TimeFormat;
import lib.ys.util.res.ResLoader;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.BuildConfig;
import yy.doctor.Constants.ModuleId;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.meet.Sign;
import yy.doctor.model.meet.Sign.TSign;
import yy.doctor.model.meet.SignResult;
import yy.doctor.model.meet.SignResult.TSignResult;
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

    private static final int KSucceedResId = R.mipmap.sign_ic_success;//成功的图片
    private static final int KErrorResId = R.mipmap.sign_ic_defeat;//失败的图片
    private static final int KSucceedColId = R.color.text_0882e7;//成功的颜色
    private static final int KErrorColId = R.color.text_333;//失败的颜色
    private static final int KSecond = 1;//间隔
    private static final int KReturnTime = 3;//关闭倒计时
    private static final int KSign = 0;
    private static final int KToSign = 1;

    private String mMeetId;//会议id

    private ImageView mIvResult;//结果图标
    private TextView mTvResult;//签到结果
    private TextView mTvResultMsg;//成功的时间/失败的原因
    private TextView mTvReturn;

    private String mLongitude;//经度
    private String mLatitude;//维度

    public static void nav(Context context, String meetId, String latitude,String longitude) {
        Intent i = new Intent(context, SignActivity.class);
        i.putExtra(Extra.KData, meetId);
        i.putExtra(Extra.KLatitude,latitude);
        i.putExtra(Extra.KLongitude,longitude);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mLatitude = getIntent().getStringExtra(Extra.KLatitude);
        mLongitude = getIntent().getStringExtra(Extra.KLongitude);
        mMeetId = getIntent().getStringExtra(Extra.KData);
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
        mTvReturn.setOnClickListener(this);

        if (BuildConfig.TEST) {
            mMeetId = "17042512131640894904";
        }

        refresh(RefreshWay.embed);
        exeNetworkReq(KToSign, NetFactory.toSign(mMeetId, ModuleId.KSign));

    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KToSign) {
            return JsonParser.ev(r.getText(), Sign.class);
        } else {
            return JsonParser.ev(r.getText(), SignResult.class);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KToSign) {//获取签到信息
            Result<Sign> r = (Result<Sign>) result;
            if (r.isSucceed()) {
                Sign signData = r.getData();
                exeNetworkReq(KSign, NetFactory.sign()
                        .meetId(mMeetId)
                        .moduleId(ModuleId.KSign)
                        .positionId(signData.getString(TSign.id))
                        .signLat(mLatitude)
                        .signLng(mLongitude)
                        .builder());
            } else {
                //正常但失败
                setViewState(ViewState.normal);
                signError(r.getError());
            }
        } else {//签到,都正常
            setViewState(ViewState.normal);
            Result<SignResult> r = (Result<SignResult>) result;
            if (r.isSucceed()) {
                signSucceed(r.getData());
            } else {
                signError(r.getError());
            }
        }
    }

    /**
     * 签到成功
     */
    private void signSucceed(SignResult signResult) {
        mIvResult.setImageResource(KSucceedResId);
        mTvResult.setTextColor(ResLoader.getColor(KSucceedColId));
        mTvResult.setText("签到已成功");

        long signTime = signResult.getLong(TSignResult.signTime);
        if (signTime == 0L) {
            signTime = System.currentTimeMillis();
        }
        mTvResultMsg.setText("时间 " + TimeUtil.formatMilli(signTime, TimeFormat.from_h_to_m_24));
        onFinish();
    }

    /**
     * 签到失败
     */
    private void signError(String msg) {
        mIvResult.setImageResource(KErrorResId);
        mTvResult.setTextColor(ResLoader.getColor(KErrorColId));
        mTvResult.setText("签到失败");
        mTvResultMsg.setText(msg);
        onFinish();
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
