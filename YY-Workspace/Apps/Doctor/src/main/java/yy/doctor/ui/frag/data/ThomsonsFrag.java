package yy.doctor.ui.frag.data;

import android.support.annotation.Nullable;

/**
 * @author CaiXiang
 * @since 2017/4/24
 */
public class ThomsonsFrag extends BaseDataUnitsFrag {

    @Override
    protected int getDataType() {
        return DataType.thomson;
    }

    @Nullable
    @Override
    public int getContentHeaderViewId() {
        return 0;
    }
}
