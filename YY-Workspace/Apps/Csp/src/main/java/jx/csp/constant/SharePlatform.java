package jx.csp.constant;

import jx.csp.R;

import static lib.ys.util.res.ResLoader.getString;

/**
 * @author CaiXiang
 * @since 2017/12/13
 */

public enum SharePlatform {

    wechat(ShareType.wechat, R.drawable.share_ic_wechat, getString(R.string.wechat), true),
    wechat_moment(ShareType.wechat_friend, R.drawable.share_ic_moment, getString(R.string.moment), true),
    qq(ShareType.qq, R.drawable.share_ic_qq, getString(R.string.QQ), true),
    linkedin(ShareType.linkedin, R.drawable.share_ic_linkedin, getString(R.string.linkedin), true),
    sina(ShareType.sina, R.drawable.share_ic_weibo, getString(R.string.weibo), true),
    dingding(ShareType.dingding, R.drawable.share_ic_dingding, getString(R.string.DingDing), true),
    sms(ShareType.sms, R.drawable.share_ic_sms, getString(R.string.message), true),
    contribute(ShareType.contribute, R.drawable.share_ic_contribute, getString(R.string.contribute), true),
    unContribute(ShareType.contribute, R.drawable.share_ic_un_contribute, getString(R.string.contribute), false),

    facebook(ShareType.facebook, R.drawable.share_ic_facebook, getString(R.string.facebook), true),
    twitter(ShareType.twitter, R.drawable.share_ic_twitter, getString(R.string.twitter), true),
    whatsapp(ShareType.whatsapp, R.drawable.share_ic_whatsapp, getString(R.string.whatsapp), true),
    line(ShareType.line, R.drawable.share_ic_line, getString(R.string.Line), true),

    preview(ShareType.preview, R.drawable.share_ic_preview, getString(R.string.preview), true),
    watch_pwd(ShareType.watch_pwd, R.drawable.share_ic_watch_pwd, getString(R.string.watch_pwd), true),
    copy_link(ShareType.copy_link, R.drawable.share_ic_copy_link, getString(R.string.copy_link), true),
    delete(ShareType.delete, R.drawable.share_ic_delete, getString(R.string.delete_speech), true),
    editor(ShareType.editor, R.drawable.share_ic_editor, getString(R.string.editor), true);

    private int mType;  // 分享平台类型
    private int mIcon; // 分享平台图像
    private String mPlatformName; // 分享平台名称
    private boolean mIsClick;

    SharePlatform(int type, int icon, String name, boolean isClick) {
        mType = type;
        mIcon = icon;
        mPlatformName = name;
        mIsClick = isClick;
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

    public boolean isClick() {
        return mIsClick;
    }
}
