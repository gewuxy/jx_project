package lib.network.model.interfaces;

/**
 * @author yuansui
 */
public interface IResult<T> {

    void setData(T data);

    T getData();

    boolean isSucceed();

    void setError(String error);

    String getError();

    void setCode(int code);

    int getCode();

    int getCodeOk();
}
