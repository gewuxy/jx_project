package lib.yy.model.form;

import android.support.annotation.StringRes;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.ConstantsEx;
import lib.ys.form.FormEx;
import lib.ys.util.RegexUtil;
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

    private int mPaddingLeft;
    private int mPaddingRight;

    public <T extends BaseForm> T paddingLeft(int padding) {
        mPaddingLeft = padding;
        return (T) this;
    }

    public <T extends BaseForm> T paddingRight(int padding) {
        mPaddingRight = padding;
        return (T) this;
    }

    public int getPaddingLeft() {
        return mPaddingLeft;
    }

    public int getPaddingRight() {
        return mPaddingRight;
    }

    @Override
    protected void refresh(FormVH holder) {
        if (holder.getTvText() != null) {
            if (!isEmpty(getHint())) {
                holder.getTvText().setHint(getHint());
            }
        }

        setTextIfExist(holder.getTvName(), getName());
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

    protected boolean checkRegex() {
        String regex = getRegex();
        boolean match = RegexUtil.match(regex, getVal());
        if (match) {
            return true;
        } else {
            showToast("格式不匹配, 不能含有......");
            return false;
        }
    }

    /**
     * 检测"请输入"选项
     *
     * @return
     */
    protected boolean checkInput() {
        return nativeCheck(R.string.toast_hint_input);
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
        return nativeCheck(R.string.toast_hint_select);
    }

    private boolean nativeCheck(@StringRes int stringId) {
        if (isEmpty(getVal())) {
            if (isEmpty(getToast())) {
                String name = getName();
                String toast = null;
                if (isEmpty(name)) {
                    toast = getHint();
                } else {
                    toast = name;
                }

                showToast(ResLoader.getString(stringId) + toast);
            } else {
                showToast(getToast());
            }
            return false;
        }
        return true;
    }
}
