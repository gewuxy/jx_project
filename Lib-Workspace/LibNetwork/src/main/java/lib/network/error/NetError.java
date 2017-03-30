package lib.network.error;

/**
 * @author yuansui
 */
public class NetError {
    // 错误提示文字
    private String mMessage;
    // 错误码
    private int mCode;
    // 实际错误
    private Exception mException;

    public NetError() {
        this(0, "");
    }

    public NetError(int code) {
        this(code, "");
    }

    public NetError(String msg) {
        this(-1, msg);
    }

    public NetError(int code, String msg) {
        this(code, msg, null);
    }

    public NetError(int code, String msg, Exception e) {
        mCode = code;
        mMessage = msg;
        mException = e;
    }

    public int getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }

    public Exception getException() {
        return mException;
    }

    @Override
    public String toString() {
        return "[message = " + mMessage + "], \r\n [code = " + mCode + "]";
    }
}
