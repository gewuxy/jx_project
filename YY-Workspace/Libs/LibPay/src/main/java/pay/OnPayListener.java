package pay;

/**
 * @auther Huoxuyu
 * @since 2017/10/11
 */

public interface OnPayListener {
    void onPaySuccess();
    void onPayError(String error);
}
