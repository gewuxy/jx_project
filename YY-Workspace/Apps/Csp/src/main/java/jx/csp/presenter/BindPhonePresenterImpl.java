package jx.csp.presenter;

import java.util.concurrent.TimeUnit;

import jx.csp.App;
import jx.csp.R;
import jx.csp.constant.CaptchaType;
import jx.csp.contact.BindPhoneContract;
import jx.csp.contact.BindPhoneContract.V;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.ui.activity.me.bind.BaseSetActivity.RelatedId;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;

/**
 * @auther Huoxuyu
 * @since 2017/11/22
 */

public class BindPhonePresenterImpl extends BasePresenterImpl<BindPhoneContract.V> implements BindPhoneContract.P {

    private final int KBindPhoneCode = 1;
    private final int KCaptchaCode = 3;

    private final int KMaxCount = 3; // 最多获取3次验证码
    private final long KCaptchaDuration = TimeUnit.MINUTES.toMillis(10); // 10分钟
    private int mCount = 0; // 计算点击多少次, 初始0次
    private long mStartTime; // 开始计算10分钟间隔的时间

    private String mPhone;

    public BindPhonePresenterImpl(V v) {
        super(v);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        getView().onStopRefresh();
        if (!r.isSucceed()) {
            onNetworkError(id, r.getError());
            return;
        }

        switch (id) {
            case KBindPhoneCode: {
                bindPhoneSuccess();
                getView().closePage();
            }
            break;
            case KCaptchaCode: {
                getView().getCaptcha();
                App.showToast(R.string.account_send_captcha);
            }
            break;
        }
    }

    @Override
    public void checkCaptcha() {
        mCount++;
        YSLog.d("mCount:", mCount + "");
        if (mCount == 1) {
            mStartTime = System.currentTimeMillis();
        }
        if (mCount > KMaxCount) {
            long duration = System.currentTimeMillis() - mStartTime;
            if (duration <= KCaptchaDuration) {
                App.showToast(R.string.get_captcha_frequently);
                return;
            } else {
                mCount = 1;
            }
        }
        confirmBindAccount(RelatedId.bind_captcha, mPhone, null, CaptchaType.re_fetch);
    }

    @Override
    public void confirmBindAccount(@RelatedId int id, String userName, String captcha, String type) {
        switch (id) {
            case KBindPhoneCode: {
                exeNetworkReq(id, UserAPI.bindPhone(userName, captcha).build());
            }
            break;
            case KCaptchaCode: {
                exeNetworkReq(id, UserAPI.sendCaptcha(userName, type).build());
            }
            break;
        }

    }

    @Override
    public void bindPhoneSuccess() {
        Profile.inst().put(TProfile.mobile, mPhone);
        Profile.inst().saveToSp();
        Notifier.inst().notify(NotifyType.profile_change, mPhone);
        Notifier.inst().notify(NotifyType.bind_phone, mPhone);
    }

    @Override
    public void checkMobile(String phone) {
        mPhone = phone;
        if (mPhone.equals(Profile.inst().getString(TProfile.mobile))) {
            App.showToast(R.string.account_is_bind);
            return;
        }
    }
}
