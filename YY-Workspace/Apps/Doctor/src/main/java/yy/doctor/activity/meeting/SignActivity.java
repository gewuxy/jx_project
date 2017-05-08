package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.MilliUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

import static yy.doctor.Extra.KData;

/**
 * 签到界面
 *
 * @author : GuoXuan
 * @since : 2017/5/2
 */

public class SignActivity extends BaseActivity {

    private static final int KReturnTime = 3;//关闭倒计时
    private static final long KSecond = 1000L;//1秒

    //TODO:mResultCode的类型
    private String mResultCode;
    private String mResult;

    private ImageView mIvResult;
    private TextView mTvResultCode;//成功还是失败
    private TextView mTvResult;//成功的时候或失败的原因
    private TextView mTvReturn;
    private Runnable mTarget;

    private Drawable mDrawSuccess;//成功的图片
    private Drawable mDrawDefeat;//失败的图片
    private int mSuccess;//成功的颜色
    private int mDefeat;//失败的颜色

    public static void nav(Context context, String resultCode, String result) {
        Intent i = new Intent(context, SignActivity.class);
        i.putExtra("code", resultCode);
        i.putExtra(KData, result);
        LaunchUtil.startActivity(context, i);
    }

    //TODO:要删
    public static Intent test(Context context, String resultCode, String result) {
        Intent i = new Intent(context, SignActivity.class);
        i.putExtra("code", resultCode);
        i.putExtra(KData, result);
        return i;
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
            mResult = intent.getStringExtra(KData);
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


        CountDownTimer timer = new CountDownTimer(4000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                mTvReturn.setText(MilliUtil.toSecond(millisUntilFinished) + "秒后返回");
            }

            @Override
            public void onFinish() {
                finish();
            }
        };
        timer.start();
//        //定时关闭界面,隔一秒刷新一次界面
//        mTarget = new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < KReturnTime; i++) {
//                    final int close = KReturnTime - i;
//                    runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mTvReturn.setText(close + "秒后返回");
//                        }
//                    });
//                    SystemClock.sleep(KSecond);
//                    if (close == 1) {
//                        finish();
//                    }
//                }
//            }
//        };
//        //TODO:该用线程池管理
//        new Thread(mTarget).start();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTarget != null) {
            mTarget = null;
        }
    }
}
