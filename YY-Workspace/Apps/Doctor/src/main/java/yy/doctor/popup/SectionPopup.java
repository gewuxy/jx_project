package yy.doctor.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import lib.ys.ui.other.PopupWindowEx;
import yy.doctor.R;
import yy.doctor.adapter.meeting.SectionFilterAdapter;
import yy.doctor.model.meet.SectionFilter;
import yy.doctor.model.meet.SectionFilter.TSectionFilter;

/**
 * @auther yuansui
 * @since 2017/4/26
 */

public class SectionPopup extends PopupWindowEx implements OnItemClickListener {


    private OnSectionListener mLsn;

    private ListView mListView;
    private List<SectionFilter> mList;

    public SectionPopup(@NonNull Context context, @Nullable OnSectionListener l) {
        super(context);
        mLsn = l;
    }

    @Override
    public void initData() {
        setTouchOutsideDismissEnabled(true);
        setDimEnabled(true);
        mList = new ArrayList<>();
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_meeting_select_section;
    }

    @Override
    public void findViews() {
        mListView = findView(R.id.section_filter_lv);
    }


    @Override
    public void setViews() {
        SectionFilterAdapter adapter = new SectionFilterAdapter();
        SectionFilter sectionFilter = new SectionFilter();
        for (int i = 0; i < 25; i++) {
            sectionFilter.put(TSectionFilter.bitmap, R.mipmap.data_ic_arrow_down);
            sectionFilter.put(TSectionFilter.name, "内科");
            sectionFilter.put(TSectionFilter.number, 100);
            mList.add(sectionFilter);
        }
        adapter.setData(mList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
      //  mListView.getDivider();
//        mRv.setLayoutManager(new StaggeredGridLayoutManager(KRowCount, StaggeredGridLayoutManager.VERTICAL));
//
//        // 分割线
//        mRv.addItemDecoration(new GridDivider(
//                DpFitter.dp(KDividerHeight),
//                R.drawable.section_divider_bg));
//
//        final SectionAdapter adapter = new SectionAdapter();
//        adapter.addAll(Util.getSections());
//        mRv.setAdapter(adapter);
//        adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {
//
//            @Override
//            public void onItemClick(View v, int position) {
//                if (mLsn != null) {
//                    mLsn.onSectionSelected(adapter.getItem(position));
//                }
//                dismiss();
//            }
//        });

    }

    @Override
    public int getWindowWidth() {
        return MATCH_PARENT;
    }

    @Override
    public int getWindowHeight() {
        return 1390;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showToast("你好 "+position);
    }

    public interface OnSectionListener {
        void onSectionSelected(String text);
    }
}
