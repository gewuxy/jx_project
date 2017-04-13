package yy.doctor.model.home;

import lib.ys.model.EVal;
import yy.doctor.adapter.VH.HomeVH;
import yy.doctor.model.home.Home.THome;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
abstract public class Home extends EVal<THome> {

    public enum THome {

        re_id,
        /**
         * {@link yy.doctor.adapter.HomeAdapter.HomeType}
         */
        re_type,
        lecturer_title,
        headImg_url

    }

    abstract public void refresh(HomeVH holder);
}
