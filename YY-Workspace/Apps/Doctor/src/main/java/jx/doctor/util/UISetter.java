package jx.doctor.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.ConstantsEx;
import lib.ys.model.FileSuffix;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.TimeFormatter;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.ViewUtil;
import jx.doctor.Constants.MeetStateText;
import jx.doctor.R;
import jx.doctor.adapter.VH.meeting.MeetingVH;
import jx.doctor.model.data.DataUnit;
import jx.doctor.model.data.DataUnit.FileOpenType;
import jx.doctor.model.data.DataUnit.TDataUnit;
import jx.doctor.model.data.DataUnitDetails;
import jx.doctor.model.data.DataUnitDetails.TDataUnitDetails;
import jx.doctor.model.home.RecUnitNum.Attention;
import jx.doctor.model.meet.Meeting;
import jx.doctor.model.meet.Meeting.MeetState;
import jx.doctor.model.meet.Meeting.MeetType;
import jx.doctor.model.meet.Meeting.TMeeting;
import jx.doctor.ui.activity.data.DataUnitDetailActivityRouter;
import jx.doctor.ui.activity.data.DownloadFileActivityRouter;
import jx.doctor.ui.activity.CommonWebViewActivityRouter;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;

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
        String text = MeetStateText.not_started;
        int color = R.color.text_01b557;

        switch (state) {
            case MeetState.not_started: {
                text = MeetStateText.not_started;
                color = R.color.text_01b557;
            }
            break;
            case MeetState.under_way: {
                text = MeetStateText.under_way;
                color = R.color.text_e6600e;
            }
            break;
            case MeetState.retrospect: {
                text = MeetStateText.retrospect;
                color = R.color.text_5cb0de;
            }
            break;
        }

        tv.setText(text);
        tv.setTextColor(ResLoader.getColor(color));
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
    public static void viewVisibility(CharSequence text, TextView textView) {
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
     * @param holder
     * @param meeting    数据
     * @param visibility 单位号是否显示
     */
    public static void meetingHolderSet(MeetingVH holder, Meeting meeting, boolean visibility) {
        if (meeting == null) {
            return;
        }

        @DrawableRes int resMeet = R.drawable.meeting_ic_file_state_under_way;
        @DrawableRes int resFolder = R.drawable.meeting_ic_folder_unit_num;
        @MeetState int state = meeting.getInt(TMeeting.state);

        switch (state) {
            case MeetState.not_started: {
                resMeet = R.drawable.meeting_ic_file_state_no_start;
                resFolder = R.drawable.meeting_ic_folder_state_not_start;
            }
            break;
            case MeetState.under_way: {
                resMeet = R.drawable.meeting_ic_file_state_under_way;
                resFolder = R.drawable.meeting_ic_folder_state_under_way;
            }
            break;
            case MeetState.retrospect: {
                resMeet = R.drawable.meeting_ic_file_state_retrospect;
                resFolder = R.drawable.meeting_ic_folder_state_retrospect;
            }
            break;
        }

        viewVisibility(visibility ? meeting.getString(TMeeting.organizer) : null, holder.getTvUnitNum());

        String title = meeting.getString(TMeeting.meetName);
        long start = meeting.getLong(TMeeting.startTime);
        long server = meeting.getLong(TMeeting.serverTime);
        long end = meeting.getLong(TMeeting.endTime);
        if (start <= server && server <= end && state == MeetState.under_way && meeting.getInt(TMeeting.liveState) != ConstantsEx.KInvalidValue) {
            title = "[直播中] ".concat(title);
        }
        holder.getTvTitle().setText(title);

        if (meeting.getInt(TMeeting.type) == MeetType.meet) {

            holder.getTvSection().setText(meeting.getString(TMeeting.meetType));
            holder.getTvTime().setText(TimeFormatter.milli(start, "MM/dd HH:mm"));

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

    /**
     * 设置收藏按钮状态
     *
     * @param ds
     * @param v
     */
    public static void setCollectionState(View v, DataUnitDetails ds) {
        if (v == null || ds == null) {
            return;

        }
        boolean favorite = ds.getBoolean(TDataUnitDetails.favorite, false);
        favorite = !favorite;

        ds.put(TDataUnitDetails.favorite, favorite);

        v.setSelected(favorite);
    }

    public static void onDataUnitClick(DataUnit item, @DataType int type, Context context) {
        switch (item.getInt(TDataUnit.openType)) {
            case FileOpenType.details: {
                String dataFileId = item.getString(TDataUnit.id);
                String fileName = item.getString(TDataUnit.title);
                DataUnitDetailActivityRouter.create(
                        dataFileId, fileName, type
                ).route(context);
            }
            break;
            case FileOpenType.pdf: {
                String filePath = CacheUtil.getThomsonCacheDir(item.getString(TDataUnit.id));
                long fileSize = item.getInt(TDataUnit.fileSize) * 1024;
                String fileName = item.getString(TDataUnit.title);
                String url = item.getString(TDataUnit.filePath);
                String dataFileId = item.getString(TDataUnit.id);

                DownloadFileActivityRouter.create()
                        .filePath(filePath)
                        .fileName(fileName)
                        .url(url)
                        .fileSuffix(FileSuffix.pdf)
                        .dataType(type)
                        .fileSize(fileSize)
                        .dataFileId(dataFileId)
                        .route(context);
            }
            break;
            case FileOpenType.html: {
                CommonWebViewActivityRouter.create(item.getString(TDataUnit.title), item.getString(TDataUnit.htmlPath))
                        .fileId(item.getString(TDataUnit.id))
                        .type(type)
                        .route(context);
            }
            break;
        }
    }

    /**
     * NavBar中间文字设置 文字太长时的设置方法
     *
     * @param bar
     * @param fileName
     * @param act
     */
    public static void setNavBarMidText(NavBar bar, String fileName, Activity act) {
        bar.addBackIcon(R.drawable.nav_bar_ic_back, act);
        View v = View.inflate(act, R.layout.layout_nav_bar_mid_text, null);
        TextView tv = v.findViewById(R.id.nav_bar_mid_tv);
        tv.setText(fileName);
        bar.addViewMid(v);
    }

}
