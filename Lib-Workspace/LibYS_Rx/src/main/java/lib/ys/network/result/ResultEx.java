package lib.ys.network.result;

import lib.network.model.NetworkError;
import lib.network.model.NetworkErrorBuilder;
import lib.network.model.interfaces.IResult;

abstract public class ResultEx<T> implements IResult<T> {

    private int mCode;
    private String mMessage;
    private T mT;
    private NetworkError mError;

    public ResultEx() {
    }

    public ResultEx(T data) {
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
    public void setMessage(String message) {
        mMessage = message;
    }

    @Override
    public String getMessage() {
        return mMessage;
    }

    @Override
    public NetworkError getError() {
        if (mError == null) {
            mError = NetworkErrorBuilder.create()
                    .code(mCode)
                    .message(mMessage)
                    .build();
        }
        return mError;
    }

    @Override
    public void setError(NetworkError err) {
        mError = err;
    }

    @Override
    abstract public int getCodeOk();
}
