package jx.csp.constant;

import jx.csp.R;

import static lib.ys.util.res.ResLoader.getString;

/**
 * @author CaiXiang
 * @since 2017/12/13
 */

public enum SharePlatform {
    wechat(ShareType.wechat, R.drawable.share_ic_wechat, getString(R.string.wechat)),
    wechat_moment(ShareType.wechat_friend, R.drawable.share_ic_moment, getString(R.string.moment)),
    qq(ShareType.qq, R.drawable.share_ic_qq, getString(R.string.QQ)),
    linkedin(ShareType.linkedin, R.drawable.share_ic_linkedin, getString(R.string.linkedin)),
    sina(ShareType.sina, R.drawable.share_ic_weibo, getString(R.string.weibo)),
    sms(ShareType.sms, R.drawable.share_ic_message, getString(R.string.message)),
    copy(ShareType.copy, R.drawable.share_ic_copy, getString(R.string.copy_link)),

    overseas_facebook(ShareType.facebook, R.drawable.share_ic_facebook, getString(R.string.facebook)),
    overseas_twitter(ShareType.twitter, R.drawable.share_ic_twitter, getString(R.string.twitter)),
    overseas_whatsapp(ShareType.whatsapp, R.drawable.share_ic_whatsapp, getString(R.string.whatsapp)),
    overseas_line(ShareType.line, R.drawable.share_ic_line, getString(R.string.Line)),
    overseas_linkedin(ShareType.linkedin, R.drawable.share_ic_linkedin, getString(R.string.linkedin)),
    overseas_sms(ShareType.sms, R.drawable.share_ic_sms, getString(R.string.SMS)),
    overseas_copy(ShareType.copy, R.drawable.share_ic_copy, getString(R.string.copy_link));

    private int mType;  // 分享平台类型
    private int mIcon; // 分享平台图像
    private String mPlatformName; // 分享平台名称

    SharePlatform(int type, int icon, String name) {
        mType = type;
        mIcon = icon;
        mPlatformName = name;
    }

    public int type() {
        return mType;
    }

    public int icon() {
        return mIcon;
    }

    public String platformName() {
        return mPlatformName;
    }
}
