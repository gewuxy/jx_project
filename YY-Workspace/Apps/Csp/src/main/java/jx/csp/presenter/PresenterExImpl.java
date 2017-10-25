package jx.csp.presenter;

import jx.csp.ui.ViewEx;

/**
 * @auther yuansui
 * @since 2017/10/24
 */

abstract public class PresenterExImpl<V extends ViewEx> {

    private V mV;

    public PresenterExImpl(V v) {
        mV = v;
    }

    protected V getView() {
        return mV;
    }
}
