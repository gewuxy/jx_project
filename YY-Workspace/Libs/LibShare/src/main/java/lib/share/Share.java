package lib.share;

import android.content.Context;

import lib.share.mob.MobProvider;
import lib.ys.util.PackageUtil;

/**
 * @auther WangLan
 * @since 2017/10/31
 *
 */
// FIXME: 2017/11/2 暂时先不封装
public class Share {


    public static void init(Context context) {

    }

    public static void setQQ() {
        MobProvider mProvider = new MobProvider();
        mProvider.setQQ(PackageUtil.getMetaValue("QQ_SECRET"), PackageUtil.getMetaValue("QQ_ID"));
    }

    public static void setWX() {
        MobProvider mProvider = new MobProvider();
        mProvider.setWechat(PackageUtil.getMetaValue("WX_ID"), PackageUtil.getMetaValue("WX_SECRET"));
    }

    public static void setWeiBo() {
        MobProvider mProvider = new MobProvider();
        mProvider.setWechat(PackageUtil.getMetaValue("SINA_KEY"), PackageUtil.getMetaValue("SINA_SECRET"));
    }

    public static void setTwitter() {
        MobProvider mProvider = new MobProvider();
        mProvider.setWechat(PackageUtil.getMetaValue("TWITTER_KEY"), PackageUtil.getMetaValue("TWITTER_SECRET"));
    }

    public static void setLinkedIn() {
        MobProvider mProvider = new MobProvider();
        mProvider.setWechat(PackageUtil.getMetaValue("LINKIN_APIKEY"), PackageUtil.getMetaValue("LINKIN_SECRETKEY"));
    }

    public static void setLine() {
        MobProvider mProvider = new MobProvider();
        mProvider.setWechat(PackageUtil.getMetaValue("LINE_ID"), PackageUtil.getMetaValue("LINE_SECRET"));
    }

    public static void setWeChatMoments() {
        MobProvider mProvider = new MobProvider();
        mProvider.setWechat(PackageUtil.getMetaValue("WX_ID"), PackageUtil.getMetaValue("WX_SECRET"));
    }

    public static void setQZone() {
        MobProvider mProvider = new MobProvider();
        mProvider.setWechat(PackageUtil.getMetaValue("QQ_SECRET"), PackageUtil.getMetaValue("QQ_ID"));
    }


    public static void share() {

    }
}
