package lib.ys.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java8.lang.FunctionalInterface;
import lib.ys.AppEx;
import lib.ys.LogMgr;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.adapter.interfaces.IViewHolder;
import lib.ys.fitter.DpFitter;
import lib.ys.fitter.LayoutFitter;
import lib.ys.interfaces.IFitParams;
import lib.ys.interfaces.IOption;
import lib.ys.util.GenericUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.view.ViewUtil;

abstract public class MultiGroupAdapterEx<T, VH extends ViewHolderEx> extends BaseExpandableListAdapter implements IFitParams, IOption, IGroupAdapter<T> {

    protected final String TAG = getClass().getSimpleName();

    private List<T> mTs;
    private LayoutInflater mInflater = null;

    private Map<View, GroupViewClickListener> mMapGroupClickLsn;
    private Map<View, ChildViewClickListener> mMapChildClickLsn;

    private Map<View, GroupKeeperVH> mMapGroupVH;
    private Map<View, ChildKeeperVH> mMapChildVH;

    private OnGroupAdapterClickListener mGroupAdapterClickListener;
    private OnChildAdapterClickListener mChildAdapterClickListener;

    private Class<VH> mVHClass;

    public MultiGroupAdapterEx() {
        mVHClass = GenericUtil.getClassType(getClass(), IViewHolder.class);

        mMapGroupVH = new HashMap<>();
        mMapChildVH = new HashMap<>();
    }

    @Override
    public void setData(List<T> data) {
        mTs = data;
    }

    @Override
    public List<T> getData() {
        return mTs;
    }

    @Override
    public void add(T groupItem) {
        if (groupItem == null) {
            return;
        }

        if (mTs == null) {
            mTs = new ArrayList<T>();
        }
        mTs.add(groupItem);
    }

    @Override
    public void addAll(List<T> groups) {
        if (groups == null) {
            return;
        }

        if (mTs == null) {
            mTs = groups;
        } else {
            mTs.addAll(groups);
        }
    }

    @Override
    public void addAll(int position, List<T> item) {
        if (mTs != null && item != null) {
            mTs.addAll(position, item);
        }
    }

    /**
     * group item part
     **/

    public boolean isGroupEmpty() {
        return getGroupCount() == 0;
    }

    @Override
    public int getGroupCount() {
        return mTs == null ? 0 : mTs.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public T getGroup(int groupPosition) {
        if (mTs == null) {
            return null;
        }

        T t = null;
        try {
            t = mTs.get(groupPosition);
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        }

        return t;
    }

    /**
     * child item part
     **/

    public boolean isChildrenEmpty(int groupPosition) {
        return getChildrenCount(groupPosition) == 0;
    }

    @Override
    public abstract int getChildrenCount(int groupPosition);

    @Override
    public abstract long getChildId(int groupPosition, int childPosition);

    @Override
    public abstract Object getChild(int groupPosition, int childPosition);

    /**
     * item attr part
     */

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * group view create and refresh
     */

    @Override
    public final View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        int groupType = getGroupType(groupPosition);

        if (convertView == null) {
            convertView = getLayoutInflater().inflate(getGroupConvertViewResId(groupType), null);
            fit(convertView);

            VH holder = ReflectionUtil.newInst(mVHClass, convertView);
            convertView.setTag(holder);

            initGroupView(groupPosition, isExpanded, holder, groupType);
        }

        setGroupViewHolderKeeper(groupPosition, convertView, groupType);

        refreshGroupView(groupPosition, isExpanded, (VH) convertView.getTag(), groupType);
        return convertView;
    }

    abstract public int getGroupConvertViewResId(int groupType);

    protected void initGroupView(int groupPosition, boolean isExpanded, VH holder, int groupType) {
    }

    abstract protected void refreshGroupView(int groupPosition, boolean isExpanded, VH holder, int groupType);

    /**
     * child view create and refresh
     */

    @Override
    public final View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        int childType = getChildType(groupPosition, childPosition);

        if (convertView == null) {
            convertView = getLayoutInflater().inflate(getChildConvertViewResId(childType), null);
            fit(convertView);

            VH holder = ReflectionUtil.newInst(mVHClass, convertView);
            convertView.setTag(holder);

            initChildView(groupPosition, childPosition, isLastChild, holder, childType);
        }

        setChildViewHolderKeeper(groupPosition, childPosition, convertView, childType);

