package yy.doctor.frag;

import android.graphics.Color;

import lib.yy.frag.base.BaseFormFrag;
import yy.doctor.R;
import yy.doctor.model.form.menu.Builder;
import yy.doctor.model.form.menu.MenuType;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
public class MenuFrag extends BaseFormFrag {

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(MenuType.divider)
                .height(fitDp(50))
                .background(Color.TRANSPARENT)
                .build());

        addItem(new Builder(MenuType.divider).build());

        addItem(new Builder(MenuType.child)
                .name("消息")
                .drawable(R.mipmap.menu_ic_msg)
                .build());
    }

    @Override
    public void initTitleBar() {
    }
}
