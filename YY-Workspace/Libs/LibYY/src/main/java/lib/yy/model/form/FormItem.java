package lib.yy.model.form;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.ConstantsEx;
import lib.ys.form.FormItemEx;
import lib.ys.util.UIUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.ViewUtil;
import lib.yy.R;
import lib.yy.adapter.VH.FormItemVH;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
abstract public class FormItem extends FormItemEx<FormItemVH> {

    @Override
    protected void init(FormItemVH holder) {
        super.init(holder);

        if (holder.getEt() != null) {
            holder.getEt().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    put(TFormElem.text, s.toString());
                }
            });
        }
    }

    @Override
    protected void refresh(FormItemVH holder) {
        if (holder.getTvText() != null) {
            if (!getString(TFormElem.hint).isEmpty()) {
                holder.getTvText().setHint(getString(TFormElem.hint));
            }
        }

        setTextIfExist(holder.getTvName(), getString(TFormElem.name));
    }

    protected void setTextIfExist(TextView tv, CharSequence text) {
        UIUtil.setTvTextIfExist(tv, text);
    }

    /**
     * 如果图片有效则设置
     */
    protected void setIvIfValid(ImageView iv, int drawableId) {
        if (iv == null) {
            return;
        }

        if (drawableId != ConstantsEx.KInvalidValue) {
            ViewUtil.showView(iv);
            iv.setImageResource(drawableId);
        } else {
            ViewUtil.goneView(iv);
        }
    }

    abstract public boolean check();

    /**
     * 检测"请输入"选项
     *
     * @return
     */
    protected boolean checkInput() {
        if (isEmpty(getString(TFormElem.val))) {
            if (getString(TFormElem.toast).isEmpty()) {
                String name = getString(TFormElem.name);
                String toast = null;
                if (name.isEmpty()) {
                    toast = getString(TFormElem.hint);
                } else {
                    toast = name;
                }

                showToast(ResLoader.getString(R.string.toast_hint_input) + toast);
            } else {
                showToast(getString(TFormElem.toast));
            }
            return false;
        }
        return true;
    }
//
//    /**
//     * 检测"请上传"选项
//     *
//     * @return
//     */
//    protected boolean checkUpload() {
//        List list = getList(TFormElem.data);
//        if (list == null || list.isEmpty()) {
//            if (getString(TFormElem.toast).isEmpty()) {
//                showToast(ResLoader.getString(R.string.toast_hint_upload) + getString(TFormElem.name));
//            } else {
//                showToast(getString(TFormElem.toast));
//            }
//            return false;
//        }
//        return true;
//    }
//
//    protected boolean checkSelector() {
//        if (isEmpty(getString(TFormElem.val))) {
//            if (getString(TFormElem.toast).isEmpty()) {
//                showToast(ResLoader.getString(R.string.toast_hint_select) + getString(TFormElem.name));
//            } else {
//                showToast(getString(TFormElem.toast));
//            }
//            return false;
//        }
//        return true;
//    }
}