        refreshChildView(groupPosition, childPosition, isLastChild, (VH) convertView.getTag(), childType);
        return convertView;
    }

    abstract public int getChildConvertViewResId(int childType);

    protected void initChildView(int groupPosition, int childPosition, boolean isLastChild, VH holder, int childType) {
    }

    abstract protected void refreshChildView(int groupPosition, int childPosition, boolean isLastChild, VH holder, int childType);

    /**
     * 根据view的id和group item的position设置对应的lsn
     *
     * @param groupPosition
     * @param v
     */
    protected void setOnGroupViewClickListener(int groupPosition, View v) {
        if (v == null) {
            return;
        }

        if (mMapGroupClickLsn == null) {
            mMapGroupClickLsn = new HashMap<View, GroupViewClickListener>();
        }

        GroupViewClickListener lsn = mMapGroupClickLsn.get(v);
        if (lsn == null) {
            // 表示这个view没有被设置过lsn
            lsn = new GroupViewClickListener(groupPosition);
            v.setOnClickListener(lsn);
            mMapGroupClickLsn.put(v, lsn);
        } else {
            lsn.mGroupPosition = groupPosition;
        }
    }

    /**
     * 去掉group里的view的点击监听
     *
     * @param v
     */
    protected void removeOnGroupViewClickListener(View v) {
        if (mMapGroupClickLsn == null) {
            return;
        }

        mMapGroupClickLsn.remove(v);
        v.setOnClickListener(null);
        v.setClickable(false);
    }

    /**
     * 根据view的id和child item的position设置对应的lsn
     *
     * @param groupPosition
     * @param childPosition
     * @param v
     */
    public void setOnChildViewClickListener(int groupPosition, int childPosition, View v) {
        if (v == null) {
            return;
        }

        if (mMapChildClickLsn == null) {
            mMapChildClickLsn = new HashMap<View, ChildViewClickListener>();
        }

        ChildViewClickListener lsn = mMapChildClickLsn.get(v);
        if (lsn == null) {
            // 表示这个view没有被设置过lsn
            lsn = new ChildViewClickListener(groupPosition, childPosition);
            v.setOnClickListener(lsn);
            mMapChildClickLsn.put(v, lsn);
        } else {
            lsn.mGroupPosition = groupPosition;
            lsn.mChildPosition = childPosition;
        }
    }

    /**
     * 去掉点击监听, 主要是为了不响应press效果
     *
     * @param v
     */
    protected void removeOnChildViewClickListener(View v) {
        if (mMapChildClickLsn == null) {
            return;
        }

        mMapChildClickLsn.remove(v);
        v.setOnClickListener(null);
        v.setClickable(false);
    }

    public LayoutInflater getLayoutInflater() {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(AppEx.ct());
        }
        return mInflater;
    }

    public void onDestroy() {
        if (mMapChildClickLsn != null) {
            mMapChildClickLsn.clear();
        }
    }

    /**
     * 点击child的view lsn
     */
    private class ChildViewClickListener implements OnClickListener {
        private int mGroupPosition;
        private int mChildPosition;

        public ChildViewClickListener(int groupPosition, int childPosition) {
            mGroupPosition = groupPosition;
            mChildPosition = childPosition;
        }

        @Override
        public void onClick(View v) {
            onChildViewClick(mGroupPosition, mChildPosition, v);

            if (mChildAdapterClickListener != null) {
                mChildAdapterClickListener.onChildAdapterClick(mGroupPosition, mChildPosition, v);
            }
        }
    }

    /**
     * 点击group的view lsn
     */
    private class GroupViewClickListener implements OnClickListener {

        private int mGroupPosition;

        public GroupViewClickListener(int groupPosition) {
            mGroupPosition = groupPosition;
        }

        @Override
        public void onClick(View v) {
            onGroupViewClick(mGroupPosition, v);

            if (mGroupAdapterClickListener != null) {
                mGroupAdapterClickListener.onGroupAdapterClick(mGroupPosition, v);
            }
        }
    }

    public Context getContext() {
        return AppEx.ct();
    }

    /********************************
     * 适配相关
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
    public void startActivity(Class<?> clz) {
        Intent intent = new Intent(AppEx.ct(), clz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppEx.ct().startActivity(intent);
    }

    @Override
    public void startActivityForResult(Class<?> clz, int requestCode) {
        // 空实现
    }

    @Override
    public void startActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppEx.ct().startActivity(intent);
    }

    @Override
    public void showToast(String content) {
        AppEx.showToast(content);
    }

    @Override
    public void showToast(@StringRes int... resId) {
        AppEx.showToast(resId);
    }

    protected int getLastGroupPosition() {
        int count = getGroupCount();
        return count == 0 ? 0 : count - 1;
    }

    protected int getLastChlidPosition(int groupPosition) {
        int count = getChildrenCount(groupPosition);
        return count == 0 ? 0 : count - 1;
    }

    /**
     * 监听child的点击事件
     *
     * @param groupPosition
     * @param childPosition
     * @param v
     */
    public void onChildViewClick(int groupPosition, int childPosition, View v) {
    }

    /**
     * 监听group点击事件
     *
     * @param groupPosition
     * @param v
     */
    public void onGroupViewClick(int groupPosition, View v) {
    }

    @FunctionalInterface
    public interface OnGroupAdapterClickListener {
        void onGroupAdapterClick(int groupPosition, View v);
    }

    @FunctionalInterface
    public interface OnChildAdapterClickListener {
        void onChildAdapterClick(int groupPosition, int childPosition, View v);
    }

    @Override
    public void setOnGroupAdapterClickListener(OnGroupAdapterClickListener listener) {
        mGroupAdapterClickListener = listener;
    }

    @Override
    public void setOnChildAdapterClickListener(OnChildAdapterClickListener listener) {
        mChildAdapterClickListener = listener;
    }

    @Override
    abstract public int getGroupType(int groupPosition);

    @Override
    abstract public int getGroupTypeCount();

    @Override
    abstract public int getChildType(int groupPosition, int childPosition);

    @Override
    abstract public int getChildTypeCount();

    private class GroupKeeperVH {
        private int mPosition;
        private int mItemType;
        private VH mViewHolder;

        public GroupKeeperVH(int position, VH holder, int itemType) {
            mPosition = position;
            mItemType = itemType;
            mViewHolder = holder;
        }
    }

    private class ChildKeeperVH {
        private int mGroupPosition;
        private int mChildPosition;
        private int mItemType;
        private VH mViewHolder;

        public ChildKeeperVH(int groupPosition, int childPosition, VH holder, int itemType) {
            mGroupPosition = groupPosition;
            mChildPosition = childPosition;
            mItemType = itemType;
            mViewHolder = holder;
        }
    }

    /**
     * 内部寻找group的cache
     *
     * @param position
     * @param itemType
     * @return
     */
    private ViewHolderEx getGroupCacheViewHolder(int position, int itemType) {
        for (GroupKeeperVH keeper : mMapGroupVH.values()) {
            if (keeper.mPosition == position && keeper.mItemType == itemType) {
                return keeper.mViewHolder;
            }
        }
        return null;
    }

    /**
     * 通过位置查找group viewHolder
     *
     * @param groupPosition
     * @return 没有则返回null
     */
    @Override
    @Nullable
    public final ViewHolderEx getGroupCacheViewHolder(int groupPosition) {
        return getGroupCacheViewHolder(groupPosition, getGroupType(groupPosition));
    }

    private void setGroupViewHolderKeeper(int groupPosition, View convertView, int groupType) {
        GroupKeeperVH keeper = mMapGroupVH.get(convertView);
        if (keeper == null) {
            keeper = new GroupKeeperVH(groupPosition, (VH) convertView.getTag(), groupType);
            mMapGroupVH.put(convertView, keeper);
        } else {
            keeper.mPosition = groupPosition;
        }
    }

    /**
     * 内部寻找group的cache
     *
     * @param groupPosition
     * @param childPosition
     * @param itemType
     * @return
     */
    @Nullable
    private ViewHolderEx getChildCacheViewHolder(int groupPosition, int childPosition, int itemType) {
        Iterator<Entry<View, ChildKeeperVH>> iter = mMapChildVH.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<View, ChildKeeperVH> entry = iter.next();
            // Object key = entry.getKey();
            ChildKeeperVH keeper = entry.getValue();
            if (keeper.mGroupPosition == groupPosition //
                    && keeper.mChildPosition == childPosition //
                    && keeper.mItemType == itemType) {
                return keeper.mViewHolder;
            }
        }
        return null;
    }

    /**
     * 通过位置查找child viewHolder
     *
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Nullable
    public final ViewHolderEx getChildCacheViewHolder(int groupPosition, int childPosition) {
        return getChildCacheViewHolder(groupPosition, childPosition, getChildType(groupPosition, childPosition));
    }

    private void setChildViewHolderKeeper(int groupPosition, int childPosition, View convertView, int groupType) {
        ChildKeeperVH keeper = mMapChildVH.get(convertView);
        if (keeper == null) {
            keeper = new ChildKeeperVH(groupPosition, childPosition, (VH) convertView.getTag(), groupType);
            mMapChildVH.put(convertView, keeper);
        } else {
            keeper.mGroupPosition = groupPosition;
            keeper.mChildPosition = childPosition;
        }
    }

    @Override
    public void registerDataSetObserver(Object observer) {
        super.registerDataSetObserver((DataSetObserver) observer);
    }

    @Override
    public void unregisterDataSetObserver(Object observer) {
        super.unregisterDataSetObserver((DataSetObserver) observer);
    }

    /*************************************************************************
     * 以下是Adapter的方法, 无用
     */
    @Override
    @Deprecated
    public final void setOnAdapterClickListener(OnAdapterClickListener listener) {
    }

    @Override
    @Deprecated
    public final T getItem(int position) {
        return null;
    }

    @Override
    @Deprecated
    public final int getLastItemPosition() {
        return 0;
    }

    @Override
    @Deprecated
    public final int getCount() {
        return 0;
    }

    @Override
    @Deprecated
    public final void removeAll() {
    }

    @Override
    @Deprecated
    public final void remove(int position) {
    }

    @Override
    @Deprecated
    public final void remove(T item) {
    }

    @Override
    @Deprecated
    public final void add(int position, T item) {
    }
}
