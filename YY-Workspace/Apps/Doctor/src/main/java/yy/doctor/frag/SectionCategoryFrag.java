package yy.doctor.frag;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.model.MapList;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseSRListFrag;
import lib.yy.network.ListResult;
import yy.doctor.adapter.SectionCategoryAdapter;
import yy.doctor.model.me.Section;
import yy.doctor.model.me.Section.TSection;
import yy.doctor.network.NetFactory;

/**
 * 科室隶属(一级)
 *
 * @auther GuoXuan
 * @since 2017/5/15
 */

public class SectionCategoryFrag extends BaseSRListFrag<Section, SectionCategoryAdapter> {

    private OnCategoryListener mListener;
    private MapList<String, List<String>> mSections;//全部科室数据(分类后)
    private boolean mInit = true;

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
        exeNetworkReq(0, NetFactory.section());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp nr) throws Exception {
        //全部数据
        ListResult<Section> allSection = (ListResult<Section>) super.onNetworkResponse(id, nr);
        //把相同的过滤掉
        ListResult<Section> showSection = new ListResult<>();
        //隶属列表(一级)
        List<Section> categories = allSection.getData();
        mSections = new MapList<>();
        String category;//科室隶属
        String name;//科室名称
        List<String> sections;//隶属的科室列表(二级)
        //按隶属分类
        for (Section section : categories) {
            category = section.getString(TSection.category);
            name = section.getString(TSection.name);
            sections = mSections.getByKey(category);
            if (sections == null) {
                sections = new ArrayList<>();
                mSections.add(category, sections);
                showSection.add(section);
            }
            sections.add(name);
        }

        return showSection;
    }

    @Override
    public void onDataSetChanged() {
        super.onDataSetChanged();

        if (mInit) {
            mInit = false;
            onItemClick(null, 0);
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        if (mListener != null) {
            String category = getItem(position).getString(TSection.category);
            mListener.onCategorySelected(position, mSections.getByKey(category));
        }

        getAdapter().setSelectItem(position);
    }

    public interface OnCategoryListener {
        void onCategorySelected(int position, List<String> names);
    }

    public void setCategoryListener(OnCategoryListener l) {
        mListener = l;
    }
}
