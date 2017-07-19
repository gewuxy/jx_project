package lib.ys.ui.frag.form;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lib.ys.ConstantsEx;
import lib.ys.R;
import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.adapter.interfaces.IViewHolder;
import lib.ys.form.OnFormViewClickListener;
import lib.ys.form.group.ChildFormEx;
import lib.ys.form.group.GroupFormEx;
import lib.ys.form.group.OnGroupFormViewClickListener;
import lib.ys.ui.frag.FragEx;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.view.LayoutUtil;

/**
 * TODO: 还没有改造完
 *
 * @param <T>
 */
abstract public class GroupFormFragEx<T extends GroupFormEx> extends FragEx implements OnFormViewClickListener, OnGroupFormViewClickListener {

    private List<T> mItems;

    private LinearLayout mLayoutHeader;
    private LinearLayout mLayoutItems;
    private LinearLayout mLayoutFooter;
    private ScrollView mScrollView;

    private HashMap<View, Integer> mMapGroupClick;
    private HashMap<Object/* key */, T> mMapGroupRelated;
    private HashMap<T, Integer> mMapGroupResId;

    private HashMap<View, ChildFormEx> mMapChildClick;
    private HashMap<ChildFormEx, Integer> mMapChildResId;
    private HashMap<Serializable/* key */, ChildFormEx> mMapChildConfig;

    private HashMap<Integer, View> mMapChildBindGroup;

    private ItemClickListener mListener;


    @Override
    public int getContentViewId() {
        return R.layout.layout_form_items;
    }

    @CallSuper
    @Override
    public void initData() {
        mMapGroupClick = new HashMap<>();

        // group
        mItems = new ArrayList<>();
        mMapGroupResId = new HashMap<>();
        mMapGroupRelated = new HashMap<>();

        // child
        mMapChildClick = new HashMap<>();
        mMapChildResId = new HashMap<>();
        mMapChildConfig = new HashMap<>();

        mMapChildBindGroup = new HashMap<>();

        mListener = new ItemClickListener();
    }

    @CallSuper
    @Override
    public void findViews() {
        mLayoutHeader = findView(R.id.base_form_layout_header);
        mLayoutItems = findView(R.id.base_form_layout_items);
        mLayoutFooter = findView(R.id.base_form_layout_footer);
        mScrollView = findView(R.id.base_form_scroll_view);

        View header = getHeaderView();
        if (header != null) {
            mLayoutHeader.addView(header, LayoutUtil.getLinearParams(MATCH_PARENT, WRAP_CONTENT));
        }

        View footer = getFooterView();
        if (footer != null) {
            mLayoutFooter.addView(footer, LayoutUtil.getLinearParams(MATCH_PARENT, WRAP_CONTENT));
        }
    }

    @CallSuper
    @Override
    public void setViews() {
        /**
         * 为了下标
         */
        for (int i = 0; i < mItems.size(); ++i) {
            // 创建一个放置group和child的root
            LinearLayout rootLayout = new LinearLayout(getActivity());
            rootLayout.setOrientation(LinearLayout.VERTICAL);
            mLayoutItems.addView(rootLayout, LayoutUtil.getLinearParams(MATCH_PARENT, WRAP_CONTENT));

            T groupItem = getGroupItem(i);

            // 添加group layout
            if (!addGroupItemView(groupItem, rootLayout, i)) {
                // 继续添加下一组数据
                continue;
            }

            // 添加child layouts
            List<ChildFormEx> childItems = groupItem.getChildItems();
            if (childItems != null && !childItems.isEmpty()) {
                // 创建一个linearLayout专门放置child layouts
                LinearLayout childLayout = new LinearLayout(getActivity());
                childLayout.setOrientation(LinearLayout.VERTICAL);
                rootLayout.addView(childLayout, LayoutUtil.getLinearParams(MATCH_PARENT, WRAP_CONTENT));
                mMapChildBindGroup.put(i, childLayout);

                for (int j = 0; j < childItems.size(); ++j) {
                    ChildFormEx childItem = childItems.get(j);
                    addChildItemView(childItem, childLayout, i, j);
                }
                if (initCollapseAllGroup()) {
                    goneView(childLayout);
                }
            }
        }
    }

