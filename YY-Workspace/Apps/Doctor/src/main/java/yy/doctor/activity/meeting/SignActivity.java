package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import lib.network.error.NetError;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TimeUtil;
import lib.ys.util.TimeUtil.TimeFormat;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.BuildConfig;
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

    private static final int KSecond = 1;//间隔
    private static final int KReturnTime = 3;//关闭倒计时
    private static final int KSign = 0;
    private static final int KToSign = 1;
    private static final String KModuleId = "4";//签到模块的id

    private String mMeetId;//会议id

    private ImageView mIvResult;//结果图标
    private TextView mTvResult;//签到结果
    private TextView mTvResultMsg;//成功的时间/失败的原因
    private TextView mTvReturn;

    private int mSucceed;//成功的颜色
    private int mError;//失败的颜色
    private Drawable mDrawSucceed;//成功的图片
    private Drawable mDrawError;//失败的图片

    public static void nav(Context context, String meetId) {
        Intent i = new Intent(context, SignActivity.class);
        i.putExtra(Extra.KData, meetId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mDrawSucceed = ResLoader.getDrawable(R.mipmap.sign_ic_success);
        mDrawError = ResLoader.getDrawable(R.mipmap.sign_ic_defeat);
        mSucceed = ResLoader.getColor(R.color.text_0882e7);
        mError = ResLoader.getColor(R.color.text_333);

        Intent intent = getIntent();
        if (intent != null) {
            mMeetId = intent.getStringExtra(Extra.KData);
        }
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

        //TODO:直接显示结果
        refresh(RefreshWay.embed);
        if (checkPermission(0, Permission.location)) {
            exeNetworkReq(KToSign, NetFactory.toSign(mMeetId, KModuleId));
        }

    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        switch (result) {
            case PermissionResult.granted: {
                exeNetworkReq(KToSign, NetFactory.toSign(mMeetId, KModuleId));
            }
            break;
            case PermissionResult.denied:
            case PermissionResult.never_ask: {

                showToast("请开启定位权限");
            }
            break;
        }
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
                        .moduleId(KModuleId)
                        .positionId(signData.getString(TSign.id))
                        .signLat(signData.getString(TSign.positionLat))
                        .signLng(signData.getString(TSign.positionLng))
                        .builder());
            } else {
                setViewState(ViewState.normal);
                signError(r.getError());
            }
        } else {//签到
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
        mIvResult.setImageDrawable(mDrawSucceed);
        mTvResult.setTextColor(mSucceed);
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
        mIvResult.setImageDrawable(mDrawError);
        mTvResult.setTextColor(mError);
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
