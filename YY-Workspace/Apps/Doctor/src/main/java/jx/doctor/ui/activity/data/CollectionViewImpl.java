package jx.doctor.ui.activity.data;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.activity.ActivityEx;
import lib.ys.ui.other.NavBar;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import jx.doctor.App;
import jx.doctor.R;
import jx.doctor.model.data.DataUnitDetails;
import jx.doctor.model.data.DataUnitDetails.TDataUnitDetails;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.CollectionAPI;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import jx.doctor.util.UISetter;
import jx.doctor.util.Util;

/**
 * 我的收藏
 *
 * @auther yuansui
 * @since 2017/8/9
 */

public class CollectionViewImpl implements ICollectionView {

    private NavBar mBar;
    private View mIv;

    private String mFileId;
    private DataUnitDetails mData;
    private Object mHost;
    @DataType
    private int mType;

    public CollectionViewImpl(NavBar bar, String fileId, @DataType int type, Object host) {
        mBar = bar;
        mFileId = fileId;
        mHost = host;
        mType = type;

        addCollectionBtn();
    }

    @Override
    public void setData(DataUnitDetails data) {
        mData = data;
        setState(mData.getBoolean(TDataUnitDetails.favorite));
    }

    @Override
    public void setState(boolean state) {
        if (mIv != null) {
            mIv.setSelected(state);
        }
    }

    @Override
    public void addCollectionBtn() {
        // 收藏
        ViewGroup group = mBar.addViewRight(R.drawable.collection_selector, v -> {
            changeCollectionState();
        });

        mIv = Util.getBarView(group, ImageView.class);
        mIv.setSelected(false);
    }

    @Override
    public void changeCollectionState() {
        if (mData == null) {
            return;
        }

        // 无网
        if (Util.noNetwork()) {
            return;
        }

        boolean state = mData.getBoolean(TDataUnitDetails.favorite, false);
        state = !state;
        UISetter.setCollectionState(mIv, mData);

        App.showToast(state ? R.string.collect_finish : R.string.cancel_collect);

        // 暂时只有两种
        if (mHost instanceof ActivityEx) {
            ActivityEx act = (ActivityEx) mHost;
            act.exeNetworkReq(KIdState, CollectionAPI.collectionStatus(mFileId, mType).build());
        }

        if (!state) {
            @NotifyType int notifyType;
            switch (mType) {
                case DataType.thomson: {
                    notifyType = NotifyType.collection_cancel_thomson;
                }
                break;
                case DataType.drug: {
                    notifyType = NotifyType.collection_cancel_drug;
                }
                break;
                case DataType.clinic: {
                    notifyType = NotifyType.collection_cancel_clinic;
                }
                break;
                case DataType.meeting: {
                    notifyType = NotifyType.collection_cancel_meeting;
                }
                break;
                default: {
                    notifyType = NotifyType.un_know;
                }
                break;
            }

            if (notifyType != NotifyType.un_know) {
                Notifier.inst().notify(notifyType, mFileId);
            }
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.error(resp.getText());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
    }
}
