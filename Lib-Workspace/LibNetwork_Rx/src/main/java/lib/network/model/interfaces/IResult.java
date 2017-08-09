package lib.network.model.interfaces;

import lib.network.model.err.NetError;

/**
 * @author yuansui
 */
public interface IResult<T> {

    void setData(T data);

    T getData();

    boolean isSucceed();

    void setMessage(String message);

    String getMessage();

    void setCode(int code);

    int getCode();

    int getCodeOk();

    NetError getError();

    void setError(NetError err);
}
