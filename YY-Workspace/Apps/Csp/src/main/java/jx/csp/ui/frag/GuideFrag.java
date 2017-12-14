package jx.csp.ui.frag;

import android.support.annotation.NonNull;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import lib.jx.ui.frag.base.BaseFrag;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;

/**
 * @author CaiXiang
 * @since 2017/12/14
 */
@Route
public class GuideFrag extends BaseFrag {

    @Arg
    int mImgResId;

    private NetworkImageView mIv;

    @Override
    public void initData() {}

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_guide;
    }

    @Override
    public void initNavBar(NavBar bar) {}

    @Override
    public void findViews() {
        mIv = findView(R.id.frag_guide_iv);
    }

    @Override
    public void setViews() {
        mIv.res(mImgResId).load();
    }
}
