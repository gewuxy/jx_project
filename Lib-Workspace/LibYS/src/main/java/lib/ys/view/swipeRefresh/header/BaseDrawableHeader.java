package lib.ys.view.swipeRefresh.header;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import lib.ys.view.swipeRefresh.base.BaseSRLayout;

/**
 * @author yuansui
 */
abstract public class BaseDrawableHeader extends BaseHeader {

    private Drawable mDrawable;

    public BaseDrawableHeader(Context context, BaseSRLayout parent) {
        super(context, parent);
    }

    @Override
    protected View initContentView() {
        ImageView iv = new ImageView(getContext());
        mDrawable = initDrawable(getContext(), getSRLayout());
        iv.setImageDrawable(mDrawable);
        return iv;
    }

    abstract protected Drawable initDrawable(Context context, BaseSRLayout layout);

    protected Drawable getDrawable() {
        return mDrawable;
    }
}
