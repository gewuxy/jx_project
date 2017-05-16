package lib.ys.network.result;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuansui
 */
abstract public class ListResponseSimpleEx<T> implements IListResponse<T> {

    private List<T> mTs = new ArrayList<T>();
    private int mCode;
    private String mLastItemId;
    private String mError;

    /**
     * @param data
     * @hide
     * @deprecated 单个对象使用的, 对于list来说无用
     */
    @Override
    public final void setData(Object data) {
    }

    @Override
    public void setData(List<T> data) {
        mTs = data;
    }

    @Override
    public void add(T item) {
        if (item == null) {
            return;
        }

        if (mTs == null) {
            mTs = new ArrayList<T>();
        }
        mTs.add(item);
    }

    @Override
    public String getLastItemId() {
        return mLastItemId;
    }

    @Override
    public void setLastItemId(String id) {
        mLastItemId = id;
    }

    @Override
    public boolean isSucceed() {
        return mCode == getCodeOk();
    }

    @Override
    public void setError(String error) {
        mError = error;
    }

    @Override
    public String getError() {
        return mError;
    }

    @Override
    public void setCode(int code) {
        mCode = code;
    }

    @Override
    public int getCode() {
        return mCode;
    }

    @Override
    public List<T> getData() {
        return mTs;
    }

    @Override
    abstract public int getCodeOk();
}
