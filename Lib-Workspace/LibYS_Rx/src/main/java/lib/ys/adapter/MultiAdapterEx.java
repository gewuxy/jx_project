package lib.ys.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java8.lang.FunctionalInterface;
import lib.ys.AppEx;
import lib.ys.LogMgr;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.adapter.interfaces.IViewHolder;
import lib.ys.fitter.DpFitter;
import lib.ys.fitter.LayoutFitter;
import lib.ys.interfaces.IFitParams;
import lib.ys.interfaces.IOption;
import lib.ys.util.GenericUtil;
import lib.ys.util.LaunchUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.view.ViewUtil;

/**
 * 复合样式Adapter
 *
 * @param <T>
 * @author yuansui
 */
abstract public class MultiAdapterEx<T, VH extends ViewHolderEx> extends BaseAdapter implements IFitParams, IOption, IAdapter<T> {

    protected final String TAG = getClass().getSimpleName();

    private List<T> mTs;
    private LayoutInflater mInflater;

    private Map<View, ViewClickListener> mMapClickLsn;
    private Map<CompoundButton, ViewCheckListener> mMapCheckLsn;
    private Map<TextView, ViewTextWatcher> mMapTextWatcher;
    private Map<View, KeeperVH> mMapVH;

    private OnAdapterClickListener mOnAdapterClickListener;

    private Class<VH> mVHClass;

    public MultiAdapterEx() {
        mVHClass = GenericUtil.getClassType(getClass(), IViewHolder.class);
        mMapVH = new HashMap<>();
    }

    @Override
    public int getCount() {
        return mTs == null ? 0 : mTs.size();
    }

