package lib.network.model;

import lib.network.NetworkUtil;


/**
 * 保存http返回的数据
 *
 * @author yuansui
 */
public class NetworkResp {
    private String mText = NetworkUtil.KTextEmpty;
    private byte[] mBytes;

    public void setText(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public byte[] getBytes() {
        return mBytes;
    }

    public void setBytes(byte[] bytes) {
        mBytes = bytes;
    }
}
