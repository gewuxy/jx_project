package jx.csp.ui.frag.main;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import lib.jx.notify.Notifier;
import lib.jx.ui.frag.base.BaseFrag;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;

/**
 * 单张预览的图片
 *
 * @auther : GuoXuan
 * @since : 2018/2/3
 */
@Route
public class PreviewPhotoFrag extends BaseFrag {

    @Arg
    String mPath;

    private NetworkImageView mIv;

    @Override
    public void initData() {
        // do nothing
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_preview_photo;
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    @Override
    public void findViews() {
        mIv = findView(R.id.preview_photo_iv);
    }

    @Override
    public void setViews() {
        mIv.storage(mPath).load();
        setOnClickListener(mIv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preview_photo_iv: {
                notify(Notifier.NotifyType.finish_preview);
            }
            break;
        }
    }
}
