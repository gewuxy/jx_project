package yy.doctor.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.util.Arrays;

import lib.ys.adapter.interfaces.OnRecyclerItemClickListener;
import lib.ys.ex.PopupWindowEx;
import lib.ys.fitter.DpFitter;
import lib.yy.view.GridItemDecoration;
import lib.yy.view.GridItemDecoration.Decoration;
import yy.doctor.R;
import yy.doctor.adapter.SectionAdapter;

/**
 * @auther yuansui
 * @since 2017/4/26
 */

public class SectionPopup extends PopupWindowEx {
    private static final int KGridViewRowCount = 3; // 列数
    private static final int KDividerHeight = 14; // 分割线高度

    private static final String[] KSectionNames = new String[]{
            "内科", "外科", "妇科", "儿科", "口腔科", "耳鼻咽喉科",
            "药剂科", "精神科", "康复科", "急诊科", "中药科", "麻醉科",
            "胃肠科", "医疗美容科", "眼科", "中医科", "内分泌科", "全科",
            "骨科", "肿瘤科", "产科", "消化科", "传染科", "其他"};


    private RecyclerView mRv;

    private OnSectionListener mLsn;

    public SectionPopup(@NonNull Context context, @Nullable OnSectionListener l) {
        super(context);
        mLsn = l;
    }

    @Override
    public void initData() {
        setOutsideTouchable(true);
        setTouchOutsideDismissEnabled(true);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_meeting_select_section;
    }

    @Override
    public void findViews() {
        mRv = findView(R.id.meeting_layout_recyclerview);
    }

    @Override
    public void setViewsValue() {

        mRv.setLayoutManager(new StaggeredGridLayoutManager(KGridViewRowCount, StaggeredGridLayoutManager.VERTICAL));

        mRv.addItemDecoration(new GridItemDecoration(
                Decoration.vertical,
                DpFitter.dp(KDividerHeight),
                R.drawable.section_divider_bg));

        final SectionAdapter adapter = new SectionAdapter();
        adapter.addAll(Arrays.asList(KSectionNames));
        mRv.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                if (mLsn != null) {
                    mLsn.onSectionSelected(adapter.getItem(position));
                }
                dismiss();
            }

            @Override
            public void onItemLongClick(View v, int position) {
            }
        });

    }

    @Override
    public int getWindowWidth() {
        return MATCH_PARENT;
    }

    @Override
    public int getWindowHeight() {
        return WRAP_CONTENT;
    }

    public interface OnSectionListener {
        void onSectionSelected(String text);
    }
}
