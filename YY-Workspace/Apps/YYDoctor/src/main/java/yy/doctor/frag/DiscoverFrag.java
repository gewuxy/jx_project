package yy.doctor.frag;

import lib.yy.frag.base.BaseFormFrag;
import yy.doctor.R;
import yy.doctor.activity.MainActivity;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class DiscoverFrag extends BaseFormFrag {

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_public_number)
                .name("公众号")
                .text("1像素")
                .build());
    }

    @Override
    public void initTitleBar() {
        Util.addMenuIcon(getTitleBar(), (MainActivity) getActivity());
    }
}
