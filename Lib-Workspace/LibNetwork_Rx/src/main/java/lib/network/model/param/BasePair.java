package lib.network.model.param;

import android.text.TextUtils;

import lib.network.NetworkConstants;


abstract public class BasePair<T> {
    private String mName = NetworkConstants.KEmpty;
    private T mVal;

    public BasePair(String name, T val) {
        setName(name);
        setVal(val);
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

    public void setVal(T val) {
        if (val == null) {
            return;
        }
        mVal = val;
    }

    public T getVal() {
        return mVal;
    }

}
