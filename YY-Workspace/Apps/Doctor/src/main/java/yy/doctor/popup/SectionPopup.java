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

import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.fitter.DpFitter;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.interfaces.listener.OnRetryClickListener;
import lib.ys.ui.other.PopupWindowEx;
import lib.yy.network.ListResult;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingSectionAdapter;
import yy.doctor.model.meet.MeetingDepartment;
import yy.doctor.model.meet.MeetingDepartment.TMeetingDepartment;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkApiDescriptor.MeetAPI;

/**
 * @auther yuansui
 * @since 2017/4/26
 */

public class SectionPopup extends PopupWindowEx implements OnItemClickListener, OnRetryClickListener {

    private OnSectionListener mLsn;

    private ListView mListView;
    private MeetingSectionAdapter mAdapter;

    public interface OnSectionListener {
        void onSectionSelected(String text);
    }

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
        mAdapter = new MeetingSectionAdapter();
        getDataFromNet();
    }

    private void getDataFromNet() {
        refresh(RefreshWay.embed);
        exeNetworkReq(MeetAPI.meetDepartment().build());
    }

    @Override
    public int getWindowWidth() {
        return MATCH_PARENT;
    }

    @Override
    public int getWindowHeight() {
        return DpFitter.dp(484);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mLsn != null) {
            mLsn.onSectionSelected(mAdapter.getItem(position).getString(TMeetingDepartment.name));
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
        if (r.isSucceed()) {
            List<MeetingDepartment> data = r.getData();
            mAdapter.setData(data);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
            setViewState(ViewState.normal);
        } else {
            setViewState(ViewState.error);
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    @Override
    public boolean onRetryClick() {
        if (super.onRetryClick()) {
            return true;
        }
        getDataFromNet();
        return true;
    }

}
