package lib.network.model.err;

/**
 * @author yuansui
 */
public class ParseError extends NetError {

    public ParseError() {
        super();
    }

    public ParseError(String msg) {
        super(msg);
    }

    public ParseError(int code, String msg) {
        super(code, msg);
    }

    public ParseError(int code, String msg, Exception e) {
        super(code, msg, e);
    }
}
