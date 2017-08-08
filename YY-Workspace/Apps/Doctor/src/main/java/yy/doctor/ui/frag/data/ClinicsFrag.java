package yy.doctor.ui.frag.data;

import yy.doctor.R;

/**
 * @auther Huoxuyu
 * @since 2017/8/3
 */

public class ClinicsFrag extends BaseDataUnitsFrag {

    @Override
    protected int getDataType() {
        return DataType.clinic;
    }

    @Override
    protected int getSearchId() {
        return R.string.drug_list_search_hint;
    }
}
