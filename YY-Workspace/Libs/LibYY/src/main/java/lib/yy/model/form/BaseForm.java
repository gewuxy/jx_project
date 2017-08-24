package lib.yy.model.form;

import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.text.InputFilter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lib.ys.ConstantsEx;
import lib.ys.form.FormEx;
import lib.ys.form.OnFormObserver;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
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

    private int mType;
    private String mUrl;

    private InputFilter[] mInputFilter;

    public <T extends BaseForm> T paddingLeft(int padding) {
        mPaddingLeft = padding;
        return (T) this;
    }

    public <T extends BaseForm> T paddingRight(int padding) {
        mPaddingRight = padding;
        return (T) this;
    }

    public <T extends BaseForm> T type(int type) {
        mType = type;
        return (T) this;
    }

    public <T extends BaseForm> T url(String url) {
        mUrl = url;
        return (T) this;
    }
    public <T extends BaseForm> T input(InputFilter[] inputFilter) {
        mInputFilter = inputFilter;
        return (T) this;
    }

    public int getPaddingLeft() {
        return mPaddingLeft;
    }

    public int getType() {
        return mType;
    }

    public int getPaddingRight() {
        return mPaddingRight;
    }

    public String getUrl() {
        return mUrl;
    }

    public InputFilter[] getInputFilter() {
        return mInputFilter;
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

    @Override
    public BaseForm name(@StringRes int id) {
        return super.name(id);
    }

    @Override
    public BaseForm name(String name) {
        return super.name(name);
    }

    @Override
    public BaseForm key(String key) {
        return super.key(key);
    }

    @Override
    public BaseForm val(String val) {
        return super.val(val);
    }

    @Override
    public BaseForm text(String text) {
        return super.text(text);
    }

    @Override
    public BaseForm texts(String... texts) {
        return super.texts(texts);
    }

    @Override
    public BaseForm texts(@StringRes int... ids) {
        return super.texts(ids);
    }

    @Override
    public BaseForm text(@StringRes int id) {
        return super.text(id);
    }

    @Override
    public BaseForm hint(String hint) {
        return super.hint(hint);
    }

    @Override
    public BaseForm hint(@StringRes int id) {
        return super.hint(id);
    }

    @Override
    public BaseForm tips(String tips) {
        return super.tips(tips);
    }

    @Override
    public BaseForm tips(@StringRes int id) {
        return super.tips(id);
    }

    @Override
    public BaseForm enable(boolean able) {
        return super.enable(able);
    }

    @Override
    public BaseForm related(Object related) {
        return super.related(related);
    }

    @Override
    public BaseForm data(Object data) {
        return super.data(data);
    }

    @Override
    public BaseForm option(Object option) {
        return super.option(option);
    }

    @Override
    public BaseForm host(Object host) {
        return super.host(host);
    }

    @Override
    public BaseForm width(int w) {
        return super.width(w);
    }

    @Override
    public BaseForm height(int h) {
        return super.height(h);
    }

    @Override
    public BaseForm column(int c) {
        return super.column(c);
    }

    @Override
    public BaseForm limit(int limit) {
        return super.limit(limit);
    }

    @Override
    public BaseForm background(@ColorInt int color) {
        return super.background(color);
    }

    @Override
    public BaseForm backgroundRes(@ColorRes int id) {
        return super.backgroundRes(id);
    }

    @Override
    public BaseForm mode(int mode) {
        return super.mode(mode);
    }

    @Override
    public BaseForm drawable(@DrawableRes int id) {
        return super.drawable(id);
    }

    @Override
    public BaseForm layout(@LayoutRes int id) {
        return super.layout(id);
    }

    @Override
    public BaseForm textColorRes(@ColorRes int id) {
        return super.textColorRes(id);
    }

    @Override
    public BaseForm textColor(@ColorInt int color) {
        return super.textColor(color);
    }

    @Override
    public BaseForm toast(String toast) {
        return super.toast(toast);
    }

    @Override
    public BaseForm intent(Intent i) {
        return super.intent(i);
    }

    @Override
    public BaseForm children(List children) {
        return super.children(children);
    }

    @Override
    public BaseForm depend(Object depend) {
        return super.depend(depend);
    }

    @Override
    public BaseForm regex(String regex) {
        return super.regex(regex);
    }

    @Override
    public BaseForm check(boolean check) {
        return super.check(check);
    }

    @Override
    public BaseForm index(int index) {
        return super.index(index);
    }

    @Override
    public BaseForm id(int id) {
        return super.id(id);
    }

    @Override
    public BaseForm visible(boolean visible) {
        return super.visible(visible);
    }

    @Override
    public BaseForm observer(OnFormObserver observer) {
        return super.observer(observer);
    }

    private void observed(String val) {
        if (getObserver() != null) {
            getObserver().callback(getRelated(), TextUtil.isNotEmpty(val));
        }
    }

    @Override
    public void save(String key, String val, String text) {
        super.save(key, val, text);
        observed(val);
    }

    @Override
    public void save(String val, String text) {
        super.save(val, text);
        observed(val);
    }
}
