package jx.doctor.ui.activity.data;

import inject.annotation.router.Route;
import lib.ys.ui.other.NavBar;
import lib.jx.notify.Notifier.NotifyType;
import jx.doctor.R;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag;
import jx.doctor.ui.frag.data.DrugsFragRouter;
import jx.doctor.util.Util;

/**
 * 药品目录
 *
 * @auther Huoxuyu
 * @since 2017/8/3
 */
@Route
public class DrugsActivity extends BaseDataUnitsActivity {


    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.drug_list, this);
        bar.addViewRight(R.drawable.nav_bar_ic_data, v -> this.notify(NotifyType.data_finish));
    }

    @Override
    protected BaseDataUnitsFrag createFrag() {
        return DrugsFragRouter.create(mId, mLeaf).route();
    }

}
