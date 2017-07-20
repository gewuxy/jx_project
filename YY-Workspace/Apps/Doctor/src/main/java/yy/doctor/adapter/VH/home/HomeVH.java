package yy.doctor.adapter.VH.home;

import android.support.annotation.NonNull;
import android.view.View;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.view.recycler.WrapRecyclerView;
import yy.doctor.R;

public class HomeVH extends ViewHolderEx {

    private HomeMeetingVH mHomeMeetingVH;
    private HomeMeetingFolderVH mHomeMeetingFolderVH;

    public HomeVH(@NonNull View convertView) {
        super(convertView);

        mHomeMeetingVH = new HomeMeetingVH(convertView);
        mHomeMeetingFolderVH = new HomeMeetingFolderVH(convertView);
    }

    public HomeMeetingVH getHomeMeetingVH() {
        return mHomeMeetingVH;
    }

    public HomeMeetingFolderVH getHomeMeetingFolderVH() {
        return mHomeMeetingFolderVH;
    }

    public WrapRecyclerView getRecyclerView() {
        return getView(R.id.home_recycler_view);
    }

}