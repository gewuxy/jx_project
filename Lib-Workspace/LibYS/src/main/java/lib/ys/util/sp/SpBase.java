package lib.ys.util.sp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

import lib.ys.inst.SingletonEvent;
import lib.ys.interfaces.ISingleton;
import lib.ys.model.EVal;

@SuppressWarnings({"rawtypes"})
abstract public class SpBase implements ISingleton {

    protected String TAG = getClass().getSimpleName();

    protected SharedPreferences mSp = null;

    public SpBase(Context context, String fileName) {
        mSp = context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        SingletonEvent.inst().addObserver(this);
    }

    public synchronized boolean save(String key, Object value) {
        return SpUtil.save(mSp, key, value);
    }

    public boolean contains(String key) {
        return SpUtil.contains(mSp, key);
    }

    public Integer getInt(String key) {
        return SpUtil.getInt(mSp, key);
    }

    public Integer getInt(String key, int defValue) {
        return SpUtil.getInt(mSp, key, defValue);
    }

    /**
     * Get Long Value
     */
    public long getLong(String key) {
        return SpUtil.getLong(mSp, key);
    }

    public long getLong(String key, long defaultValue) {
        return SpUtil.getLong(mSp, key, defaultValue);
    }

    /**
     * Get String Value
     */
    public String getString(String key) {
        return SpUtil.getString(mSp, key);
    }

    public String getString(String key, String defaultValue) {
        return SpUtil.getString(mSp, key, defaultValue);
    }

    /**
     * get boolean value
     */
    public Boolean getBoolean(String key) {
        return SpUtil.getBoolean(mSp, key);
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        return SpUtil.getBoolean(mSp, key, defaultValue);
    }

    /**
     * clear all values
     */
    public boolean clear() {
        return SpUtil.clear(mSp);
    }

    public boolean remove(String key) {
        return SpUtil.remove(mSp, key);
    }

    public boolean removeKeys(String... keys) {
        return SpUtil.removeKeys(mSp, keys);
    }

    /**
     * @param key
     * @return 没有值的时候返回null
     */
    public Serializable getSerializable(String key) {
        return SpUtil.getSerializable(mSp, key);
    }

    /**
     * 保存EnumsValue不需要传key, key是自动生成的
     *
     * @param value
     */
    public <T extends EVal> boolean save(T value) {
        return save(null, value);
    }

    public <T extends EVal> boolean save(String key, T value) {
        return SpUtil.save(mSp, key, value);
    }

    /**
     * 读取保存的EnumsValue子类数据
     *
     * @param clz
     * @return
     */
    public <T extends EVal> T getEV(Class<T> clz) {
        return SpUtil.getEV(mSp, null, clz);
    }

    /**
     * 读取保存的EnumsValue子类数据
     *
     * @param key
     * @param clz
     * @return
     */
    public <T extends EVal> T getEV(String key, Class<T> clz) {
        return SpUtil.getEV(mSp, key, clz);
    }

    /**
     * 判断EnumsValue是否存在
     *
     * @param clz
     * @return
     */
    public <T extends EVal> boolean isEVExist(Class<T> clz) {
        return SpUtil.isEVExist(mSp, null, clz);
    }

    /**
     * 判断EnumsValue是否存在
     *
     * @param clz
     * @return
     */
    public <T extends EVal> boolean isEVExist(String key, Class<T> clz) {
        return SpUtil.isEVExist(mSp, key, clz);
    }
}
