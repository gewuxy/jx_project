package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.me.UnitNumVH;

/**
 * 搜索
 *
 * @auther : GuoXuan
 * @since : 2017/6/8
 */

public class RecVH extends ViewHolderEx {

    private UnitNumVH mUnitNumVH;
    private MeetingVH mMeetingVH;

    public RecVH(@NonNull View convertView) {
        super(convertView);

        mUnitNumVH = new UnitNumVH(convertView);
        mMeetingVH = new MeetingVH(convertView);
    }

    public UnitNumVH getUnitNumVH() {
        return mUnitNumVH;
    }

    public MeetingVH getMeetingVH() {
        return mMeetingVH;
    }

    public TextView getTvMore() {
        return getView(R.id.reach_more_tv);
    }
}
