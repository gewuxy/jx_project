package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.network.model.interfaces.IListResult;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseSRListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.meeting.VideoDetailAdapter;
import yy.doctor.model.meet.video.Detail;
import yy.doctor.model.meet.video.Detail.TDetail;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 视频列表界面
 *
 * @author : GuoXuan
 * @since : 2017/5/24
 */

public class VideoDetailActivity extends BaseSRListActivity<Detail, VideoDetailAdapter> implements OnAdapterClickListener {
    private String mPreId;
    private List<Detail> mDetails;
    private TextView mBarTvRight;

    public static void nav(Context context, String preId) {
        Intent i = new Intent(context, VideoDetailActivity.class)
                .putExtra(Extra.KData, preId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mPreId = getIntent().getStringExtra(Extra.KData);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "视频", this);
        mBarTvRight = bar.addTextViewRight("", null);
    }

    @Override
    public void setViews() {
        super.setViews();
        setOnAdapterClickListener(this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.video(mPreId));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.evs(r.getText(), Detail.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);
        setViewState(ViewState.normal);
        IListResult<Detail> r = (IListResult<Detail>) result;
        if (r.isSucceed()) {
            mDetails = r.getData();
            addAll(mDetails);

            long time = 0;
            for (Detail detail : mDetails) {
                time += detail.getLong(TDetail.duration);
            }
            mBarTvRight.setText(getString(R.string.video_studied));
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    @Override
    public void onAdapterClick(int position, View v) {
        startActivity(VideoActivity.class);
    }
}
