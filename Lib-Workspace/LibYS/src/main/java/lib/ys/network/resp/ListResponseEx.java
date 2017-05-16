package lib.ys.network.result;

import java.util.ArrayList;
import java.util.List;

import lib.ys.ConstantsEx;
import lib.ys.model.EVal;

/**
 * 网络返回列表类型基础
 *
 * @param <T> 单个对象类
 * @param <E> 整个文本的关键字段
 * @author yuansui
 */
@SuppressWarnings("serial")
abstract public class ListResponseEx<T, E extends Enum<E>> extends EVal<E> implements IListResponse<T> {

    private List<T> mTs = new ArrayList<T>();
    private int mCode;
    private String mLastItemId;
    private String mError = ConstantsEx.KEmptyValue;


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
    public List<T> getData() {
        return mTs;
    }

    public int getCount() {
        return mTs == null ? 0 : mTs.size();
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
    abstract public int getCodeOk();

    @Override
    public String getLastItemId() {
        return mLastItemId;
    }

    @Override
    public void setLastItemId(String id) {
        mLastItemId = id;
    }
}
