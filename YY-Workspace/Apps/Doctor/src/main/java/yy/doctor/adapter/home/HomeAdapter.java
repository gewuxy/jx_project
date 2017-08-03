package yy.doctor.adapter.home;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.util.TimeUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.res.ResUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.home.HomeMeetFolderVH;
import yy.doctor.adapter.VH.home.HomeMeetingVH;
import yy.doctor.adapter.VH.home.HomeVH;
import yy.doctor.adapter.home.HomeUnitNumAdapter.onAttentionListener;
import yy.doctor.model.home.IHome;
import yy.doctor.model.home.IHome.HomeType;
import yy.doctor.model.home.Lecturer;
import yy.doctor.model.home.Lecturer.TLecturer;
import yy.doctor.model.home.RecMeeting;
import yy.doctor.model.home.RecMeeting.TRecMeeting;
import yy.doctor.model.home.RecMeetingFolder;
import yy.doctor.model.home.RecMeetingFolder.TRecMeetingFolder;
import yy.doctor.model.home.RecUnitNums;
import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.ui.activity.meeting.MeetingDetailsActivity;
import yy.doctor.ui.activity.meeting.MeetingFolderActivity;
import yy.doctor.ui.activity.meeting.MeetingFolderActivityIntent;
import yy.doctor.util.UISetter;

/**
 * 首页的adapter
 *
 * @author CaiXiang
 * @since 2017/4/10
 */
public class HomeAdapter extends MultiAdapterEx<IHome, HomeVH> {

    private final String KLayout = "home_meet_folder_item_layout_speaker_";
    private final String KName = "home_meet_folder_item_tv_speaker_name_";
    private final String KTitle = "home_meet_folder_item_tv_speaker_title_";

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

            if (item.getBoolean(TRecMeeting.rewardCredit)) {
                showView(homeMeetingVH.getIvCme());
            }
            if (item.getInt(TRecMeeting.requiredXs, 0) > 0) {
                homeMeetingVH.getIvEpn().setSelected(item.getBoolean(TRecMeeting.requiredXs));
            }

            homeMeetingVH.getTvSpeakerName().setText(item.getString(TRecMeeting.lecturer));
            homeMeetingVH.getTvSpeakerRank().setText(item.getString(TRecMeeting.lecturerTile));
            //会议item点击事件
            homeMeetingVH.getMeetingItemLayout().setOnClickListener(v ->
                    MeetingDetailsActivity.nav(getContext(), item.getString(TRecMeeting.id), item.getString(TRecMeeting.meetName)));
        } else if (itemType == HomeType.unit_num) {
            RecUnitNums items = (RecUnitNums) getItem(position);
            mHomeUnitNumAdapter.setData(items.getData());
        } else if (itemType == HomeType.meeting_folder) {
            HomeMeetFolderVH folderVH = holder.getHomeMeetFolderVH();
            RecMeetingFolder item = (RecMeetingFolder) getItem(position);

            folderVH.getTvFolderName().setText(item.getString(TRecMeetingFolder.infinityName));
            folderVH.getTvFolderUnitNum().setText(item.getString(TRecMeetingFolder.unitUserName));
            folderVH.getTvFolderMeetNum().setText(String.format("%d个会议", item.getInt(TRecMeetingFolder.meetCount), 0));
            List<Lecturer> lecturers = item.getList(TRecMeetingFolder.lecturerList);

            List<View> layouts = new ArrayList<>();
            List<TextView> names = new ArrayList<>();
            List<TextView> titles = new ArrayList<>();

            int index = 1;
            while (true) {
                View v = holder.getConvertView().findViewById(ResLoader.getIdentifier(KLayout + index, ResUtil.ResDefType.id));
                if (v == null) {
                    break;
                } else {
                    TextView name = (TextView) holder.getConvertView().findViewById(ResLoader.getIdentifier(KName + index, ResUtil.ResDefType.id));
                    names.add(name);
                    TextView title = (TextView) holder.getConvertView().findViewById(ResLoader.getIdentifier(KTitle + index, ResUtil.ResDefType.id));
                    titles.add(title);
                    layouts.add(v);
                    index++;
                }
            }

            if (lecturers != null && lecturers.size() > 0) {
                showView(folderVH.getSpeakers());
                for (int i = 0; i < lecturers.size(); i++) {
                    Lecturer lecturer = lecturers.get(i);
                    if (lecturer == null) {
                        return;
                    }
                    showView(layouts.get(i));
                    names.get(i).setText(lecturer.getString(TLecturer.name));
                    titles.get(i).setText(lecturer.getString(TLecturer.title));
                }
            }


            setOnViewClickListener(position, folderVH.getFolderItemLayout());
        }
    }

    @Override
    public int getConvertViewResId(int itemType) {
        int id = R.layout.layout_home_meeting_item;
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

    @Override
    protected void onViewClick(int position, View v) {
        if (getItem(position).getType() == HomeType.meeting_folder) {
            RecMeetingFolder item = (RecMeetingFolder) getItem(position);
            MeetingFolderActivityIntent
                    .create(item.getString(TRecMeetingFolder.infinityName),item.getInt(TRecMeetingFolder.meetCount))
                    .preId(item.getString(TRecMeetingFolder.id))
                    .start(getContext());
        }
    }
}
