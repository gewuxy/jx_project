package lib.ys.form;

/**
 * form的观察者
 *
 * @author yuansui
 */
@FunctionalInterface
public interface OnFormObserver {
    void callback(Object... params);
}
