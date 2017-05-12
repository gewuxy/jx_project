package yy.doctor.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import lib.ys.adapter.recycler.OnRecyclerItemClickListener;
import lib.ys.fitter.DpFitter;
import lib.ys.ui.other.PopupWindowEx;
import lib.yy.view.GridDivider;
import yy.doctor.R;
import yy.doctor.adapter.SectionAdapter;
import yy.doctor.util.Util;

import static yy.doctor.Constants.SectionConstants.KDividerHeight;
import static yy.doctor.Constants.SectionConstants.KRowCount;

/**
 * @auther yuansui
 * @since 2017/4/26
 */

public class SectionPopup extends PopupWindowEx {

    private RecyclerView mRv;

    private OnSectionListener mLsn;

    public SectionPopup(@NonNull Context context, @Nullable OnSectionListener l) {
        super(context);
        mLsn = l;
    }

    @Override
    public void initData() {
        setTouchOutsideDismissEnabled(true);
        setDimEnabled(true);
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
    public void setViews() {

        mRv.setLayoutManager(new StaggeredGridLayoutManager(KRowCount, StaggeredGridLayoutManager.VERTICAL));

        mRv.addItemDecoration(new GridDivider(
                DpFitter.dp(KDividerHeight),
                R.drawable.section_divider_bg));

        final SectionAdapter adapter = new SectionAdapter();
        adapter.addAll(Util.getSections());
        mRv.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                if (mLsn != null) {
                    mLsn.onSectionSelected(adapter.getItem(position));
                }
                dismiss();
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
