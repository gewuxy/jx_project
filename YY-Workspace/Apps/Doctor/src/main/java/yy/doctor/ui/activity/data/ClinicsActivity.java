package yy.doctor.ui.activity.data;

import lib.processor.annotation.AutoIntent;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag;
import yy.doctor.ui.frag.data.ClinicsFragArg;
import yy.doctor.util.Util;

/**
 * @auther Huoxuyu
 * @since 2017/8/3
 */
@AutoIntent
public class ClinicsActivity extends BaseDataUnitsActivity {

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.clinical_guide, this);
        bar.addViewRight(R.mipmap.nav_bar_ic_data, v -> this.notify(NotifyType.data_finish));
    }

    @Override
    protected BaseDataUnitsFrag createFrag() {
        return ClinicsFragArg.create(mId, mLeaf).build();
    }
}
