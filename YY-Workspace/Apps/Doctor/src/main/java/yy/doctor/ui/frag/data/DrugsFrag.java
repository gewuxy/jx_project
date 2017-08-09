package yy.doctor.ui.frag.data;

import lib.processor.annotation.AutoArg;
import yy.doctor.R;

/**
 * @auther Huoxuyu
 * @since 2017/8/3
 */
@AutoArg
public class DrugsFrag extends BaseDataUnitsFrag {

    @Override
    protected int getDataType() {
        return DataType.drug;
    }

    @Override
    protected int getSearchId() {
        return R.string.drug_list_search_hint;
    }

}
