package lib.ys.decor;

/**
 * 页面加载统一使用的progress view接口, 包括dialog和embed形式的
 *
 * @author yuansui
 */
public interface IDecorProgressView {
    /**
     * 开始progress动画
     */
    void start();

    /**
     * 停止progress动画
     */
    void stop();
}
