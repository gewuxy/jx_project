package jx.doctor.ui.frag.data;

import android.support.annotation.Nullable;

/**
 * @author CaiXiang
 * @since 2017/4/24
 */
public class ThomsonFrag extends BaseDataUnitsFrag {

    @Override
    protected int getDataType() {
        return DataType.thomson;
    }

    @Nullable
    @Override
    public int getContentHeaderViewId() {
        return 0;
    }

    @Override
    public void setViews() {
        super.setViews();

        getAdapter().setDataType(DataType.thomson, DataFrom.data);
    }

}
