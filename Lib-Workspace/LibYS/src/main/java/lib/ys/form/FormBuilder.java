package lib.ys.form;

import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import java.util.List;

import lib.ys.form.FormItemEx.TFormElem;
import lib.ys.util.res.ResLoader;


/**
 * Form item builder
 *
 * @author yuansui
 */
abstract public class FormBuilder<Item extends FormItemEx> {

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

    private String mToast;


    public FormBuilder(int type) {
        mType = type;
        mEnable = initEnable();
        mVisible = true;
    }

    public <T extends FormBuilder<Item>> T name(String name) {
        mName = name;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T val(Object val) {
        mVal = val;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T key(Object key) {
        mKey = key;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T name(@StringRes int id) {
        mName = ResLoader.getString(id);
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T text(CharSequence text) {
        mText = text;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T multiText(CharSequence... texts) {
        mTextMulti = texts;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T multiText(@StringRes int... ids) {
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

    public <T extends FormBuilder<Item>> T text(@StringRes int id) {
        mText = ResLoader.getString(id);
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T hint(String hint) {
        mHint = hint;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T hint(@StringRes int id) {
        mHint = ResLoader.getString(id);
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T tips(String tips) {
        mTips = tips;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T tips(@StringRes int id) {
        mTips = ResLoader.getString(id);
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T enable(boolean able) {
        mEnable = able;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T related(Object related) {
        mRelated = related;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T data(Object data) {
        mData = data;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T option(Object option) {
        mOption = option;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T host(Object host) {
        mHost = host;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T width(int w) {
        mWidth = w;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T height(int h) {
        mHeight = h;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T column(int c) {
        mColumn = c;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T limit(int limit) {
        mLimit = limit;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T background(@ColorInt int color) {
        mBgColor = color;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T backgroundRes(@ColorRes int id) {
        mBgColor = ResLoader.getColor(id);
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T mode(int mode) {
        mMode = mode;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T drawable(@DrawableRes int id) {
        mDrawableId = id;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T drawable(String url) {
        mDrawableUrl = url;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T layout(@LayoutRes int id) {
        mLayoutId = id;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T toast(String toast) {
        mToast = toast;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T intent(Intent i) {
        mIntent = i;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T children(List children) {
        mChildren = children;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T depend(Object depend) {
        mDepend = depend;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T regex(String regex) {
        mRegex = regex;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T check(boolean check) {
        mCheck = check;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T index(int index) {
        mIndex = index;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T id(int id) {
        mId = id;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T visible(boolean visible) {
        mVisible = visible;
        return (T) this;
    }

    public <T extends FormBuilder<Item>> T observer(OnFormObserver observer) {
        mObserver = observer;
        return (T) this;
    }

    public Item build() {
        return build(mType);
    }

    abstract protected Item build(int type);

    protected void saveItemValues(Item item) {
        putIfNotNull(item, TFormElem.name, mName);
        putIfNotNull(item, TFormElem.text, mText);
        putIfNotNull(item, TFormElem.text_multi, mTextMulti);
        putIfNotNull(item, TFormElem.hint, mHint);
        putIfNotNull(item, TFormElem.tips, mTips);
        putIfNotNull(item, TFormElem.enable, mEnable);
        putIfNotNull(item, TFormElem.related, mRelated);
        putIfNotNull(item, TFormElem.data, mData);
        putIfNotNull(item, TFormElem.option, mOption);
        putIfNotNull(item, TFormElem.host, mHost);
        putIfNotNull(item, TFormElem.width, mWidth);
        putIfNotNull(item, TFormElem.height, mHeight);
        putIfNotNull(item, TFormElem.column, mColumn);
        putIfNotNull(item, TFormElem.background, mBgColor);
        putIfNotNull(item, TFormElem.mode, mMode);
        putIfNotNull(item, TFormElem.limit, mLimit);
        putIfNotNull(item, TFormElem.drawable, mDrawableId);
        putIfNotNull(item, TFormElem.drawable, mDrawableUrl);
        putIfNotNull(item, TFormElem.layout, mLayoutId);
        putIfNotNull(item, TFormElem.toast, mToast);
        putIfNotNull(item, TFormElem.intent, mIntent);
        putIfNotNull(item, TFormElem.children, mChildren);
        putIfNotNull(item, TFormElem.depend, mDepend);
        putIfNotNull(item, TFormElem.regex, mRegex);
        putIfNotNull(item, TFormElem.check, mCheck);
        putIfNotNull(item, TFormElem.index, mIndex);
        putIfNotNull(item, TFormElem.id, mId);
        putIfNotNull(item, TFormElem.visible, mVisible);
        putIfNotNull(item, TFormElem.observer, mObserver);

        putIfNotNull(item, TFormElem.key, mKey);
        putIfNotNull(item, TFormElem.val, mVal);
    }

    private void putIfNotNull(Item item, TFormElem key, Object value) {
        if (value != null) {
            item.put(key, value);
        }
    }

    /**
     * 初始化时用
     *
     * @return
     */
    abstract protected boolean initEnable();
}
