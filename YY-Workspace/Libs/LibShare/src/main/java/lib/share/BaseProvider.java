package lib.share;

import android.media.Image;

/**
 * @auther WangLan
 * @since 2017/10/31
 */

abstract public class BaseProvider {

    abstract public void setQQ(String key, String secret);
    abstract public void setWeibo(String key, String secret);
    abstract public void setWechat(String key, String secret);
    abstract public void setTwitter(String key, String secret);
    abstract public void setLinkedIn(String key, String secret);
    abstract public void setLine(String key, String secret);
    abstract public void setWeChatMoments(String key, String secret);
    abstract public void setQZone(String key, String secret);
    abstract public void share(String platform, String text);
    abstract public void share(String platform, String text, Image image);
}
