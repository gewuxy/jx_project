package jx.csp.ui.frag.main;

import android.graphics.Color;

import java.util.List;

import lib.jx.ui.frag.base.BaseVPFrag;
import lib.ys.ui.other.NavBar;

/**
 * 预览多图
 *
 * @auther : GuoXuan
 * @since : 2018/2/7
 */
public class PreviewChoicePhotoFrag extends BaseVPFrag {

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {

    }

    @Override
    public void setViews() {
        super.setViews();

        setBackgroundColor(Color.BLACK);
    }

    public void setPhotos(List<String> photos) {
        if (photos == null || photos.isEmpty()) {
            return;
        }
        removeAll();
        for (String photo : photos) {
            add(PreviewPhotoFragRouter.create(photo).route());
        }
        invalidate();
    }
}
