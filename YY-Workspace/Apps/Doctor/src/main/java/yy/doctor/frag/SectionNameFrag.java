package yy.doctor.frag;

import android.view.View;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.adapter.SectionNameAdapter;

/**
 * 科室名字(二级)
 *
 * @auther GuoXuan
 * @since 2017/5/15
 */

public class SectionNameFrag extends BaseSRListFrag<String, SectionNameAdapter> {

    private OnSectionListener mListener;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();

        getLv().setVerticalScrollBarEnabled(false);
        setDividerHeight(fitDp(0));
        setRefreshEnabled(false);
        setAutoLoadMoreEnabled(false);
    }

    @Override
    public void getDataFromNet() {
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

    @Override
    public boolean enableRefreshWhenInit() {
        return false;
    }

    public interface OnSectionListener {
        void onSectionSelected(int position, String name);
    }

    public void setListener(OnSectionListener l) {
        mListener = l;
    }

}
