package lib.yy.model.form;

import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.ConstantsEx;
import lib.ys.form.FormEx;
import lib.ys.util.UIUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.ViewUtil;
import lib.yy.R;
import lib.yy.adapter.VH.FormVH;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
abstract public class BaseForm extends FormEx<FormVH> {

    @Override
    protected void refresh(FormVH holder) {
        if (holder.getTvText() != null) {
            if (!getString(TForm.hint).isEmpty()) {
                holder.getTvText().setHint(getString(TForm.hint));
            }
        }

        setTextIfExist(holder.getTvName(), getString(TForm.name));
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
        if (isEmpty(getString(TForm.val))) {
            if (getString(TForm.toast).isEmpty()) {
                String name = getString(TForm.name);
                String toast = null;
                if (name.isEmpty()) {
                    toast = getString(TForm.hint);
                } else {
                    toast = name;
                }

                showToast(ResLoader.getString(R.string.toast_hint_input) + toast);
            } else {
                showToast(getString(TForm.toast));
            }
            return false;
        }/* else {
            // TODO: 完善form regex
            String regex = getString(TForm.regex);
            boolean match = RegexUtil.match(regex, getString(val));
            if (match) {
                return true;
            } else {
                showToast("格式不匹配, 不能含有......");
                return false;
            }
        }*/
        return true;
    }


    /**
     * 检测"请上传"选项
     *
     * @return
     */
//    protected boolean checkUpload() {
//        List list = getList(TForm.data);
//        if (list == null || list.isEmpty()) {
//            if (getString(TForm.toast).isEmpty()) {
//                showToast(ResLoader.getString(R.string.toast_hint_upload) + getString(TForm.name));
//            } else {
//                showToast(getString(TForm.toast));
//            }
//            return false;
//        }
//        return true;
//    }

    //
    protected boolean checkSelector() {
        if (isEmpty(getString(TForm.val))) {
            if (getString(TForm.toast).isEmpty()) {
                showToast(ResLoader.getString(R.string.toast_hint_select) + getString(TForm.name));
            } else {
                showToast(getString(TForm.toast));
            }
            return false;
        }
        return true;
    }
}
