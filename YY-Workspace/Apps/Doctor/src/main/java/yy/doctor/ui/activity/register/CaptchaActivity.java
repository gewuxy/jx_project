package yy.doctor.ui.activity.register;

import android.support.annotation.NonNull;
import android.webkit.WebView;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.network.UrlUtil;
import yy.doctor.util.Util;

/**
 * 获取激活码说明界面
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
public class CaptchaActivity extends BaseActivity {
    private WebView mWebView;
    private String mUrlActivityCode = UrlUtil.getHostName() + "api/register/get_invite_code";

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_captcha;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.title_fetch_captcha, this);
    }

    @Override
    public void findViews() {
        mWebView = findView(R.id.captcha_web);
    }

    @Override
    public void setViews() {
        mWebView.loadUrl(mUrlActivityCode);
    }
}
