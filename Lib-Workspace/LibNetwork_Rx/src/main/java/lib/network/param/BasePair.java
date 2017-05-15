package lib.network.param;

import android.text.TextUtils;

import lib.network.model.NetworkConstants;


abstract public class BasePair<T> {
    private String mName = NetworkConstants.KEmpty;
    private T mValue;

    public BasePair(String name, T val) {
        setName(name);
        setValue(val);
    }

    public void setName(String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setValue(T value) {
        if (value == null) {
            return;
        }
        mValue = value;
    }

    public T getValue() {
        return mValue;
    }

}
