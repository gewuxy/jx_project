package lib.ys.form;

import java8.lang.FunctionalInterface;

/**
 * form的观察者
 *
 * @author yuansui
 */
@FunctionalInterface
public interface OnFormObserver {
    void callback(Object... params);
}
