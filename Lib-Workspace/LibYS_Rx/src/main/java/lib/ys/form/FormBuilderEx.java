package lib.ys.form;

import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import java.util.List;

import lib.ys.form.FormEx.TFormElem;
import lib.ys.util.res.ResLoader;


/**
 * Form item builder
 *
 * @author yuansui
 */
abstract public class FormBuilderEx<FORM extends FormEx> {

    private int mType;

    private Object mVal;
    private Object mKey;

    private String mName;
    private CharSequence mText;
    private CharSequence[] mTextMulti;
    private String mHint;
    private String mTips;
    private boolean mEnable;
    private Integer mLimit;
    private Object mRelated;
    private Object mData;
    private Object mOption;
    private Object mHost;
    private Integer mColumn;
    private Integer mWidth;
    private Integer mHeight;
    private Integer mMode;
    private Intent mIntent;
    private List mChildren;
    private Object mDepend;
    private String mRegex;
    private boolean mCheck;
    private Integer mIndex;
    private Integer mId;
    private boolean mVisible;
    private Object mObserver;

    @ColorInt
    private int mBgColor;

    @DrawableRes
    private Integer mDrawableId;

    private String mDrawableUrl;

    @LayoutRes
    private Integer mLayoutId;

    @DrawableRes
    private Integer mTextColorId;

    private String mToast;


    public FormBuilderEx(int type) {
        mType = type;
        mEnable = initEnable();
        mVisible = true;
    }

    public <T extends FormBuilderEx<FORM>> T name(String name) {
        mName = name;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T val(Object val) {
        mVal = val;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T key(Object key) {
        mKey = key;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T name(@StringRes int id) {
        mName = ResLoader.getString(id);
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T text(CharSequence text) {
        mText = text;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T multiText(CharSequence... texts) {
        mTextMulti = texts;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T multiText(@StringRes int... ids) {
        int len = ids.length;
        if (len == 0) {
            return (T) this;
        }

        mTextMulti = new CharSequence[len];
        for (int i = 0; i < len; ++i) {
            mTextMulti[i] = ResLoader.getString(ids[i]);
        }

        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T text(@StringRes int id) {
        mText = ResLoader.getString(id);
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T hint(String hint) {
        mHint = hint;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T hint(@StringRes int id) {
        mHint = ResLoader.getString(id);
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T tips(String tips) {
        mTips = tips;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T tips(@StringRes int id) {
        mTips = ResLoader.getString(id);
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T enable(boolean able) {
        mEnable = able;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T related(Object related) {
        mRelated = related;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T data(Object data) {
        mData = data;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T option(Object option) {
        mOption = option;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T host(Object host) {
        mHost = host;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T width(int w) {
        mWidth = w;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T height(int h) {
        mHeight = h;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T column(int c) {
        mColumn = c;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T limit(int limit) {
        mLimit = limit;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T background(@ColorInt int color) {
        mBgColor = color;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T backgroundRes(@ColorRes int id) {
        mBgColor = ResLoader.getColor(id);
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T mode(int mode) {
        mMode = mode;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T drawable(@DrawableRes int id) {
        mDrawableId = id;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T drawable(String url) {
        mDrawableUrl = url;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T textColor(@DrawableRes int id) {
        mTextColorId = id;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T layout(@LayoutRes int id) {
        mLayoutId = id;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T toast(String toast) {
        mToast = toast;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T intent(Intent i) {
        mIntent = i;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T children(List children) {
        mChildren = children;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T depend(Object depend) {
        mDepend = depend;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T regex(String regex) {
        mRegex = regex;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T check(boolean check) {
        mCheck = check;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T index(int index) {
        mIndex = index;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T id(int id) {
        mId = id;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T visible(boolean visible) {
        mVisible = visible;
        return (T) this;
    }

    public <T extends FormBuilderEx<FORM>> T observer(OnFormObserver observer) {
        mObserver = observer;
        return (T) this;
    }

    public FORM build() {
        FORM i = build(mType);
        saveItemValues(i);
        return i;
    }

    abstract protected FORM build(int type);

    private void saveItemValues(FORM FORM) {
        putIfNotNull(FORM, TFormElem.name, mName);
        putIfNotNull(FORM, TFormElem.text, mText);
        putIfNotNull(FORM, TFormElem.text_multi, mTextMulti);
        putIfNotNull(FORM, TFormElem.hint, mHint);
        putIfNotNull(FORM, TFormElem.tips, mTips);
        putIfNotNull(FORM, TFormElem.enable, mEnable);
        putIfNotNull(FORM, TFormElem.related, mRelated);
        putIfNotNull(FORM, TFormElem.data, mData);
        putIfNotNull(FORM, TFormElem.option, mOption);
        putIfNotNull(FORM, TFormElem.host, mHost);
        putIfNotNull(FORM, TFormElem.width, mWidth);
        putIfNotNull(FORM, TFormElem.height, mHeight);
        putIfNotNull(FORM, TFormElem.column, mColumn);
        putIfNotNull(FORM, TFormElem.background, mBgColor);
        putIfNotNull(FORM, TFormElem.mode, mMode);
        putIfNotNull(FORM, TFormElem.limit, mLimit);
        putIfNotNull(FORM, TFormElem.drawable, mDrawableId);
        putIfNotNull(FORM, TFormElem.drawable, mDrawableUrl);
        putIfNotNull(FORM, TFormElem.layout, mLayoutId);
        putIfNotNull(FORM, TFormElem.toast, mToast);
        putIfNotNull(FORM, TFormElem.intent, mIntent);
        putIfNotNull(FORM, TFormElem.children, mChildren);
        putIfNotNull(FORM, TFormElem.depend, mDepend);
        putIfNotNull(FORM, TFormElem.regex, mRegex);
        putIfNotNull(FORM, TFormElem.check, mCheck);
        putIfNotNull(FORM, TFormElem.index, mIndex);
        putIfNotNull(FORM, TFormElem.id, mId);
        putIfNotNull(FORM, TFormElem.visible, mVisible);
        putIfNotNull(FORM, TFormElem.observer, mObserver);
        putIfNotNull(FORM, TFormElem.text_color, mTextColorId);

        putIfNotNull(FORM, TFormElem.key, mKey);
        putIfNotNull(FORM, TFormElem.val, mVal);
    }

    private void putIfNotNull(FORM FORM, TFormElem key, Object value) {
        if (value != null) {
            FORM.put(key, value);
        }
    }

    /**
     * 初始化时用
     *
     * @return
     */
    abstract protected boolean initEnable();
}
