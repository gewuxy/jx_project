package yy.doctor.model.home;

import yy.doctor.adapter.VH.HomeVH;
import yy.doctor.adapter.VH.HomeVH.HomeMeetingVH;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class HomeMeeting extends Home {

    @Override
    public void refresh(HomeVH holder) {
        HomeMeetingVH vh = holder.getHolderMeeting();

        String url = getString(THome.headImg_url);
    }
}
