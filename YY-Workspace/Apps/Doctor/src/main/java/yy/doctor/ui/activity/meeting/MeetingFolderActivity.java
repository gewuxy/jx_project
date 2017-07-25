package yy.doctor.ui.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingAdapter;
import yy.doctor.model.meet.Meeting;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

public class MeetingFolderActivity extends BaseSRListActivity<Meeting, MeetingAdapter> {

  private String mTitle;
  private TextView mTvNum;
  private TextView mTvTitle;

  public static void nav(Context context, String title) {
    Intent i = new Intent(context, MeetingFolderActivity.class)
            .putExtra(Extra.KData, title);
    LaunchUtil.startActivity(context, i);
  }

  @Nullable
  public View createHeaderView() {
    return inflate(R.layout.layout_meeting_folder_header);
  }

  @Override
  public void initData() {
    mTitle = getIntent().getStringExtra(Extra.KData);
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
    exeNetworkReq(NetFactory.meets(1, "", getOffset(), getLimit()));
  }

  @Override
  public void setViews() {
    super.setViews();

    mTvTitle.setText(mTitle);
//    ((MeetingAdapter)getAdapter()).setUnitNumVisibility(0);
  }
}