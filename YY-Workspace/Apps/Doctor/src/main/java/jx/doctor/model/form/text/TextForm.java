package jx.doctor.model.form.text;

import android.widget.TextView;

import lib.ys.ConstantsEx;
import lib.ys.YSLog;
import lib.ys.network.image.NetworkImageView;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.adapter.VH.FormVH;
import lib.yy.model.form.BaseForm;
import jx.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class TextForm extends BaseForm {

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
            YSLog.d(TAG, "refresh:" + getUrl());
            ViewUtil.showView(iv);
        }

        TextView tvText = holder.getTvText();
        if (getHintTextColor() != null) {
            tvText.setHintTextColor(getHintTextColor());
        }
        tvText.setHint(getHint());

        if (getTextColor() != null) {
            tvText.setTextColor(getTextColor());
        }
        tvText.setText(getText());
    }

    @Override
    public boolean check() {
        return checkSelector();
    }

}
