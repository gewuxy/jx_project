package yy.doctor.ui.activity.data;

import android.view.ViewGroup;
import android.widget.ImageView;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseGroupListActivity;
import yy.doctor.R;
import yy.doctor.adapter.data.DrugDetailAdapter;
import yy.doctor.model.data.GroupDrugDetail;
import yy.doctor.model.data.DrugDetail;
import yy.doctor.util.Util;

/**
 * @auther WangLan    占位，临床指导的详情内容是什么
 * @since 2017/7/29
 */

public class ClinicalGuideDetailActivity  extends BaseGroupListActivity<GroupDrugDetail, DrugDetail, DrugDetailAdapter> {
    private ImageView mIvCollection;
    private boolean mStoredState = false;  // 默认没有收藏
    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "test", this);
        // 收藏
        ViewGroup group = bar.addViewRight(R.drawable.collection_selector, v -> {
            mStoredState = !mStoredState;
            mIvCollection.setSelected(mStoredState);
            showToast(mStoredState ? R.string.collect_finish : R.string.cancel_collect);
            //exeNetworkReq(NetFactory.collectMeeting(mMeetId, mStoredState ? CollectType.collect : CollectType.cancel));
        });
        mIvCollection = Util.getBarView(group, ImageView.class);
    }
}