    public int getLastItemPosition() {
        int count = getCount();
        return count == 0 ? 0 : count - 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public T getItem(int position) {
        if (mTs == null) {
            return null;
        }

        T t = null;
        try {
            t = mTs.get(position);
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        }

        return t;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        int itemType = getItemViewType(position);

        /**
         * 如果没有生成convertView或不使用缓存模式
         */
        if (convertView == null || !useCache()) {
            convertView = getLayoutInflater().inflate(getConvertViewResId(itemType), null);
            fit(convertView);

            VH holder = ReflectionUtil.newInst(mVHClass, convertView);
            convertView.setTag(holder);

            initView(position, holder, itemType);
        }

        setViewHolderKeeper(position, convertView, itemType);

        refreshView(position, (VH) convertView.getTag(), itemType);
        return convertView;
    }

    /**
     * 用于设置一次性属性, 比如图片的圆角处理, view的大小位置等
     *
     * @param position
     * @param holder
     * @param itemType
     */
    protected void initView(int position, VH holder, int itemType) {
    }

    abstract protected void refreshView(int position, VH holder, int itemType);

    abstract public int getConvertViewResId(int itemType);

    private void setViewHolderKeeper(int position, View convertView, int itemType) {
        KeeperVH keeper = mMapVH.get(convertView);
        if (keeper == null) {
            keeper = new KeeperVH(position, (VH) convertView.getTag(), itemType);
            mMapVH.put(convertView, keeper);
        } else {
            keeper.mPosition = position;
        }
    }

    /**
     * 根据view的id和item的position设置对应的lsn, 使用和convertView一样的缓存模式
     *
     * @param position
     * @param view
     */
    public void setOnViewClickListener(int position, View view) {
        if (view == null) {
            return;
        }

        if (mMapClickLsn == null) {
            mMapClickLsn = new HashMap<View, ViewClickListener>();
        }

        ViewClickListener lsn = mMapClickLsn.get(view);
        if (lsn == null) {
            // 表示这个view没有被设置过lsn
            lsn = new ViewClickListener(position);
            view.setOnClickListener(lsn);
            mMapClickLsn.put(view, lsn);
        } else {
            lsn.mPosition = position;
        }
    }

    /**
     * 去掉点击监听, 主要是为了不响应press效果
     *
     * @param view
     */
    protected void removeOnViewClickListener(View view) {
        if (mMapClickLsn == null) {
            return;
        }

        mMapClickLsn.remove(view);
        view.setOnClickListener(null);
        view.setClickable(false);
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    public void setData(List<T> data) {
        mTs = data;
    }

    public void add(T item) {
        if (item == null) {
            return;
        }

        if (mTs == null) {
            mTs = new ArrayList<>();
        }
        mTs.add(item);
    }

    public void add(int position, T item) {
        if (item == null) {
            return;
        }

        if (mTs == null) {
            mTs = new ArrayList<>();
        }
        mTs.add(position, item);
    }

    public void addAll(List<T> data) {
        if (data == null) {
            return;
        }

        if (mTs == null) {
            mTs = data;
        } else {
            mTs.addAll(data);
        }
    }

    public void addAll(int position, List<T> item) {
        if (mTs != null && item != null) {
            mTs.addAll(position, item);
        }
    }

    public List<T> getData() {
        if (mTs == null) {
            mTs = new ArrayList<>();
        }
        return mTs;
    }

    public void remove(T item) {
        if (mTs != null) {
            mTs.remove(item);
        }
    }

    public void remove(int position) {
        if (mTs != null) {
            mTs.remove(position);
        }
    }

    public void removeAll() {
        if (mTs != null) {
            mTs.clear();
        }
    }

    public Context getContext() {
        return AppEx.ct();
    }

    public LayoutInflater getLayoutInflater() {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(getContext());
        }
        return mInflater;
    }

    public void onDestroy() {
        if (mMapClickLsn != null) {
            mMapClickLsn.clear();
        }

        if (mMapCheckLsn != null) {
            mMapCheckLsn.clear();
        }

        if (mMapTextWatcher != null) {
            mMapTextWatcher.clear();
        }

        mMapVH.clear();
    }

    private class KeeperVH {
        private int mPosition;
        private int mItemType;
        private VH mHolder;

        public KeeperVH(int position, VH holder, int itemType) {
            mPosition = position;
            mItemType = itemType;
            mHolder = holder;
        }
    }

    // 内部查找
    @Nullable
    private ViewHolderEx getCacheViewHolder(int position, int itemType) {
        for (KeeperVH keeper : mMapVH.values()) {
            if (keeper.mPosition == position && keeper.mItemType == itemType) {
                return keeper.mHolder;
            }
        }
        return null;
    }

    /**
     * 通过位置查找viewHolder
     *
     * @param position
     * @return 没有则返回null
     */
    @Nullable
    public final ViewHolderEx getCacheViewHolder(int position) {
        return getCacheViewHolder(position, getItemViewType(position));
    }

    /**
     * 处理view的click消息
     *
     * @author yuansui
     */
    private class ViewClickListener implements OnClickListener {

        private int mPosition;

        public ViewClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            onViewClick(mPosition, v);

            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener.onAdapterClick(mPosition, v);
            }
        }
    }

    /*************************************************
     * ViewCheckListener相关-start
     */

    /**
     * 处理Btn的Check消息
     *
     * @author yuansui
     */
    private class ViewCheckListener implements OnCheckedChangeListener {

        private int mPosition;
        private OnCompoundBtnCheckListener mBtnCheckListener;

        public ViewCheckListener(int position, OnCompoundBtnCheckListener listener) {
            mPosition = position;
            mBtnCheckListener = listener;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mBtnCheckListener != null) {
                mBtnCheckListener.onCompoundBtnCheck(mPosition, buttonView, isChecked);
            }
        }
    }

    public interface OnCompoundBtnCheckListener {
        public void onCompoundBtnCheck(int position, CompoundButton btn, boolean isChecked);
    }

    /**
     * 根据view的id和item的position设置对应的lsn, 使用和convertView一样的缓存模式
     *
     * @param position
     * @param btn
     * @param listener
     */
    protected void setOnCompoundBtnCheckListener(int position, CompoundButton btn, OnCompoundBtnCheckListener listener) {
        if (btn == null || listener == null) {
            return;
        }

        if (mMapCheckLsn == null) {
            mMapCheckLsn = new HashMap<CompoundButton, ViewCheckListener>();
        }

        ViewCheckListener lsn = mMapCheckLsn.get(btn);
        if (lsn == null) {
            // 表示这个btn没有被设置过lsn
            lsn = new ViewCheckListener(position, listener);
            btn.setOnCheckedChangeListener(lsn);
            mMapCheckLsn.put(btn, lsn);
        } else {
            lsn.mPosition = position;
        }
    }

    /*************************************************
     * ViewCheckListener相关-end
     */

    /**
     * 供外部响应click消息而使用的listener
     *
     * @param listener
     */
    public final void setOnAdapterClickListener(OnAdapterClickListener listener) {
        mOnAdapterClickListener = listener;
    }

    /**********************************************
     * ViewTextWatcher相关-start
     */
    private class ViewTextWatcher implements TextWatcher {
        private int mPosition;
        private OnViewTextWatcher mWatcher = null;

        public ViewTextWatcher(int position, OnViewTextWatcher watcher) {
            mPosition = position;
            mWatcher = watcher;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mWatcher != null) {
                mWatcher.onTextChanged(mPosition, s);
            }
        }
    }

    public interface OnViewTextWatcher {
        void onTextChanged(int position, Editable s);
    }

    /**
     * 根据view的id和item的position设置对应的TextWatcher, 使用和convertView一样的缓存模式
     *
     * @param position
     * @param tv
     * @param onViewTextWatcher
     */
    protected void setViewTextWatcher(int position, TextView tv, OnViewTextWatcher onViewTextWatcher) {
        if (tv == null) {
            return;
        }

        if (mMapTextWatcher == null) {
            mMapTextWatcher = new HashMap<TextView, ViewTextWatcher>();
        }

        ViewTextWatcher watcher = mMapTextWatcher.get(tv);
        if (watcher == null) {
            // 表示这个view没有被设置过
            watcher = new ViewTextWatcher(position, onViewTextWatcher);
            tv.addTextChangedListener(watcher);
            mMapTextWatcher.put(tv, watcher);
        } else {
            watcher.mPosition = position;
        }
    }

    /**********************************************
     * ViewTextWatcher相关-end
     */

    @Override
    public void fitAbsParamsPx(View v, int x, int y) {
        LayoutFitter.fitAbsParamsPx(v, x, y);
    }

    @Override
    public int fitDp(float dp) {
        return DpFitter.dp(dp);
    }

    @Override
    public void fit(View v) {
        LayoutFitter.fit(v);
    }

    @Override
    public void showView(View v) {
        ViewUtil.showView(v);
    }

    @Override
    public void hideView(View v) {
        ViewUtil.hideView(v);
    }

    @Override
    public void goneView(View v) {
        ViewUtil.goneView(v);
    }

    @Override
    public void showToast(String content) {
        AppEx.showToast(content);
    }

    @Override
    public void showToast(@StringRes int... resId) {
        AppEx.showToast(resId);
    }

    @Override
    public void startActivity(Class<?> clz) {
        LaunchUtil.startActivity(getContext(), clz);
    }

    @Override
    public void startActivity(Intent intent) {
        LaunchUtil.startActivity(getContext(), intent);
    }

    @Override
    public void startActivityForResult(Class<?> clz, int requestCode) {
        // 空实现
    }

    /**
     * 监听控件的点击, 可以不重写, 交给外部处理
     *
     * @param position
     * @param v
     * @see #setOnAdapterClickListener(OnAdapterClickListener)
     */
    protected void onViewClick(int position, View v) {
    }

    /**
     * 是否在getView()的时候使用缓存机制
     *
     * @return
     */
    protected boolean useCache() {
        return true;
    }

    @Override
    abstract public int getItemViewType(int position);

    @Override
    abstract public int getViewTypeCount();

    @Override
    public void registerDataSetObserver(Object observer) {
        super.registerDataSetObserver((DataSetObserver) observer);
    }

    @Override
    public void unregisterDataSetObserver(Object observer) {
        super.unregisterDataSetObserver((DataSetObserver) observer);
    }

    @FunctionalInterface
    public interface OnAdapterClickListener {
        void onAdapterClick(int position, View v);
    }
}
