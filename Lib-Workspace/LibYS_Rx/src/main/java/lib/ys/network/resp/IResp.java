package lib.ys.network.resp;

/**
 * @author yuansui
 */
public interface IResp<T> {

    void setData(T data);

    T getData();

    boolean isSucceed();

    void setError(String error);

    String getError();

    void setCode(int code);

    int getCode();

    int getCodeOk();
}