    /**
     * 初始化group view
     *
     * @param item
     * @param groupPosition
     * @return 成功则返回v, 失败返回null
     */
    private boolean addGroupItemView(T item, ViewGroup root, int groupPosition) {
        View v = getLayoutInflater().inflate(mMapGroupResId.get(item), null);
        if (v == null) {
            return false;
        }
        root.addView(v, LayoutUtil.getLinearParams(MATCH_PARENT, WRAP_CONTENT));

        ViewHolderEx vh = ReflectionUtil.newInst(getViewHolderClz(), v);
        if (vh == null) {
            return false;
        }

        item.setAttrs(vh, groupPosition, this);
        v.setTag(vh);

        v.setOnClickListener(mListener);
        mMapGroupClick.put(v, groupPosition);

        return true;
    }

    /**
     * 初始化child view
     *
     * @param item
     * @param groupPosition
     * @param childPosition
     * @return
     */
    private <CHILD extends ChildFormEx> CHILD addChildItemView(CHILD item, LinearLayout root, int groupPosition, int childPosition) {
//        View v = getLayoutInflater().inflate(item.getResId(), null);
//        if (v == null) {
//            return;
//        }
//        root.addView(v, LayoutUtil.getLinearParams(MATCH_PARENT, WRAP_CONTENT));
//
//        ViewHolderEx vh = ReflectionUtil.newInst(getViewHolderClz(), v);
//        if (vh == null) {
//            return;
//        }
//
//        item.setAttrs(vh, groupPosition, childPosition, this);
//        v.setTag(vh);
//
//        v.setOnClickListener(mListener);
//        mMapChildClick.put(v, item);
        return null;
    }

    protected int getCount() {
        return mItems.size();
    }

    protected HashMap<Object, T> getMapGroupRelated() {
        return mMapGroupRelated;
    }

    protected HashMap<Serializable, ChildFormEx> getMapChildConfig() {
        return mMapChildConfig;
    }

    protected T getItem(Object related) {
        return mMapGroupRelated.get(related);
    }

    protected T getGroupItem(int groupPosition) {
        return mItems.get(groupPosition);
    }

    protected ChildFormEx getChildItem(Serializable config) {
        return mMapChildConfig.get(config);
    }

    protected List<T> getData() {
        return mItems;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMapGroupRelated != null) {
            mMapGroupRelated.clear();
            mMapGroupRelated = null;
        }

        if (mMapGroupResId != null) {
            mMapGroupResId.clear();
            mMapGroupResId = null;
        }

        if (mMapGroupClick != null) {
            mMapGroupClick.clear();
            mMapGroupClick = null;
        }

        if (mMapChildClick != null) {
            mMapChildClick.clear();
            mMapChildClick = null;
        }

        if (mMapChildConfig != null) {
            mMapChildConfig.clear();
            mMapChildConfig = null;
        }

        if (mMapChildResId != null) {
            mMapChildResId.clear();
            mMapChildResId = null;
        }

