package jx.csp.constant;

import jx.csp.R;

import static lib.ys.util.res.ResLoader.getColor;
import static lib.ys.util.res.ResLoader.getString;

/**
 * @auther Huoxuyu
 * @since 2017/12/14
 */

public enum VipPermission {
    norm_record(R.drawable.vip_ic_record, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_record)),
    norm_live(R.drawable.vip_ic_live, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_live)),
    norm_meeting(R.drawable.vip_ic_meet_num, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_three_meeting)),
    norm_advertising(R.drawable.vip_ic_un_advertising, getColor(R.color.text_507a8b9f), getString(R.string.vip_manage_advertising)),
    norm_watermark(R.drawable.vip_ic_un_watermark, getColor(R.color.text_507a8b9f), getString(R.string.vip_manage_close_watermark)),

    advanced_record(R.drawable.vip_ic_record, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_record)),
    advanced_live(R.drawable.vip_ic_live, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_live)),
    advanced_meeting(R.drawable.vip_ic_meet_num, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_ten_meeting)),
    advanced_advertising(R.drawable.vip_ic_advertising, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_advertising)),
    advanced_watermark(R.drawable.vip_ic_watermark, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_close_watermark)),

    profession_record(R.drawable.vip_ic_record, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_record)),
    profession_live(R.drawable.vip_ic_live, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_live)),
    profession_meeting(R.drawable.vip_ic_meet_num, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_infinite_meeting)),
    profession_advertising(R.drawable.vip_ic_advertising, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_advertising)),
    profession_watermark(R.drawable.vip_ic_watermark, getColor(R.color.text_7a8b9f), getString(R.string.vip_manage_custom_watermark));

    private int mImage;
    private int mColor;
    private String mText;

    VipPermission(int image, int color, String text) {
        mImage = image;
        mText = text;
        mColor = color;
    }

    public int image() {
        return mImage;
    }

    public int color() {
        return mColor;
    }

    public String text() {
        return mText;
    }
}
