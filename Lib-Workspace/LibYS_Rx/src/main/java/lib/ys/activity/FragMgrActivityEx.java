package lib.ys.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lib.ys.ConstantsEx;
import lib.ys.R;

/**
 * 管理多个fragment的添加和切换
 *
 * @author yuansui
 */
abstract public class FragMgrActivityEx extends ActivityEx {

    /**
     * fragment管理相关
     */
    private FragmentManager mFm;
    private List<Fragment> mFrags;

    // tab和fragment的关联
    private HashMap<Fragment, Integer> mTabMap;

    private boolean mIsInStateLoss = false;

    @Override
    public int getContentViewId() {
        return R.layout.activity_frag_mgr;
    }

    protected int getContainerResId() {
        return R.id.frag_mgr_layout_container;
    }

    @Override
    public void initData() {
        mFm = getSupportFragmentManager();

        mFrags = new ArrayList<>();
        mTabMap = new HashMap<>();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // 不进行状态的保存, 如果被意外销毁了, 重新进行加载
        // super.onSaveInstanceState(outState);
        mIsInStateLoss = true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // super.onRestoreInstanceState(savedInstanceState);
        mIsInStateLoss = false;
    }

    protected int add(Fragment fragment, int tabId) {
        mFrags.add(fragment);

        if (tabId != 0) {
            mTabMap.put(fragment, tabId);
        }

        FragmentTransaction transaction = mFm.beginTransaction();
        transaction.add(getContainerResId(), fragment);
        return commit(transaction);
    }

    protected int add(Fragment fragment) {
        return add(fragment, 0);
    }

    protected int show(Fragment fragment) {
        FragmentTransaction transaction = mFm.beginTransaction();

        for (Fragment frag : mFrags) {
            Integer resId = mTabMap.get(frag);
            if (frag == fragment) {
                transaction.show(fragment);
                if (resId != null) {
                    findView(resId).setSelected(true);
                }
            } else {
                transaction.hide(frag);
                if (resId != null) {
                    findView(resId).setSelected(false);
                }
            }
        }

        return commit(transaction);
    }

    protected int show(int position) {
        if (position >= 0 && position < mFrags.size()) {
            return show(mFrags.get(position));
        }
        return ConstantsEx.KInvalidValue;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mTabMap.clear();
        mTabMap = null;

        mFrags.clear();
        mFrags = null;
    }

    protected int commit(FragmentTransaction transaction) {
        if (mIsInStateLoss) {
            return transaction.commitAllowingStateLoss();
        } else {
            return transaction.commit();
        }
    }
}
