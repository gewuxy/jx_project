package yy.doctor.ui.activity.data;

import lib.network.model.NetworkResp;
import yy.doctor.model.data.DataUnitDetails;

/**
 * @auther yuansui
 * @since 2017/8/9
 */

public interface ICollectionView {

    int KIdState = 0;
    int KIdDetail = 1;

    void setData(DataUnitDetails data);

    void setState(boolean state);

    void addCollectionBtn();

    void changeCollectionState();

    Object onNetworkResponse(int id, NetworkResp r) throws Exception;

    void onNetworkSuccess(int id, Object result);
}
