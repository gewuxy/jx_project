package jx.csp.constant;

import jx.csp.R;

import static lib.ys.util.res.ResLoader.getString;

/**
 * @auther ${HuoXuYu}
 * @since 2018/1/3
 */

public enum SharePreview {
    preview(SharePreviewType.preview, R.drawable.share_ic_copy, getString(R.string.preview)),
    pwd(SharePreviewType.pwd, R.drawable.share_ic_copy, getString(R.string.watch_pwd)),
    copy(SharePreviewType.copy, R.drawable.share_ic_copy, getString(R.string.copy_link)),
    delete(SharePreviewType.delete, R.drawable.share_ic_copy, getString(R.string.delete_speech));

    private int mType;
    private int mIcon;
    private String mName;

    SharePreview(int type, int icon, String name) {
        mType = type;
        mIcon = icon;
        mName = name;
    }

    public int type() {
        return mType;
    }

    public int icon() {
        return mIcon;
    }

    public String previewName() {
        return mName;
    }
}
