package lib.ys.adapter.recycler;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import lib.ys.AppEx;
import lib.ys.YSLog;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.VH.RecyclerViewHolderEx;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.adapter.interfaces.IViewHolder;
import lib.ys.fitter.DpFitter;
import lib.ys.fitter.LayoutFitter;
import lib.ys.ui.interfaces.opt.ICommonOpt;
import lib.ys.ui.interfaces.opt.IFitOpt;
import lib.ys.util.GenericUtil;
import lib.ys.util.LaunchUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.view.ViewUtil;

/**
 * @author yuansui
 */
abstract public class MultiRecyclerAdapterEx<T, VH extends RecyclerViewHolderEx>
        extends Adapter<VH>
        implements IAdapter<T>, IFitOpt, ICommonOpt {

    protected final String TAG = getClass().getSimpleName();

    private List<T> mTs;
    private LayoutInflater mInflater = null;

    private OnRecyclerItemClickListener mItemClickListener;
    private boolean mEnableLongClick;

    private HashSet<View> mSetInit;
    private HashMap<View, ViewClickListener> mMapClickLsn = null;
    private OnAdapterClickListener mOnAdapterClickListener;

    private Map<VH, KeeperVH> mMapVH;

    private Class<VH> mVHClass;


    public MultiRecyclerAdapterEx() {
        mVHClass = GenericUtil.getClassType(getClass(), IViewHolder.class);
        if (mVHClass == null) {
            throw new IllegalStateException("can not find view holder");
        }

        mMapVH = new HashMap<>();
        mSetInit = new HashSet<>();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = getLayoutInflater().inflate(getConvertViewResId(viewType), parent, false);
        fit(v);

        final VH holder = ReflectionUtil.newInst(mVHClass, v);


        return holder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        View v = holder.getConvertView();

        if (!mSetInit.contains(v)) {
            mSetInit.add(v);

            initView(position, holder, getItemViewType(position));

            v.setOnClickListener(v1 -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, holder.getLayoutPosition());
                }
            });

            v.setOnLongClickListener(v1 -> {
                if (mEnableLongClick) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemLongClick(v1, holder.getLayoutPosition());
                    }
                    return true;
                }
                return false;
            });
        }

        setViewHolderKeeper(position, holder, getItemViewType(position));
        refreshView(position, holder, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    /*********************************
     * 以上是原生方法
     *******************************/

    /**
     * 设置点击监听
     *
     * @param listener
     */
    public final void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * 是否允许长点击
     *
     * @param enable
     */
    public final void setEnableLongClick(boolean enable) {
        mEnableLongClick = enable;
    }

    public LayoutInflater getLayoutInflater() {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(getContext());
        }
        return mInflater;
    }

    public Context getContext() {
        return AppEx.ct();
    }

    /**
     * @param position
     * @param holder
     * @param itemType
     */
    protected void initView(int position, VH holder, int itemType) {
    }

    abstract protected void refreshView(int position, VH holder, int itemType);

    abstract public int getConvertViewResId(int itemType);

    @Override
    public void registerDataSetObserver(Object observer) {
        registerAdapterDataObserver((AdapterDataObserver) observer);
    }

    @Override
    public void unregisterDataSetObserver(Object observer) {
        unregisterAdapterDataObserver((AdapterDataObserver) observer);
    }

    @Override
    public void setOnAdapterClickListener(OnAdapterClickListener listener) {
        mOnAdapterClickListener = listener;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
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
            YSLog.e(TAG, e);
        }

        return t;
    }

    @Override
    public int getLastItemPosition() {
        return 0;
    }

    @Override
    public int getCount() {
        return mTs == null ? 0 : mTs.size();
    }

    @Override
    public List<T> getData() {
        return mTs;
    }

    @Override
    public void removeAll() {
        if (mTs != null) {
            mTs.clear();
        }
    }

    @Override
    public void remove(int position) {
        if (mTs != null) {
            mTs.remove(position);
        }
    }

    @Override
    public void remove(T item) {
        if (mTs != null) {
            mTs.remove(item);
        }
    }

    @Override
    public void addAll(int position, List<T> item) {
        if (mTs != null && item != null) {
            mTs.addAll(position, item);
        }
    }

    @Override
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

    @Override
    public void add(T item) {
        if (item == null) {
            return;
        }

        if (mTs == null) {
            mTs = new ArrayList<T>();
        }
        mTs.add(item);
    }

    @Override
    public void add(int position, T item) {
        if (item == null) {
            return;
        }

        if (mTs == null) {
            mTs = new ArrayList<T>();
        }
        mTs.add(position, item);
    }

    @Override
    public void setData(List<T> data) {
        mTs = data;
    }

    @Override
    public void fit(View v) {
        LayoutFitter.fit(v);
    }

    @Override
    public void fitAbsByPx(View v, int x, int y) {
        LayoutFitter.fitAbsByPx(v, x, y);
    }

    @Override
    public int fitDp(float dp) {
        return DpFitter.dp(dp);
    }

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

    @Override
    public void showToast(String content) {
        AppEx.showToast(content);
    }

    @Override
    public void showToast(@StringRes int... resId) {
        AppEx.showToast(resId);
    }

    public class KeeperVH {
        private int mPosition;
        private int mItemType;
        private VH mHolder;

        public KeeperVH(int position, VH holder, int itemType) {
            mPosition = position;
            mItemType = itemType;
            mHolder = holder;
        }
    }

    private void setViewHolderKeeper(int position, VH holder, int itemType) {
        KeeperVH keeper = mMapVH.get(holder);
        if (keeper == null) {
            keeper = new KeeperVH(position, holder, itemType);
            mMapVH.put(holder, keeper);
        } else {
            keeper.mPosition = position;
        }
    }

    // 内部查找
    @Nullable
    private VH getCacheVH(int position, int itemType) {
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
    public final VH getCacheVH(int position) {
        return getCacheVH(position, getItemViewType(position));
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        super.onViewDetachedFromWindow(holder);

        mSetInit.clear();
        mMapClickLsn.clear();
    }
}
