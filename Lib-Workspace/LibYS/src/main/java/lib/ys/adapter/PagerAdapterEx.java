package lib.ys.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lib.ys.AppEx;
import lib.ys.LogMgr;
import lib.ys.adapter.interfaces.IViewHolder;
import lib.ys.fitter.DpFitter;
import lib.ys.fitter.LayoutFitter;
import lib.ys.interfaces.IFitParams;
import lib.ys.interfaces.IOption;
import lib.ys.util.GenericUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.pager.indicator.IconPagerAdapter;

abstract public class PagerAdapterEx<T, VH extends ViewHolderEx> extends PagerAdapter implements IFitParams, IOption, IconPagerAdapter {

    protected String TAG = getClass().getSimpleName();

    private List<T> mTs;
    private LayoutInflater mInflater = null;

    private Class<VH> mVHClass;

    // 给loop专用的缓存map
    private SparseArray<View> mMapLoop = null;

    public PagerAdapterEx() {
        mMapLoop = new SparseArray<>();

        mVHClass = GenericUtil.getClassType(getClass(), IViewHolder.class);
    }

    public void setData(List<T> data) {
        mTs = data;
    }

    public T getItem(int position) {
        if (mTs == null) {
            return null;
        }

        T t = null;
        try {
            if (isLoop()) {
                position %= getRealCount();
            }
            t = mTs.get(position);
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        }

        return t;
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

    @Override
    public int getCount() {
        int count = mTs == null ? 0 : mTs.size();
        if (isLoop()) {
            if (count < 2) {
                return count;
            }

            return Integer.MAX_VALUE;
        } else {
            return count;
        }
    }

    @Override
    public boolean isLoop() {
        return false;
    }

    @Override
    public int getRealCount() {
        return mTs == null ? 0 : mTs.size();
    }

    protected int getLastItemPosition() {
        int count = getCount();
        return count == 0 ? 0 : count - 1;
    }

    @Override
    abstract public int getIndicatorResId(int index);

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = null;
        int modPos = 0;
        if (isLoop()) {
            modPos = position % getRealCount();
            convertView = mMapLoop.get(modPos);
        } else {
            modPos = position;
        }

        boolean needInit = false;
        if (convertView == null) {
            needInit = true;
        } else if (convertView.getParent() != null) {
            // 没有被系统自动调用destroyItem的view不能直接使用
            needInit = true;
            mMapLoop.remove(modPos);
        }

        if (needInit) {
            convertView = getLayoutInflater().inflate(getConvertViewResId(), null);
            fit(convertView);

            VH holder = ReflectionUtil.newInst(mVHClass, convertView);
            convertView.setTag(holder);

            initView(modPos, holder);

            mMapLoop.put(modPos, convertView);
        }

        refreshView(modPos, (VH) convertView.getTag());

        container.addView(convertView);

        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    protected Context getContext() {
        return AppEx.ct();
    }

    protected LayoutInflater getLayoutInflater() {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(getContext());
        }
        return mInflater;
    }

    protected void initView(int position, VH holder) {
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
        Intent intent = new Intent(AppEx.ct(), clz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    /**
     * @deprecated 调用无效, 只能启动activity而没有result返回
     */
    @Override
    public void startActivityForResult(Class<?> clz, int requestCode) {
        Intent intent = new Intent(AppEx.ct(), clz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
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

    abstract public int getConvertViewResId();

    abstract protected void refreshView(int position, VH holder);

    public void onDestroy() {
        mMapLoop.clear();
        mMapLoop = null;
    }
}
