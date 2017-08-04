package yy.doctor.ui.activity.data;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.interfaces.IListResult;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.network.ListResult;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseSRGroupListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.data.DrugDetailAdapter;
import yy.doctor.model.data.DrugDetail;
import yy.doctor.model.data.DrugDetail.TDrugDetail;
import yy.doctor.model.data.DrugDetailData;
import yy.doctor.model.data.DrugDetailData.TDrugDetailData;
import yy.doctor.model.data.GroupDrugDetail;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 药品详情
 *
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DrugDetailActivity extends BaseSRGroupListActivity<GroupDrugDetail, DrugDetail, DrugDetailAdapter> {

    private ImageView mIvCollection;
    private boolean mStoredState = true;  // 默认有收藏
    private String mDataFileId;
    private String mFileName;
    private String mType = 2+""; // type为2，表示药品目录
    private final int KCollectionState = 0;
    private final int KCollectionDetail = 1;

    public static void nav(Context context, String id, String fileName) {
        Intent i = new Intent(context, DrugDetailActivity.class)
                .putExtra(Extra.KId, id)
                .putExtra(Extra.KName, fileName);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mDataFileId = getIntent().getStringExtra(Extra.KId);
        mFileName = getIntent().getStringExtra(Extra.KName);
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar,mFileName, this);
        // 收藏
        ViewGroup group = bar.addViewRight(R.drawable.collection_selector, v -> {
            mStoredState = !mStoredState;
            mIvCollection.setSelected(mStoredState);
            showToast(mStoredState ? R.string.collect_finish : R.string.cancel_collect);
            exeNetworkReq(KCollectionState,NetFactory.collectionStatus(mDataFileId,mType));
            if (!mStoredState) {
                notify(NotifyType.getCancel_collection_drug,mDataFileId);
            }
        });
        mIvCollection = Util.getBarView(group, ImageView.class);
        mIvCollection.setSelected(true);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(KCollectionDetail,NetFactory.drugDetail(mDataFileId));
    }

    @Override
    public IListResult<GroupDrugDetail> parseNetworkResponse(int id, String text) throws JSONException {
        if (id == KCollectionDetail) {
            Result<DrugDetailData> dataResult = JsonParser.ev(text, DrugDetailData.class);
            ListResult<GroupDrugDetail> result = new ListResult<>();
            result.setCode(dataResult.getCode());

            if (dataResult.isSucceed()) {
                DrugDetailData data = dataResult.getData();
                List<DrugDetail> details = data.getList(TDrugDetailData.detailList);
                for (DrugDetail detail : details) {
                    GroupDrugDetail group = new GroupDrugDetail();
                    group.setTag(detail.getString(TDrugDetail.detailKey));
                    List<DrugDetail> children = new ArrayList<>();
                    children.add(detail);
                    group.setChildren(children);
                    result.add(group);
                }
            }
            return result;
        }else {
            return null;
        }

    }
}
