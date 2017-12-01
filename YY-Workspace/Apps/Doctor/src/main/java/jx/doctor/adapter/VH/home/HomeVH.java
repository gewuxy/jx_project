package jx.doctor.adapter.VH.home;

import android.support.annotation.NonNull;
import android.view.View;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.view.recycler.WrapRecyclerView;
import jx.doctor.R;

public class HomeVH extends ViewHolderEx {

    private HomeMeetingVH mHomeMeetingVH;
    private HomeMeetFolderVH mHomeMeetFolderVH;

    public HomeVH(@NonNull View convertView) {
        super(convertView);

        mHomeMeetingVH = new HomeMeetingVH(convertView);
        mHomeMeetFolderVH = new HomeMeetFolderVH(convertView);
    }

    public HomeMeetingVH getHomeMeetingVH() {
        return mHomeMeetingVH;
    }

    public HomeMeetFolderVH getHomeMeetFolderVH() {
        return mHomeMeetFolderVH;
    }

    public WrapRecyclerView getRecyclerView() {
        return getView(R.id.home_recycler_view);
    }

}