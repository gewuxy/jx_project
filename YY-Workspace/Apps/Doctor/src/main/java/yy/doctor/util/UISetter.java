package yy.doctor.util;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import lib.ys.fitter.DpFitter;
import lib.ys.util.res.ResLoader;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;

/**
 * @auther yuansui
 * @since 2017/6/8
 */

public class UISetter {

    private static final int KMeetIconSizeDp = 11;

    /**
     * 根据会议状态
     *
     * @param state
     * @param tv
     */
    public static void setMeetState(@MeetsState int state, TextView tv) {
        String text = null;
        int color = 0;
        Drawable d = null;

        switch (state) {
            case MeetsState.not_started: {
                text = "未开始";
                color = ResLoader.getColor(R.color.text_01b557);
                d = ResLoader.getDrawable(R.mipmap.meeting_ic_not_started);
            }
            break;
            case MeetsState.under_way: {
                text = "进行中";
                color = ResLoader.getColor(R.color.text_e6600e);
                d = ResLoader.getDrawable(R.mipmap.meeting_ic_under_way);
            }
            break;
            case MeetsState.retrospect: {
                text = "精彩回顾";
                color = ResLoader.getColor(R.color.text_5cb0de);
                d = ResLoader.getDrawable(R.mipmap.meeting_ic_retrospect);
            }
            break;
        }

        if (d != null) {
            d.setBounds(0, 0, DpFitter.dp(KMeetIconSizeDp), DpFitter.dp(KMeetIconSizeDp));
        }

        tv.setText(text);
        tv.setTextColor(color);
        tv.setCompoundDrawables(d, null, null, null);
    }
}
