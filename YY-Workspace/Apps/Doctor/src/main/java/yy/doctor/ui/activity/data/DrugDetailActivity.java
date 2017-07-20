package yy.doctor.ui.activity.data;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ImageView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.ui.activity.base.BaseGroupListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.data.DrugDetailAdapter;
import yy.doctor.model.data.GroupDrugDetail;
import yy.doctor.util.Util;

/**
 * 药品详情
 *
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DrugDetailActivity extends BaseGroupListActivity<GroupDrugDetail, String, DrugDetailAdapter> {

    private ImageView mIvCollection;
    private boolean mStoredState = false;  // 默认没有收藏

    public static void nav(Context context, int name) {
        Intent i = new Intent(context, DrugDetailActivity.class)
                .putExtra(Extra.KName, name);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {

        for (int i = 0; i < 12; ++i) {
            GroupDrugDetail groupDrugListDetail = new GroupDrugDetail();
            groupDrugListDetail.addChild("" + i);
            addItem(groupDrugListDetail);
        }

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

    @Override
    public void setViews() {
        super.setViews();

    }

    @Override
    public void getDataFromNet() {

    }

}
