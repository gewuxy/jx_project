package yy.doctor.ui.activity.data;

import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.interfaces.IListResult;
import lib.processor.annotation.AutoIntent;
import lib.processor.annotation.Extra;
import lib.ys.ui.other.NavBar;
import lib.yy.network.ListResult;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseSRGroupListActivity;
import yy.doctor.R;
import yy.doctor.adapter.data.DrugDetailAdapter;
import yy.doctor.model.data.DrugDetail;
import yy.doctor.model.data.DrugDetail.TDrugDetail;
import yy.doctor.model.data.DataDetail;
import yy.doctor.model.data.DataDetail.TDrugDetailData;
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
@AutoIntent
public class DrugDetailActivity extends BaseSRGroupListActivity<GroupDrugDetail, DrugDetail, DrugDetailAdapter> {

    private final String KType = "2"; // type为2，表示药品目录
    private final int KCollectionState = 0;
    private final int KCollectionDetail = 1;

    @Extra(optional = true)
    String mDataFileId;

    @Extra(optional = true)
    String mFileName;

    private ImageView mIvCollection;

    private boolean mStoredState ;  // 默认没有收藏
    private DataDetail mData;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, mFileName, this);
        // 收藏
        ViewGroup group = bar.addViewRight(R.drawable.collection_selector, v -> {
            mStoredState = !mStoredState;
            mData.put(TDrugDetailData.favorite,mStoredState);
            mIvCollection.setSelected(mStoredState);
            showToast(mStoredState ? R.string.collect_finish : R.string.cancel_collect);
            exeNetworkReq(KCollectionState, NetFactory.collectionStatus(mDataFileId, KType));
            if (!mStoredState) {
                notify(NotifyType.getCancel_collection_drug, mDataFileId);
            }
        });
        mIvCollection = Util.getBarView(group, ImageView.class);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(KCollectionDetail, NetFactory.collectionDetail(mDataFileId));
    }

    @Override
    public IListResult<GroupDrugDetail> parseNetworkResponse(int id, String text) throws JSONException {
        if (id == KCollectionDetail) {
            Result<DataDetail> dataResult = JsonParser.ev(text, DataDetail.class);
            ListResult<GroupDrugDetail> result = new ListResult<>();
            result.setCode(dataResult.getCode());

            if (dataResult.isSucceed()) {
                mData = dataResult.getData();
                List<DrugDetail> details = mData.getList(TDrugDetailData.detailList);
                mStoredState = mData.getBoolean(TDrugDetailData.favorite);
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
        } else {
            return null;
        }
    }

    @Override
    public void onNetRefreshSuccess() {
        super.onNetRefreshSuccess();

        mIvCollection.setSelected(mStoredState);
    }
}
