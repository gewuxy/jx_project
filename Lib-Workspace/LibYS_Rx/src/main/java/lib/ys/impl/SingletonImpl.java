package lib.ys.impl;

import java.util.Observable;

/**
 * 单例事件管理, 统一释放
 *
 * @author yuansui
 */
public class SingletonImpl extends Observable {

    private static SingletonImpl mInst;

    private SingletonImpl() {
    }

    public static SingletonImpl inst() {
        if (mInst == null) {
            mInst = new SingletonImpl();
        }
        return mInst;
    }

    public void freeAll() {
        setChanged();
        notifyObservers();
        deleteObservers();
    }
}
