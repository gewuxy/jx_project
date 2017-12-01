package jx.doctor.popup;

import android.content.Context;
import android.os.Bundle;
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
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.fitter.Fitter;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.interfaces.listener.OnRetryClickListener;
import lib.ys.ui.other.PopupWindowEx;
import jx.doctor.R;
import jx.doctor.adapter.meeting.MeetingSectionAdapter;
import jx.doctor.model.meet.MeetingDepartment;
import jx.doctor.model.meet.MeetingDepartment.TMeetingDepartment;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;

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
    public void initData(Bundle state) {
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
        return Fitter.dp(484);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mLsn != null) {
            mLsn.onSectionSelected(mAdapter.getItem(position).getString(TMeetingDepartment.name));
        }
        dismiss();
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws JSONException {
        return JsonParser.evs(resp.getText(), MeetingDepartment.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            List<MeetingDepartment> data = r.getList();
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
