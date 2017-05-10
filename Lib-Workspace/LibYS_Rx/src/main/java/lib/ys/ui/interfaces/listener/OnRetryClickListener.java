package lib.ys.ui.interfaces.listener;

public interface OnRetryClickListener {
    /**
     * 点击了重试按钮
     *
     * @return false表示未被处理, 可继续传递往下传递; true表示已处理
     */
    boolean onRetryClick();
}
