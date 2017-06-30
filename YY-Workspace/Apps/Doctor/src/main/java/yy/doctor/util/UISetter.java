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
import lib.ys.util.TextUtil;
import lib.ys.util.TimeUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.activity.me.DownloadDataActivity;
import yy.doctor.model.home.RecUnitNum.Attention;
import yy.doctor.model.unitnum.FileData;
import yy.doctor.model.unitnum.FileData.TFileData;

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
        tvDuration.setText("时长:" + Util.parse(endTime - startTime));
    }

    public static void setFileData(LinearLayout layout, List<FileData> listFile, int id) {
        String fileName;
        String fileUrl;
        String fileType;
        for (int i = 0; i < listFile.size(); i++) {
            FileData fileItem = listFile.get(i);

            long fileSize = fileItem.getLong(TFileData.fileSize);
            fileName = fileItem.getString(TFileData.materialName);
            if (TextUtil.isEmpty(fileName)) {
                fileName = fileItem.getString(TFileData.name);
                fileUrl = fileItem.getString(TFileData.fileUrl);
                fileType = fileItem.getString(TFileData.fileType);
            } else {
                fileUrl = fileItem.getString(TFileData.materialUrl);
                fileType = fileItem.getString(TFileData.materialType);
            }

            String finalFileName = fileName;
            String finalFileUrl = fileUrl;
            String finalFileType = fileType;
            addFileItem(layout, fileName, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    DownloadDataActivity.nav(v.getContext(),
                            CacheUtil.getUnitNumCacheDir(String.valueOf(id)),
                            finalFileName, finalFileUrl, finalFileType, fileSize);
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

    /**
     * 关注按钮的状态改变
     *
     * @param tv
     * @param attention
     */
    public static void setAttention(TextView tv, int attention) {
        if (attention == Attention.yes) {
            tv.setText(R.string.already_attention);
            tv.setSelected(true);
            tv.setClickable(false);
        } else {
            tv.setText(R.string.attention);
            tv.setSelected(false);
            tv.setClickable(true);
        }
    }

}
