package yy.doctor.adapter.home;

import android.support.v7.widget.LinearLayoutManager;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.util.TimeUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.home.HomeMeetingFolderVH;
import yy.doctor.adapter.VH.home.HomeMeetingVH;
import yy.doctor.adapter.VH.home.HomeVH;
import yy.doctor.adapter.home.HomeUnitNumAdapter.onAttentionListener;
import yy.doctor.model.home.IHome;
import yy.doctor.model.home.IHome.HomeType;
import yy.doctor.model.home.RecMeeting;
import yy.doctor.model.home.RecMeeting.TRecMeeting;
import yy.doctor.model.home.RecMeetingFolder;
import yy.doctor.model.home.RecUnitNums;
import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.ui.activity.meeting.MeetingDetailsActivity;
import yy.doctor.util.UISetter;

/**
 * 首页的adapter
 *
 * @author CaiXiang
 * @since 2017/4/10
 */
public class HomeAdapter extends MultiAdapterEx<IHome, HomeVH> {

    private onAttentionListener mListener;
    private HomeUnitNumAdapter mHomeUnitNumAdapter;

    @Override
    protected void initView(int position, HomeVH holder, int itemType) {
        if (itemType == HomeType.unit_num) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mHomeUnitNumAdapter = new HomeUnitNumAdapter();
            mHomeUnitNumAdapter.setAttentionListener(mListener);
            holder.getRecyclerView().setLayoutManager(linearLayoutManager);
            holder.getRecyclerView().setAdapter(mHomeUnitNumAdapter);
        }
    }

    @Override
    protected void refreshView(int position, HomeVH holder, int itemType) {
        if (itemType == HomeType.meeting) {

            HomeMeetingVH homeMeetingVH = holder.getHomeMeetingVH();
            RecMeeting item = (RecMeeting) getItem(position);

            homeMeetingVH.getTvTitle().setText(item.getString(TRecMeeting.meetName));
            //判断会议状态
            @MeetState int state = item.getInt(TRecMeeting.state);
            UISetter.setHomeMeetingState(state, homeMeetingVH.getTvStatus());

            homeMeetingVH.getTvSection().setText(item.getString(TRecMeeting.meetType));
            homeMeetingVH.getTvData().setText(TimeUtil.formatMilli(item.getLong(TRecMeeting.startTime), "MM/dd HH:mm"));
            homeMeetingVH.getIvSpeaker()
                    .placeHolder(R.mipmap.ic_default_home_meeting_speaker)
                    .url(item.getString(TRecMeeting.lecturerImg))
                    .load();

            homeMeetingVH.getTvSpeakerName().setText(item.getString(TRecMeeting.lecturer));
            homeMeetingVH.getTvSpeakerRank().setText(item.getString(TRecMeeting.lecturerTile));
            //会议item点击事件
            homeMeetingVH.getMeetingItemLayout().setOnClickListener(v ->
                    MeetingDetailsActivity.nav(getContext(), item.getString(TRecMeeting.id), item.getString(TRecMeeting.meetName)));
        } else if (itemType == HomeType.unit_num) {
            RecUnitNums items = (RecUnitNums) getItem(position);
            mHomeUnitNumAdapter.setData(items.getData());
        } else if (itemType == HomeType.meeting_folder) {

            HomeMeetingFolderVH homeMeetingFolderVH = holder.getHomeMeetingFolderVH();
            RecMeetingFolder item = (RecMeetingFolder) getItem(position);
            homeMeetingFolderVH.getFolderItemLayout().setOnClickListener(v -> showToast("会议文件夹"));

        }
    }

    @Override
    public int getConvertViewResId(int itemType) {
        int id = 0;
        switch (itemType) {
            case HomeType.meeting: {
                id = R.layout.layout_home_meeting_item;
            }
            break;
            case HomeType.unit_num: {
                id = R.layout.layout_home_recycler_view;
            }
            break;
            case HomeType.meeting_folder: {
                id = R.layout.layout_home_meeting_folder_item;
            }
            break;
        }
        return id;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return HomeType.class.getDeclaredFields().length;
    }

    public void setTvAttentionListener(onAttentionListener l) {
        mListener = l;
    }

    public void refreshAttentionState(int position, int attention) {
        mHomeUnitNumAdapter.setTvAttention(position, attention);
    }
}
