package yy.doctor.ui.activity.data;

import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
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

    IResult onNetworkResponse(int id, NetworkResp resp) throws Exception;

    void onNetworkSuccess(int id, IResult r);
}