        if (mItems != null) {
            mItems.clear();
            mItems = null;
        }
    }

    public View getFooterView() {
        return null;
    }

    public View getHeaderView() {
        return null;
    }

    /**
     * @param item
     * @param resId
     * @param key    每个item的map key, 可以直接通过这个key寻找对应的item
     * @param isInit 是否是在数据初始化的时候添加, true表示不进行inflate
     */
    protected void addItem(T item, int resId, Serializable key, boolean isInit) {
        if (item == null || resId <= 0) {
            return;
        }

        mItems.add(item);
        mMapGroupResId.put(item, resId);
        mMapGroupRelated.put(key, item);

        // if (childResIds != null && childKeys != null) {
        // List<ChildFormItemEx> childItems = item.getChildItems();
        // for (int i = 0; i < childResIds.size() && i < childItems.size(); ++i)
        // {
        // mMapChildResId.put(childItems.get(i), childResIds.get(i));
        // mMapChildConfig.put(childKeys.get(i), childItems.get(i));
        // }
        // }
        List<ChildFormEx> childItems = item.getChildItems();
        if (childItems != null && !childItems.isEmpty()) {
            for (int i = 0; i < childItems.size(); i++) {
                ChildFormEx childItem = childItems.get(i);
//                mMapChildResId.put(childItem, childItem.getResId());
//                mMapChildConfig.put(childItem.getKey(), childItem);
            }
        }

        if (!isInit) {
            // TODO: 参照 setViews
            // View v = getLayoutInflater().inflate(resId, null);
            // addGroupItemView(item, v, mItems.size() - 1);
            //
            // if (childResIds != null) {
            // List<ChildFormItemEx> childItems = item.getChildItems();
            // for (int i = 0; i < childItems.size(); ++i) {
            // ChildFormItemEx childItem = childItems.get(i);
            // View childView =
            // getLayoutInflater().inflate(mMapChildResId.get(childItem), null);
            // addChildItemView(childItem, childView, mItems.size() - 1, i);
            // }
            // }
        }
    }

    @Override
    protected void onResultData(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if ((requestCode & 1) == 1) {
            //表示点击的是group
            requestCode = (requestCode >> ConstantsEx.KGroupOffset) & 0xff;
            getGroupItem(requestCode).onActivityResult(requestCode, resultCode, data);
            freshItem(requestCode);
        } else {
            int groupPosition = (requestCode >> ConstantsEx.KGroupOffset) & 0xff;
            int childPosition = (requestCode >> ConstantsEx.KChildOffset) & 0xff;
            ChildFormEx childItem = (ChildFormEx) getGroupItem(groupPosition).getChildItems().get(childPosition);
            childItem.onActivityResult(requestCode, resultCode, data);
            childItem.refresh();
        }
    }

    /**
     * 刷新item布局
     *
     * @param position
     */
    protected void freshItem(int position) {
        getGroupItem(position).refresh();
    }

    protected void freshItem(Serializable config) {
        T item = getItem(config);
        if (item != null) {
            item.refresh();
        }
    }

    protected IViewHolder getViewHolder(int position) {
        return getGroupItem(position).getHolder();
    }

    protected IViewHolder getViewHolder(Serializable config) {
        return getItem(config).getHolder();
    }

    /**
     * scrollview滑动到底部
     */
    public void scrollToBottom() {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    /**
     * 点击item
     *
     * @param position
     */
    abstract protected void onGroupItemClick(int position);

    abstract protected void onChildItemClick(int groupPosition, int childPosition);

    abstract protected Class<? extends ViewHolderEx> getViewHolderClz();

    /**
     * 把item的点击监听单独出来, 和header和footer进行区分
     *
     * @author yuansui
     */
    private class ItemClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (mMapChildClick.get(v) instanceof ChildFormEx) {
                ChildFormEx childItem = mMapChildClick.get(v);
                onChildItemClick(childItem.getGroupPosition(), childItem.getChildPosition());
            } else {
                onGroupItemClick(mMapGroupClick.get(v));
            }
        }

    }

    @Override
    public void onViewClick(View v, int position, Object related) {

    }

    @Override
    public void onItemViewClick(View v, int groupPosition, int childPosition) {
    }

    protected void expandGroup(int position) {
        View view = mMapChildBindGroup.get(position);
        if (view != null) {
            showView(view);
        }
    }

    protected void collapseGroup(int position) {
        View view = mMapChildBindGroup.get(position);
        if (view != null) {
            goneView(view);
        }
    }

    protected void expandAllGroup() {
        for (int i = 0; i < mItems.size(); i++) {
            View view = mMapChildBindGroup.get(i);
            if (view != null) {
                showView(view);
            }
        }
    }

    protected void collapseAllGroup() {
        for (int i = 0; i < mItems.size(); ++i) {
            View view = mMapChildBindGroup.get(i);
            if (view != null) {
                goneView(view);
            }
        }
    }

    protected boolean initCollapseAllGroup() {
        return true;
    }
}
