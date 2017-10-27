package jx.csp.presenter;

import jx.csp.contact.ProfileContract;
import jx.csp.contact.ProfileContract.V;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public class ProfilePresenterImpl extends PresenterExImpl<ProfileContract.V> implements ProfileContract.P{

    public ProfilePresenterImpl(V v) {
        super(v);
    }
}
