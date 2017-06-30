package yy.doctor.adapter;

import android.support.v7.widget.LinearLayoutManager;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.util.TextUtil;
import lib.ys.util.TimeUtil;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.activity.me.unitnum.UnitNumDetailActivity;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.adapter.HomeUnitNumAdapter.onAttentionListener;
import yy.doctor.adapter.VH.HomeVH;
import yy.doctor.model.home.IHome;
import yy.doctor.model.home.IHome.HomeType;
import yy.doctor.model.home.RecMeeting;
import yy.doctor.model.home.RecMeeting.TRecMeeting;
import yy.doctor.model.home.RecUnitNums;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;

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

            RecMeeting item = (RecMeeting) getItem(position);

            holder.getTvTitle().setText(item.getString(TRecMeeting.meetName));

            //判断会议状态
            @MeetsState int state = item.getInt(TRecMeeting.state);
            UISetter.setMeetState(state, holder.getTvStatus());

            holder.getTvSection().setText(item.getString(TRecMeeting.meetType));
            holder.getTvData().setText(TimeUtil.formatMilli(item.getLong(TRecMeeting.startTime), "MM月dd日 HH:mm"));
            holder.getTvDuration().setText(Util.parse(item.getLong(TRecMeeting.endTime) - item.getLong(TRecMeeting.startTime)));


            //判断用户是否已经收藏过这个会议  此功能已经取消
            /*if (item.getInt(TRecMeeting.stored) == 1) {
                showView(holder.getTvCollection());
            } else {
                goneView(holder.getTvCollection());
            }*/

            holder.getIvSpeaker()
                    .placeHolder(R.mipmap.ic_default_home_meeting_speaker)
                    .url(item.getString(TRecMeeting.lecturerImg))
                    .load();
            holder.getIvUnit()
                    .placeHolder(R.mipmap.ic_default_home_meeting_unit_num)
                    .renderer(new CircleRenderer())
                    .url(item.getString(TRecMeeting.pubUserHead))
                    .load();

            holder.getTvSpeakerName().setText(item.getString(TRecMeeting.lecturer));
            holder.getTvSpeakerRank().setText(item.getString(TRecMeeting.lecturerTile));

            //判断是否有医院
            String hospital = item.getString(TRecMeeting.lecturerHos);
            if (TextUtil.isEmpty(hospital)) {
                goneView(holder.getTvHospital());
            } else {
                holder.getTvHospital().setText(item.getString(TRecMeeting.lecturerHos));
            }

            //单位号头像点击事件
            holder.getIvUnit().setOnClickListener(v -> UnitNumDetailActivity.nav(getContext(), item.getInt(TRecMeeting.pubUserId)));
            //会议点击事件
            holder.getMeetingItemLayout().setOnClickListener(v -> MeetingDetailsActivity.nav(getContext(), item.getString(TRecMeeting.id), item.getString(TRecMeeting.meetName)));

        } else {
            RecUnitNums items = (RecUnitNums) getItem(position);
            mHomeUnitNumAdapter.setData(items.getData());
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
