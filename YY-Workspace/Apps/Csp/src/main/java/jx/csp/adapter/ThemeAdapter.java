package jx.csp.adapter;

import jx.csp.R;
import jx.csp.adapter.share.EditorAdapter;

/**
 * @author CaiXiang
 * @since 2018/2/7
 */

public class ThemeAdapter extends EditorAdapter {

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_theme_item;
    }
}
