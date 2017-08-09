package yy.doctor.model.form.text;

import lib.ys.network.image.NetworkImageView;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.adapter.VH.FormVH;
import lib.yy.model.form.BaseForm;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class TextForm extends BaseForm {

    @Override
    public int getContentViewResId() {
        return R.layout.form_text;
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

        holder.getTvText().setText(getText());
    }

    @Override
    public boolean check() {
        return checkSelector();
    }

}
