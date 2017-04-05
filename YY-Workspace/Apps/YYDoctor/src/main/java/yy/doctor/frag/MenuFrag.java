package yy.doctor.frag;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.yy.frag.base.BaseListFrag;
import yy.doctor.adapter.MenuAdapter;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
public class MenuFrag extends BaseListFrag<String> {

    @Override
    public void initData() {
        addItem("123");
        addItem("456");
    }

    @Override
    public void initTitleBar() {
    }

    @Override
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new MenuAdapter();
    }
}
