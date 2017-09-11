package yy.doctor.ui.activity.data;

import inject.annotation.router.Route;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag;
import yy.doctor.ui.frag.data.ClinicsFragRouter;
import yy.doctor.util.Util;

/**
 * 临床指南
 *
 * @auther Huoxuyu
 * @since 2017/8/3
 */
@Route
public class ClinicsActivity extends BaseDataUnitsActivity {

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.clinical_guide, this);
        bar.addViewRight(R.drawable.nav_bar_ic_data, v -> this.notify(NotifyType.data_finish));
    }

    @Override
    protected BaseDataUnitsFrag createFrag() {
        return ClinicsFragRouter.create(mId, mLeaf).route();
    }
}
