package yy.doctor.util;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import lib.ys.fitter.DpFitter;
import lib.ys.fitter.LayoutFitter;
import lib.ys.util.TimeUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.activity.me.DownloadDataActivity;
import yy.doctor.model.unitnum.UnitNumDetailData;
import yy.doctor.model.unitnum.UnitNumDetailData.TUnitNumDetailData;

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

    public static void setDateDuration(TextView tvDate, TextView tvDuration, long startTime, long endTime) {
        tvDate.setText(TimeUtil.formatMilli(startTime, "MM月dd日 HH:mm"));
        tvDuration.setText("时长:" + Util.timeParse(endTime - startTime));
    }

    public static void setFileData(LinearLayout layout,List<UnitNumDetailData> listFile, int id) {

        for (int i = 0; i < listFile.size(); i++) {
            UnitNumDetailData fileItem = listFile.get(i);
            addFileItem(layout, fileItem.getString(TUnitNumDetailData.materialName), new OnClickListener() {

                @Override
                public void onClick(View v) {
                    DownloadDataActivity.nav(v.getContext(),
                            CacheUtil.getUnitNumCacheDir(String.valueOf(id)),
                            fileItem.getString(TUnitNumDetailData.materialName),
                            fileItem.getString(TUnitNumDetailData.materialUrl),
                            fileItem.getString(TUnitNumDetailData.materialType),
                            fileItem.getLong(TUnitNumDetailData.fileSize));
                }
            });
        }

    }

    /**
     * 添加文件item
     *
     * @param text
     * @param l
     */
    public static void addFileItem(LinearLayout layout, CharSequence text, OnClickListener l) {

        View v = LayoutInflater.from(layout.getContext()).inflate(R.layout.layout_unit_num_detail_file_item, null);
        TextView tv = (TextView) v.findViewById(R.id.unit_num_detail_file_item_tv_name);
        tv.setText(text);
        v.setOnClickListener(l);

        LayoutFitter.fit(v);
        layout.addView(v, LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
    }


}
