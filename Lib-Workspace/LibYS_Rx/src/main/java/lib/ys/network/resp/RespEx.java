package lib.ys.network.resp;

abstract public class RespEx<T> implements IResp<T> {

    private int mCode;
    private String mError;
    private T mT;

    public RespEx() {
    }

    public RespEx(T data) {
        mT = data;
    }

    @Override
    public void setData(T data) {
        mT = data;
    }

    @Override
    public T getData() {
        return mT;
    }

    @Override
    public void setCode(int code) {
        mCode = code;
    }

    @Override
    public boolean isSucceed() {
        return mCode == getCodeOk();
    }

    @Override
    public int getCode() {
        return mCode;
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
}
