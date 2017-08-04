package yy.doctor.adapter.home;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
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
import yy.doctor.adapter.VH.meeting.MeetingVH;
import yy.doctor.adapter.home.HomeUnitNumAdapter.onAttentionListener;
import yy.doctor.model.home.IHome;
import yy.doctor.model.home.IHome.HomeType;
import yy.doctor.model.home.Lecturer;
import yy.doctor.model.home.Lecturer.TLecturer;
import yy.doctor.model.home.RecMeeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.model.home.RecUnitNums;
import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.ui.activity.meeting.MeetingDetailsActivity;
import yy.doctor.ui.activity.meeting.MeetingFolderActivityIntent;
import yy.doctor.util.UISetter;

/**
 * 首页的adapter
 *
 * @author CaiXiang
 * @since 2017/4/10
 */
public class HomeAdapter extends MultiAdapterEx<IHome, HomeVH> {

    private static final String KLayout = "home_meet_folder_item_layout_speaker_";
    private static final String KName = "home_meet_folder_item_tv_speaker_name_";
    private static final String KTitle = "home_meet_folder_item_tv_speaker_title_";

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
        switch (itemType) {
            case HomeType.meeting: {
                RecMeeting item = (RecMeeting) getItem(position);

                HomeMeetingVH homeVH = holder.getHomeMeetingVH();
                // 会议item点击事件
                setOnViewClickListener(position, homeVH.getMeetingItemLayout());

                //判断会议状态
                @MeetState int state = item.getInt(TMeeting.state);
                UISetter.setHomeMeetingState(state, homeVH.getTvStatus());

                List<Lecturer> lecturers = item.getList(TMeeting.lecturerList);
                if (lecturers != null && lecturers.size() > 0) {
                    Lecturer lecturer = lecturers.get(0);
                    homeVH.getIvSpeaker()
                            .placeHolder(R.mipmap.ic_default_home_meeting_speaker)
                            .url(lecturer.getString(TLecturer.headimg))
                            .load();
                    homeVH.getTvSpeakerName().setText(lecturer.getString(TLecturer.name));
                    homeVH.getTvSpeakerRank().setText(lecturer.getString(TLecturer.title));
                }

                MeetingVH meetVH = homeVH.getMeetingVH();
                UISetter.meetingHolderSet(meetVH, item, false);

            }
            break;
            case HomeType.meeting_folder: {
                RecMeeting item = (RecMeeting) getItem(position);

                HomeMeetFolderVH folderVH = holder.getHomeMeetFolderVH();
                // 文件夹的点击事件
                setOnViewClickListener(position, folderVH.getFolderItemLayout());

                folderVH.getTvFolderName().setText(item.getString(TMeeting.meetName));
                folderVH.getTvFolderUnitNum().setText(item.getString(TMeeting.organizer));
                folderVH.getTvFolderMeetNum().setText(String.format("%d个会议", item.getInt(TMeeting.meetCount), 0));

                // 讲者相关
                List<Lecturer> lecturers = item.getList(TMeeting.lecturerList);
                if (lecturers != null && lecturers.size() > 0) {
                    showView(folderVH.getSpeakers());
                    // 用fori的特点找id和设置数据
                    List<View> layouts = new ArrayList<>();
                    List<TextView> names = new ArrayList<>();
                    List<TextView> titles = new ArrayList<>();

                    int index = 1;
                    while (true) {
                        View v = holder.getConvertView().findViewById(ResLoader.getIdentifier(KLayout + index, ResUtil.ResDefType.id));
                        if (v == null) {
                            break;
                        } else {
                            // 确保size一致
                            TextView name = (TextView) holder.getConvertView().findViewById(ResLoader.getIdentifier(KName + index, ResUtil.ResDefType.id));
                            names.add(name);
                            TextView title = (TextView) holder.getConvertView().findViewById(ResLoader.getIdentifier(KTitle + index, ResUtil.ResDefType.id));
                            titles.add(title);
                            layouts.add(v);
                            index++;
                        }
                    }
                    // 设置数据
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
            }
            break;
            case HomeType.unit_num: {
                RecUnitNums items = (RecUnitNums) getItem(position);
                mHomeUnitNumAdapter.setData(items.getData());
            }
            break;
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
        return getItem(position).getHomeType();
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
        RecMeeting item = (RecMeeting) getItem(position);
        if (getItem(position).getHomeType() == HomeType.meeting_folder) {
            MeetingFolderActivityIntent
                    .create(item.getString(TMeeting.meetName), item.getInt(TMeeting.meetCount))
                    .preId(item.getString(TMeeting.id))
                    .start(getContext());
        } else {

            MeetingDetailsActivity.nav(getContext(), item.getString(TMeeting.id), item.getString(TMeeting.meetName));
        }
    }
}
