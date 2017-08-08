package yy.doctor.ui.activity.meeting;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import lib.processor.annotation.AutoIntent;
import lib.processor.annotation.Extra;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingAdapter;
import yy.doctor.model.meet.Meeting;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

@AutoIntent
public class MeetingFolderActivity extends BaseSRListActivity<Meeting, MeetingAdapter> {

    @Extra
    public String mTitle;

    @Extra(optional = true)
    public String mPreId;

    @Extra(optional = true)
    public String mInfinityId;

    @Extra(defaultInt = 0)
    public int mNum;

    private TextView mTvNum;
    private TextView mTvTitle;
    private final int KFolder = 0;
    private final int KFolderResource = 1;

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
        if (TextUtil.isEmpty(mInfinityId)) {
            exeNetworkReq(KFolder, NetFactory.meetFolder(mPreId));
        } else {
            exeNetworkReq(KFolderResource, NetFactory.folderResource(mInfinityId));
        }
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