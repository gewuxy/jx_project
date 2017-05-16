package lib.ys.network.result;

/**
 * @author yuansui
 */
public interface IResponse<T> {

    void setData(T data);

    T getData();

    boolean isSucceed();

    void setError(String error);

    String getError();

    void setCode(int code);

    int getCode();

    int getCodeOk();
}
