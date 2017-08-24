package yy.doctor.ui.frag.data;

import inject.annotation.router.Route;
import yy.doctor.R;

/**
 * @auther Huoxuyu
 * @since 2017/8/3
 */
@Route
public class ClinicsFrag extends BaseDataUnitsFrag {

    @Override
    protected int getDataType() {
        return DataType.clinic;
    }

    @Override
    protected int getSearchId() {
        return R.string.clinical_guide_search_hint;
    }

    @Override
    public void setViews() {
        super.setViews();

        getAdapter().setDataType(DataType.clinic);
    }
}
