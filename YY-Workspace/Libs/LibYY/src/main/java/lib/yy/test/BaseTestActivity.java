package lib.yy.test;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.impl.SingletonImpl;
import lib.ys.ui.dialog.DialogEx;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseListActivity;

/**
 * @author yuansui
 */
abstract public class BaseTestActivity extends BaseListActivity<Test> {

    @Override
    public MultiAdapterEx createAdapter() {
        return new TestAdapter();
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addTextViewMid("测试");
    }

    protected void add(String name, Class clz) {
        Test t = new Test();
        t.mName = name;
        t.mClass = clz;
        addItem(t);
    }

    protected void add(String name, Intent intent) {
        Test t = new Test();
        t.mName = name;
        t.mIntent = intent;
        addItem(t);
    }

    protected void add(String name, DialogEx dialog) {
        Test t = new Test();
        t.mName = name;
        t.mDialogEx = dialog;
        addItem(t);
    }

    protected void add(String name, Dialog dialog) {
        Test t = new Test();
        t.mName = name;
        t.mDialog = dialog;
        addItem(t);
    }

    protected void add(String name, OnClickListener listener) {
        Test t = new Test();
        t.mName = name;
        t.mListener = listener;
        addItem(t);
    }

    @Override
    public void onItemClick(View v, int position) {
        Test t = getItem(position);
        if (t.mClass != null) {
            startActivity(getItem(position).mClass);
        } else if (t.mIntent != null) {
            startActivity(t.mIntent);
        } else if (t.mDialogEx != null) {
            t.mDialogEx.show();
        } else if (t.mDialog != null) {
            t.mDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SingletonImpl.inst().freeAll();
    }
}
