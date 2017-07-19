package lib.ys.frag.form;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java8.lang.Iterables;
import lib.ys.ConstantsEx;
import lib.ys.R;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.adapter.interfaces.IViewHolder;
import lib.ys.form.FormItemEx;
import lib.ys.form.FormItemEx.TForm;
import lib.ys.form.IFormHost;
import lib.ys.form.OnFormViewClickListener;
import lib.ys.form.TransparencyType;
import lib.ys.frag.FragEx;
import lib.ys.util.GenericUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.view.LayoutUtil;
import lib.ys.view.StayScrollView;


@SuppressWarnings("rawtypes")
abstract public class FormFragEx<T extends FormItemEx<VH>, VH extends ViewHolderEx> extends FragEx
        implements OnFormViewClickListener, IFormHost {

    private List<T> mItems;

    private LinearLayout mLayoutHeader;
    private LinearLayout mLayoutItems;
    private LinearLayout mLayoutFooter;
    private StayScrollView mSv;

    private Map<Object/* key */, T> mMapRelated;
    private Map<View, Integer> mMapClick;

    private OnClickListener mItemClickListener;

    private List<T> mRemandItems;

    private Class<VH> mVHClass;

    public FormFragEx() {
        mVHClass = GenericUtil.getClassType(getClass(), IViewHolder.class);
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_form_items;
    }

    @CallSuper
    @Override
    public void initData() {
        mItems = new ArrayList<>();

        mMapClick = new HashMap<>();
        mMapRelated = new HashMap<>();

        mRemandItems = new ArrayList<>();

        /**
         * 把item的点击监听单独出来, 和header和footer进行区分
         */
        mItemClickListener = v -> onItemClick(v, mMapClick.get(v));
    }

    @CallSuper
    @Override
    public void findViews() {
        mLayoutHeader = findView(R.id.base_form_layout_header);
        mLayoutItems = findView(R.id.base_form_layout_items);
        mLayoutFooter = findView(R.id.base_form_layout_footer);
        mSv = findView(R.id.base_form_scroll_view);

        View header = createHeaderView();
        if (header != null) {
            mLayoutHeader.addView(header, LayoutUtil.getLinearParams(MATCH_PARENT, WRAP_CONTENT));
            fit(header);
        }

        View footer = createFooterView();
        if (footer != null) {
            mLayoutFooter.addView(footer, LayoutUtil.getLinearParams(MATCH_PARENT, WRAP_CONTENT));
            fit(footer);
        }
    }

    @CallSuper
    @Override
    public void setViews() {
        for (T item : mRemandItems) {
            mItems.add(item);
            Object related = item.getObject(TForm.related);
            if (related != null) {
                mMapRelated.put(related, item);
            }

            View v = getLayoutInflater().inflate(item.getContentViewResId(), null);
            addItemView(item, v, getLastItemPosition());
        }

        /**
         * 为了修复fragment在view pager切换的时候, 会自动滚到到最底部的bug
         */
        ViewGroup parent = (ViewGroup) mSv.getParent();
        parent.setFocusable(true);
        parent.setFocusableInTouchMode(true);
        parent.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
    }

    protected void addHeaderView(View v) {
        mLayoutHeader.addView(v);
    }

    protected void addHeaderView(View v, int position) {
        mLayoutHeader.addView(v, position);
    }

    protected void showHeaderView() {
        showView(mLayoutHeader);
    }

    protected void goneHeaderView() {
        goneView(mLayoutHeader);
    }

    private void addItemView(T item, View v, int position) {
        if (v == null) {
            return;
        }

        mLayoutItems.addView(v, LayoutUtil.getLinearParams(MATCH_PARENT, WRAP_CONTENT));
        fit(v);

        VH holder = ReflectionUtil.newInst(mVHClass, v);
        if (holder == null) {
            return;
        }

        item.setAttrs(holder, position, this);

        v.setOnClickListener(mItemClickListener);
        mMapClick.put(v, position);
    }

    protected int getCount() {
        if (mItems == null) {
            return 0;
        }
        return mItems.size();
    }

    protected boolean isEmpty() {
        return getCount() == 0;
    }

    protected int getLastItemPosition() {
        int position = getCount() - 1;
        if (position < 0) {
            position = 0;
        }
        return position;
    }

    @Override
    public T getRelatedItem(Object related) {
        return mMapRelated.get(related);
    }

    @Override
    public T getItem(int position) {
        return mItems.get(position);
    }

    protected List<T> getData() {
        return mItems;
    }

    /**
     * 获取初始化的footer
     *
     * @return
     */
    protected View createFooterView() {
        return null;
    }

    /**
     * 获取初始化的header
     *
     * @return
     */
    protected View createHeaderView() {
        return null;
    }

    protected final View getHeaderView() {
        return mLayoutHeader;
    }

    protected final View getFooterView() {
        return mLayoutFooter;
    }

    public void setItems(List<T> ts) {
        mItems.clear();
        mRemandItems.clear();
        removeAllItem();

        Iterables.forEach(ts, t -> addItem(t));
    }

    public final T addItem(T t) {
        if (t == null || t.getContentViewResId() <= 0) {
            return t;
        }

        t.put(TForm.host, this);

        Object related = t.getObject(TForm.related);

        if (mLayoutItems == null) {
            mRemandItems.add(t);
            return t;
        } else {
            mItems.add(t);
            if (related != null) {
                mMapRelated.put(related, t);
            }
        }

        View v = getLayoutInflater().inflate(t.getContentViewResId(), null);
        addItemView(t, v, getLastItemPosition());

        return t;
    }

    @Override
    protected void onResultData(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || requestCode >= getCount()) {
            return;
        }
        getItem(requestCode).onActivityResult(requestCode, resultCode, data);
        refreshItem(requestCode);
    }

    /**
     * 刷新item布局
     *
     * @param position
     */
    protected void refreshItem(int position) {
        getItem(position).refresh();
    }

    protected void refreshRelatedItem(Object related) {
        T item = getRelatedItem(related);
        if (item != null) {
            item.refresh();
        }
    }

    protected void refreshItem(T... ts) {
        for (T t : ts) {
            t.refresh();
        }
    }

    protected void hideItem(int position) {
        getItem(position).hide();
    }

    protected void showItem(int position) {
        getItem(position).show();
    }

    protected void hideItem(T... ts) {
        for (T t : ts) {
            t.hide();
        }
    }

    protected void showItem(T... ts) {
        for (T t : ts) {
            t.show();
        }
    }

    protected void hideRelatedItem(Object related) {
        T t = getRelatedItem(related);
        if (t != null) {
            t.hide();
        }
    }

    protected void showRelatedItem(Object related) {
        T t = getRelatedItem(related);
        if (t != null) {
            t.show();
        }
    }

    protected void remove(T item) {
        mItems.remove(item);
    }

    protected void remove(int position) {
        mItems.remove(position);
    }

    public void removeAllItem() {
        mItems.clear();
        mLayoutItems.removeAllViews();
    }

    protected ViewHolderEx getViewHolder(int position) {
        return getItem(position).getHolder();
    }

    protected ViewHolderEx getViewHolder(Object related) {
        return getRelatedItem(related).getHolder();
    }

    /**
     * 直接显示顶部
     */
    public void scrollToTop() {
        mSv.scrollTo(0, 0);
    }

    /**
     * scrollview滑动到顶部
     */
    public void smoothScrollToTop() {
        runOnUIThread(() -> mSv.fullScroll(ScrollView.FOCUS_UP));
    }

    /**
     * scrollview滑动到底部
     */
    public void smoothScrollToBottom() {
        runOnUIThread(() -> mSv.fullScroll(ScrollView.FOCUS_DOWN));
    }

    /**
     * 点击item, item和host相斥接收到消息, 只有item本身不处理的时候host才处理
     *
     * @param position
     */
    protected final void onItemClick(View v, int position) {
        if (!getItem(position).onItemClick(this, v)) {
            onFormItemClick(v, position);
        }
    }

    @Override
    public final void onViewClick(View v, int position, Object related) {
        onFormViewClick(v, position, related);
    }

    /**
     * 点击form的整条item
     *
     * @param v
     * @param position
     */
    protected void onFormItemClick(View v, int position) {
    }

    /**
     * 点击form上的某个view
     *
     * @param v
     * @param position
     * @param related
     */
    protected void onFormViewClick(View v, int position, Object related) {
    }

    /**
     * 检测是否有效
     *
     * @return
     */
    protected boolean check() {
        if (isEmpty()) {
            return false;
        }

        for (T item : getData()) {
            if (item.check()) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    protected void setScrollViewBgColor(@ColorInt int color) {
        mSv.setBackgroundColor(color);
    }

    protected void smoothSetTransparency(final int distance, final View v, @TransparencyType final int type) {
        int height = v.getHeight();
        if (height == 0 && v.getVisibility() != View.GONE) {
            addOnPreDrawListener(new OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    int height = v.getHeight();
                    if (height == 0) {
                        return true;
                    }

                    final int realDis = distance - height;
                    bindScrollListener(v, type, realDis);

                    removeOnPreDrawListener(this);
                    return true;
                }
            });
        } else {
            int realDis = distance - height;
            bindScrollListener(v, type, realDis);
        }
    }

    private void bindScrollListener(final View v, @TransparencyType final int type, final int distance) {
        mSv.setOnScrollListener((l, t, oldl, oldt) -> {
            float rate = computeAlphaRate(t, distance);
            if (type == TransparencyType.bg) {
                v.getBackground().setAlpha((int) (rate * ConstantsEx.KAlphaMax));
            } else {
                v.setAlpha(rate);
            }
        });
    }

    private float computeAlphaRate(float top, float distance) {
        float rate = top / distance;
        if (rate > 1) {
            rate = 1;
        }
        return rate;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMapRelated != null) {
            mMapRelated.clear();
            mMapRelated = null;
        }

        if (mMapClick != null) {
            mMapClick.clear();
            mMapClick = null;
        }

        if (mItems != null) {
            mItems.clear();
            mItems = null;
        }

        if (mRemandItems != null) {
            mRemandItems.clear();
            mRemandItems = null;
        }
    }
}
