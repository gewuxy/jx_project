package jx.doctor.ui.frag.user;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseListFrag;
import jx.doctor.adapter.user.SectionNameAdapter;

/**
 * 科室名字(二级)
 *
 * @auther GuoXuan
 * @since 2017/5/15
 */

public class SectionNameFrag extends BaseListFrag<String, SectionNameAdapter> {

    private OnSectionListener mListener;

    @Override
    public void initData(Bundle state) {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();
        setDividerHeight(fit(0));
    }

    @Override
    public void onItemClick(View v, int position) {
        if (mListener != null) {
            mListener.onSectionSelected(position, getItem(position));
        }
    }

    public void setSection(List<String> sections) {
        setData(sections);
        getAdapter().notifyDataSetChanged();
    }

    public interface OnSectionListener {
        void onSectionSelected(int position, String name);
    }

    public void setListener(OnSectionListener l) {
        mListener = l;
    }

}
