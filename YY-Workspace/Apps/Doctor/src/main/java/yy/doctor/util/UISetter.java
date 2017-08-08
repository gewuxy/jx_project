package yy.doctor.util;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import lib.ys.YSLog;
import lib.ys.fitter.DpFitter;
import lib.ys.fitter.LayoutFitter;
import lib.ys.util.TextUtil;
import lib.ys.util.TimeUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.MeetingVH;
import yy.doctor.model.home.RecUnitNum.Attention;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.model.meet.Meeting.MeetType;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.model.unitnum.FileData;
import yy.doctor.model.unitnum.FileData.TFileData;
import yy.doctor.ui.activity.data.DownloadFileActivityIntent;

/**
 * @auther yuansui
 * @since 2017/6/8
 */

public class UISetter {

    /**
     * 根据会议状态设置text
     *
     * @param state
     * @param tv
     */
    public static void setHomeMeetingState(@MeetState int state, TextView tv) {
        String text = "未开始";
        int color = R.color.text_01b557;

        switch (state) {
            case MeetState.not_started: {
                text = "未开始";
                color = R.color.text_01b557;
            }
            break;
            case MeetState.under_way: {
                text = "进行中";
                color = R.color.text_e6600e;
            }
            break;
            case MeetState.retrospect: {
                text = "精彩回顾";
                color = R.color.text_5cb0de;
            }
            break;
        }

        tv.setText(text);
        tv.setTextColor(ResLoader.getColor(color));
    }

    /**
     * 设置资料
     *
     * @param layout
     * @param listFile
     * @param id
     */
    public static void setFileData(LinearLayout layout, List<FileData> listFile, int id) {
        String fileName;
        String fileUrl;
        String fileType;
        for (int i = 0; i < listFile.size(); i++) {
            FileData fileItem = listFile.get(i);

            long fileSize = fileItem.getLong(TFileData.fileSize);
            fileName = fileItem.getString(TFileData.materialName);
            String fileId = fileItem.getString(TFileData.id);
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

            addFileItem(layout, fileName, v ->
                    DownloadFileActivityIntent.create()
                            .filePath(CacheUtil.getUnitNumCacheDir(String.valueOf(id)))
                            .fileName(finalFileName)
                            .url(finalFileUrl)
                            .type(finalFileType)
                            .fileSize(fileSize)
                            .dataFileId(fileId)
                            .start(v.getContext()));
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

    /**
     * 没有文字就隐藏TextView
     * 有文字内容就设置给TextView
     *
     * @param text
     * @param textView
     */
    public static void viewVisibility(String text, TextView textView) {
        if (TextUtil.isEmpty(text)) {
            ViewUtil.goneView(textView);
        } else {
            textView.setText(text);
        }
    }

    /**
     * 设置密码的输入范围格式
     *
     * @param et
     */
    public static void setPwdRange(EditText et) {

        et.setKeyListener(new NumberKeyListener() {

            @Override
            protected char[] getAcceptedChars() {
                String chars = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM-×÷＝%√°′″{}()[].|*/#~,:;?\"‖&*@\\^,$–…'=+!><.-—_";
                return chars.toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }
        });
    }

    /**
     *          * 判定输入汉字
     *          * @param c
     *          * @return
     *         
     */
   /* public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }*/

    /**
     *          * 检测String是否全是中文
     *          * @param name
     *          * @return
     *         
     */
   /* public static boolean checkNameChese(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (!isChinese(cTemp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }*/

    /**
     * @param holder
     * @param meeting    数据
     * @param visibility 单位号是否显示
     */
    public static void meetingHolderSet(MeetingVH holder, Meeting meeting, boolean visibility) {
        if (meeting == null) {
            return;
        }

        @DrawableRes int resMeet = R.mipmap.meeting_ic_state_under_way;
        @DrawableRes int resFolder = R.mipmap.meeting_ic_folder_unit_num;
        @MeetState int state = meeting.getInt(TMeeting.state);

        switch (state) {
            case MeetState.not_started: {
                resMeet = R.mipmap.meeting_ic_state_not_started;
                resFolder = R.mipmap.meeting_ic_folder_state_not_start;
            }
            break;
            case MeetState.under_way: {
                resMeet = R.mipmap.meeting_ic_state_under_way;
                resFolder = R.mipmap.meeting_ic_folder_state_under_way;
            }
            break;
            case MeetState.retrospect: {
                resMeet = R.mipmap.meeting_ic_state_retrospect;
                resFolder = R.mipmap.meeting_ic_folder_state_retrospect;
            }
            break;
        }

        viewVisibility(visibility ? meeting.getString(TMeeting.organizer) : null, holder.getTvUnitNum());

        holder.getTvTitle().setText(meeting.getString(TMeeting.meetName));

        if (meeting.getInt(TMeeting.type) == MeetType.meet) {

            holder.getTvSection().setText(meeting.getString(TMeeting.meetType));
            holder.getTvTime().setText(TimeUtil.formatMilli(meeting.getLong(TMeeting.startTime), "MM/dd HH:mm"));

            if (meeting.getBoolean(TMeeting.rewardCredit) && meeting.getInt(TMeeting.eduCredits) > 0) {
                ViewUtil.showView(holder.getIvCme());
            } else {
                // 会复用故要隐藏
                ViewUtil.goneView(holder.getIvCme());
            }
            ImageView ivEpn = holder.getIvEpn();
            if (meeting.getInt(TMeeting.xsCredits, 0) > 0) {
                ViewUtil.showView(ivEpn);
                ivEpn.setSelected(meeting.getBoolean(TMeeting.requiredXs));
            } else {
                ViewUtil.goneView(ivEpn);
            }
            // 首页没有的
            ImageView ivState = holder.getIvState();
            if (ivState != null) {
                ivState.setImageResource(resMeet);
            }
            View layoutProgress = holder.getLayoutProgress();
            if (layoutProgress != null) {
                int progress = meeting.getInt(TMeeting.completeProgress);
                if (progress > 0 && state == MeetState.retrospect) {
                    ViewUtil.showView(layoutProgress);
                    holder.getCpvProgress().setProgress(progress);
                    String percent = progress + "%";
                    if (progress == 100) {
                        percent = "完成";
                    }
                    holder.getTvProgress().setText(percent);
                } else {
                    ViewUtil.goneView(layoutProgress);
                }
            }

        } else {
            holder.getIvState().setImageResource(resFolder);

            holder.getTvMeetingNum().setText(String.format("%d个会议", meeting.getInt(TMeeting.meetCount, 0)));

        }
    }

}
