package jx.csp.presenter;

import jx.csp.contact.ProfileContract;
import lib.yy.contract.BasePresenterImpl;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public class ProfilePresenterImpl extends BasePresenterImpl<ProfileContract.V> implements ProfileContract.P{

    public ProfilePresenterImpl(ProfileContract.V v) {
        super(v);
    }
}
