package yy.doctor.ui.frag.data;

import inject.annotation.router.Route;
import yy.doctor.R;

/**
 * @auther Huoxuyu
 * @since 2017/8/3
 */
@Route
public class DrugsFrag extends BaseDataUnitsFrag {

    @Override
    protected int getDataType() {
        return DataType.drug;
    }

    @Override
    protected int getSearchId() {
        return R.string.drug_list_search_hint;
    }

    @Override
    public void setViews() {
        super.setViews();

        getAdapter().setDataType(DataType.drug, DataFrom.data);
    }
}
