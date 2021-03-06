package jx.doctor.ui.activity.meeting;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.ui.other.NavBar;
import lib.jx.ui.activity.base.BaseSRListActivity;
import jx.doctor.R;
import jx.doctor.adapter.meeting.MeetingAdapter;
import jx.doctor.model.meet.Meeting;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.util.Util;

/**
 * 会议文件夹
 */
@Route
public class MeetingFolderActivity extends BaseSRListActivity<Meeting, MeetingAdapter> {

    @IntDef({
            ZeroShowType.show,
            ZeroShowType.hide,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ZeroShowType {
        int show = 0; // 显示
        int hide = 1; // 隐藏
    }

    @Arg
    public String mPreId;

    @Arg(opt = true)
    public String mTitle;

    @Arg(opt = true, defaultInt = 0)
    public int mNum;

    @ZeroShowType
    @Arg(opt = true, defaultInt = ZeroShowType.show)
    public int mShowZero;

    private TextView mTvNum;
    private TextView mTvTitle;

    @Nullable
    public View createHeaderView() {
        return inflate(R.layout.layout_meeting_folder_header);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "会议文件夹", this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvTitle = findView(R.id.meeting_folder_header_tv_title);
        mTvNum = findView(R.id.meeting_folder_header_tv_meeting_num);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(MeetAPI.meetFolder(mPreId, mShowZero).build());
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvTitle.setText(mTitle);
        mTvNum.setText(String.format("%d个会议", mNum));
        getAdapter().hideUnitNum();
    }

    @Override
    public View createEmptyFooterView() {
        return null;
    }
}