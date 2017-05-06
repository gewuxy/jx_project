package lib.ys.util;

import android.view.View;

import java.lang.reflect.Field;

import lib.ys.LogMgr;
import lib.ys.model.inject.ViewId;


/**
 * 现阶段暂时不推荐大量使用注解框架, 反射效率低的问题没有解决
 *
 * @author yuansui
 * @deprecated
 */
public class InjectUtil {

    public interface IInjectView {
        <T extends View> T findView(int id);
    }

    private static void injectViews(IInjectView host, Class clz) {
        Field[] fields = clz.getDeclaredFields();

        if (IInjectView.class.isAssignableFrom(clz.getSuperclass())) {
            injectViews(host, clz.getSuperclass());
        }

        // 遍历所有成员变量
        for (Field f : fields) {
            if (!f.isAnnotationPresent(ViewId.class)) {
                continue;
            }

            ViewId annotation = f.getAnnotation(ViewId.class);
            int id = annotation.value();

            if (id != View.NO_ID) {
                // 初始化View
                View v = host.findView(id);
                if (v == null) {
                    continue;
                }

                try {
                    f.setAccessible(true);
                    f.set(host, v);
                } catch (Exception e) {
                    LogMgr.e(clz.getSimpleName(), "injectViews", e);
                }
            }
        }
    }

    public static void injectViews(IInjectView host) {
        Class<?> clz = host.getClass();
        injectViews(host, clz);
    }
}
