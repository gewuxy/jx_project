package jx.csp.presenter.star;

import jx.csp.contact.StarContract;
import jx.csp.model.main.Meet;

/**
 * 直播的星评
 *
 * @auther : GuoXuan
 * @since : 2018/1/19
 */
public class LiveStarPresenterImpl extends BaseStarPresenterImpl<StarContract.V> {

    public LiveStarPresenterImpl(StarContract.V v, Meet meet) {
        super(v, meet);
    }
}
