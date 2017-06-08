package yy.doctor.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.util.res.ResLoader;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.activity.me.UnitNumDetailActivity;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.adapter.HomeUnitNumAdapter.onAttentionListener;
import yy.doctor.adapter.VH.HomeVH;
import yy.doctor.model.home.IHome;
import yy.doctor.model.home.IHome.HomeType;
import yy.doctor.model.home.RecMeeting;
import yy.doctor.model.home.RecMeeting.TRecMeeting;
import yy.doctor.model.home.RecUnitNums;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class HomeAdapter extends MultiAdapterEx<IHome, HomeVH> {

    private onTvAttentionListener mListener;
    private HomeUnitNumAdapter mHomeUnitNumAdapter;

    @Override
    protected void initView(int position, HomeVH holder, int itemType) {
        if (itemType == HomeType.unit_num) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mHomeUnitNumAdapter = new HomeUnitNumAdapter();
            holder.getRecyclerView().setLayoutManager(linearLayoutManager);
            holder.getRecyclerView().setAdapter(mHomeUnitNumAdapter);
        }
    }

    @Override
    protected void refreshView(int position, HomeVH holder, int itemType) {
        if (itemType == HomeType.meeting) {
            RecMeeting meeting = (RecMeeting)getItem(position);
            holder.getTvTitle().setText(meeting.getString(TRecMeeting.meetName));
            //holder.getTvSpeakerName().setText(meeting.getString(TRecMeeting.lecturer));
            //holder.getTvSpeakerRank().setText(meeting.getString(TRecMeeting.lecturerTile));
            holder.getIvSpeaker()
                    .placeHolder(R.mipmap.ic_default_home_meeting_speaker)
                    .load();
            holder.getIvUnit()
                    .placeHolder(R.mipmap.ic_default_home_meeting_unit_num)
                    .renderer(new CircleRenderer())
                    .load();
            //判断用户是否已经收藏过这个会议
            if (meeting.getInt(TRecMeeting.stored) == 1) {
                showView(holder.getTvCollection());
            } else {
                goneView(holder.getTvCollection());
            }
            //会议状态判断
            if (meeting.getInt(TRecMeeting.state) == MeetsState.not_started) {
                holder.getTvStatus().setText("未开始");
                holder.getTvStatus().setTextColor(ResLoader.getColor(R.color.text_01b557));
                holder.getIvStatus().setImageResource(R.mipmap.meeting_ic_not_started);
            } else if (meeting.getInt(TRecMeeting.state) == MeetsState.under_way) {
                holder.getTvStatus().setText("进行中");
                holder.getTvStatus().setTextColor(ResLoader.getColor(R.color.text_e6600e));
                holder.getIvStatus().setImageResource(R.mipmap.meeting_ic_under_way);
            } else if (meeting.getInt(TRecMeeting.state) == MeetsState.retrospect) {
                holder.getTvStatus().setText("精彩回顾");
                holder.getTvStatus().setTextColor(ResLoader.getColor(R.color.text_5cb0de));
                holder.getIvStatus().setImageResource(R.mipmap.meeting_ic_retrospect);
            } else {
                //do nothing
            }

            //单位号头像点击事件
            holder.getIvUnit().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UnitNumDetailActivity.nav(getContext(), meeting.getInt(TRecMeeting.pubUserId));
                }
            });
            //会议点击事件
            holder.getMeetingItemLayout().setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    MeetingDetailsActivity.nav(getContext(), meeting.getString(TRecMeeting.id));
                }
            });
        } else {
            RecUnitNums UnitNums = (RecUnitNums) getItem(position);
            mHomeUnitNumAdapter.setData(UnitNums.getData());
            mHomeUnitNumAdapter.setAttentionListener(new onAttentionListener() {

                @Override
                public void onAttentionClick(int attention, int unitNumId, TextView tv) {
                    if (mListener != null) {
                        mListener.onTvAttentionClick(attention, unitNumId, tv);
                    }
                }
            });

            holder.getRecyclerViewFooter().setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showToast("8525");
                }
            });
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

    public interface onTvAttentionListener {
        void onTvAttentionClick(int attention, int unitNumId, TextView tv);
    }

    public void setTvAttentionListener(onTvAttentionListener l) {
        mListener = l;
    }

}
