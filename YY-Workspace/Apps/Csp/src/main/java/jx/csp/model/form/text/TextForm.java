package jx.csp.model.form.text;

import android.widget.TextView;

import jx.csp.R;
import lib.jx.adapter.VH.FormVH;
import lib.jx.model.form.BaseForm;
import lib.ys.ConstantsEx;
import lib.ys.network.image.NetworkImageView;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;

/**
 * @auther HuoXuYu
 * @since 2017/9/21
 */

public class TextForm extends BaseForm {

    @Override
    public boolean check() {
        return checkSelector();
    }

    @Override
    public int getContentViewResId() {
        int layout = getLayoutId();
        if (layout != ConstantsEx.KInvalidValue) {
            return layout;
        }
        return R.layout.form_text;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);

        if (!isEnabled()) {
            ViewUtil.hideView(holder.getIvArrow());
            holder.getConvertView().setClickable(false);
        }
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);

        NetworkImageView iv = holder.getIv();
        if (TextUtil.isEmpty(getUrl())) {
            setIvIfValid(iv, getDrawable());
        } else {
            iv.url(getUrl()).load();
            ViewUtil.showView(iv);
        }

        TextView tvName = holder.getTvName();
        if (getNameColor() != null) {
            tvName.setTextColor(getNameColor());
        }
    }
}
