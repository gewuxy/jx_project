package lib.ys.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import lib.ys.util.GenericUtil;


/**
 * 泛型通知者
 *
 * @param <I>
 */
abstract public class NotifierEx<I> {

    private List<I> mObservers = new ArrayList<>();

    public synchronized void add(I subscriber) {
        mObservers.add(subscriber);
    }

    public synchronized void remove(I subscriber) {
        mObservers.remove(subscriber);
    }

    /**
     * 发布通知-附带内容
     *
     * @param type
     * @param data
     */
    public synchronized void notify(final int type, final Object data) {
        I[] arrays = null;
        synchronized (this) {
            Class clz = GenericUtil.getClassType(getClass());
            arrays = (I[]) Array.newInstance(clz, mObservers.size());
            arrays = mObservers.toArray(arrays);
        }

        if (arrays != null) {
            Observable.fromArray(arrays)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(i -> callback(i, type, data));
        }
    }

    abstract protected void callback(I o, int type, Object data);

    /**
     * 发布通知-只有消息, 无内容
     *
     * @param type
     */
    public synchronized void notify(int type) {
        notify(type, null);
    }
}
