package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.network.error.NetError;
import lib.network.model.NetworkResponse;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Response;
import yy.doctor.BuildConfig;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.meet.ToSign;
import yy.doctor.model.meet.ToSign.TToSign;
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

    private static final int KReturnTime = 3;//关闭倒计时
    private static final long KSecond = 1000L;//1秒
    private static final int KToSign = 0;
    private static final int KSign = 1;

    //TODO:mResultCode的类型
    private String mResultCode;
    private String mResult;

    private ImageView mIvResult;
    private TextView mTvResultCode;//成功还是失败
    private TextView mTvResult;//成功的时候或失败的原因
    private TextView mTvReturn;

    private Drawable mDrawSuccess;//成功的图片
    private Drawable mDrawDefeat;//失败的图片
    private int mSuccess;//成功的颜色
    private int mDefeat;//失败的颜色
    private String mModuleId;
    private String mMeetId;

    public static void nav(Context context, String resultCode, String result) {
        Intent i = new Intent(context, SignActivity.class);
        i.putExtra("code", resultCode);
        i.putExtra(Extra.KData, result);
        LaunchUtil.startActivity(context, i);
    }


    @Override
    public void initData() {
        mDrawSuccess = ResLoader.getDrawable(R.mipmap.sign_ic_success);
        mDrawDefeat = ResLoader.getDrawable(R.mipmap.sign_ic_defeat);
        mSuccess = ResLoader.getColor(R.color.text_0882e7);
        mDefeat = ResLoader.getColor(R.color.text_333);

        Intent intent = getIntent();
        if (intent != null) {
            mResultCode = intent.getStringExtra("code");
            mResult = intent.getStringExtra(Extra.KData);
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
        mTvResultCode = findView(R.id.sign_tv_result_code);
        mTvResult = findView(R.id.sign_tv_result);
        mTvReturn = findView(R.id.sign_tv_return);
    }

    @Override
    public void setViews() {
        mTvReturn.setOnClickListener(this);

        mTvResultCode.setText(mResultCode);
        mTvResult.setText(mResult);

        if ("成功".equals(mResultCode)) {
            mIvResult.setImageDrawable(mDrawSuccess);
            mTvResultCode.setTextColor(mSuccess);
        } else {
            mIvResult.setImageDrawable(mDrawDefeat);
            mTvResultCode.setTextColor(mDefeat);
        }

        if (BuildConfig.TEST) {
            mModuleId = "4";
            mMeetId = "17042512131640894904";
        }
        //TODO：只在这个界面显示结果?
        refresh(RefreshWay.embed);
        exeNetworkRequest(0, NetFactory.toSign(mMeetId, mModuleId));

        //TODO:后面再加倒计时
//        CountDownTimer timer = new CountDownTimer(4000, 1000) {
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//                mTvReturn.setText(MilliUtil.toSecond(millisUntilFinished) + "秒后返回");
//            }
//
//            @Override
//            public void onFinish() {
//                finish();
//            }
//        };
//        timer.start();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
        if (id == KToSign) {
            return JsonParser.ev(nr.getText(), ToSign.class);
        }
        return JsonParser.error(nr.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        if (id == KToSign) {
            Response<ToSign> r = (Response<ToSign>) result;
            if (r.isSucceed()) {
                ToSign signData = r.getData();
                exeNetworkRequest(KSign, NetFactory.sign()
                        .meetId(mMeetId)
                        .moduleId(mModuleId)
                        .positionId(signData.getString(TToSign.id))
                        //ToDO:获取经纬度
                        .signLat(signData.getString(TToSign.positionLat))
                        .signLng(signData.getString(TToSign.positionLng))
                        .builder());
            } else {
                showToast(r.getError());
            }
        } else if (id == KSign) {
            Response r = (Response) result;
            if (r.isSucceed()) {
                showToast("成功");
            } else {
                showToast(r.getError());
            }
        }
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
            default:
                break;
        }
    }
}
