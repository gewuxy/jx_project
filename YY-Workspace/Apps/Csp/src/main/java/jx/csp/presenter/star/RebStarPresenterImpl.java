package jx.csp.presenter.star;

import jx.csp.contact.StarContract;
import jx.csp.model.main.Meet;
import jx.csp.network.NetworkApiDescriptor;

/**
 * 录播的星评
 *
 * @auther : GuoXuan
 * @since : 2018/1/19
 */
public class RebStarPresenterImpl extends BaseStarPresenterImpl<StarContract.V> {

    public static final int KDeleteMusic = 1;

    public RebStarPresenterImpl(StarContract.V v, Meet meet) {
        super(v, meet);
    }

    @Override
    public void getDataFromNet(int netType) {
        if (netType == KDeleteMusic) {
            String meetId = getMeet().getString(Meet.TMeet.id);
            exeNetworkReq(netType, NetworkApiDescriptor.MeetingAPI.selectBgMusic(meetId, "0").build());
        } else {
            super.getDataFromNet(netType);
        }
    }
}
