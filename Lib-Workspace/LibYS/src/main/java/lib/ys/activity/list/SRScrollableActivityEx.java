package lib.ys.activity.list;

import lib.ys.R;

/**
 * @author yuansui
 */
abstract public class SRScrollableActivityEx<T> extends SRListActivityEx<T> {

    @Override
    public int getListViewResId() {
        return R.id.list;
    }

    /**
     * 子布局才会知道
     *
     * @return
     */
    @Override
    abstract public int getSRLayoutResId();

    @Override
    abstract public int getContentViewId();
}
