package yy.doctor.adapter.VH;

import android.view.View;

import lib.ys.adapter.VH.RecyclerViewHolderEx;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class HomeVH extends RecyclerViewHolderEx {

    private HomeMeetingVH mHolderMeeting;

    public HomeVH(View itemView) {
        super(itemView);

        mHolderMeeting = new HomeMeetingVH(itemView);
    }

    public HomeMeetingVH getHolderMeeting() {
        return mHolderMeeting;
    }

    public class HomeMeetingVH extends RecyclerViewHolderEx {

        public HomeMeetingVH(View itemView) {
            super(itemView);
        }

    }

}
