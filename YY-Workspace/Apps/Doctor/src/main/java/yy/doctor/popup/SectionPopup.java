package yy.doctor.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import org.json.JSONException;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.ui.other.PopupWindowEx;
import lib.yy.network.ListResult;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingDepartmentAdapter;
import yy.doctor.model.meet.MeetingDepartment;
import yy.doctor.model.meet.MeetingDepartment.TMeetingDepartment;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * @auther yuansui
 * @since 2017/4/26
 */

public class SectionPopup extends PopupWindowEx implements OnItemClickListener {


    private OnSectionListener mLsn;

    private ListView mListView;
    private MeetingDepartmentAdapter adapter;

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
        mListView = findView(R.id.section_filter_lv);
    }


    @Override
    public void setViews() {
        adapter = new MeetingDepartmentAdapter();
        exeNetworkReq(NetFactory.meetingDepartment());
      /*  for (int i = 0; i < 25; i++) {
            mSectionFilter.put(TSectionFilter.bitmap, R.mipmap.data_ic_arrow_down);
            mSectionFilter.put(TSectionFilter.name, "内科");
            mSectionFilter.put(TSectionFilter.number, 100);
            mList.add(mSectionFilter);
        }
        adapter.setData(mList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);*/
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
        showToast("你好 " + position);
        if (mLsn != null) {
            mLsn.onSectionSelected(adapter.getItem(position).getString(TMeetingDepartment.name));
        }
        dismiss();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws JSONException {
        return JsonParser.evs(r.getText(), MeetingDepartment.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        ListResult r = (ListResult) result;
        List<MeetingDepartment> data = r.getData();
        adapter.setData(data);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    public interface OnSectionListener {
        void onSectionSelected(String text);
    }
}
