package yy.doctor.ui.frag.data;

import android.support.annotation.Nullable;

import lib.processor.annotation.AutoArg;

/**
 * @author CaiXiang
 * @since 2017/4/24
 */
@AutoArg
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
