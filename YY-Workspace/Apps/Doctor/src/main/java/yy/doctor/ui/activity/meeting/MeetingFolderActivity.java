package yy.doctor.ui.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.interfaces.IListResult;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.ListResult;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingAdapter;
import yy.doctor.model.home.RecMeetingFolder;
import yy.doctor.model.home.RecMeetingFolder.TRecMeetingFolder;
import yy.doctor.model.meet.IMeet;
import yy.doctor.model.meet.Meet;
import yy.doctor.model.meet.Meet.TMeet;
import yy.doctor.model.meet.Meeting;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

public class MeetingFolderActivity extends BaseSRListActivity<IMeet, MeetingAdapter> {

    private RecMeetingFolder folder;
    private String mInfinityId;
    private String mTitle;

    private TextView mTvNum;
    private TextView mTvTitle;
    private final int KFolder = 0;
    private final int KFolderResource = 1;

    public static void nav(Context context, RecMeetingFolder folder, String infinityId, String title) {
        Intent i = new Intent(context, MeetingFolderActivity.class)
                .putExtra(Extra.KData, folder)
                .putExtra(Extra.KId, infinityId)
                .putExtra(Extra.KTitle, title);
        LaunchUtil.startActivity(context, i);
    }

    @Nullable
    public View createHeaderView() {
        return inflate(R.layout.layout_meeting_folder_header);
    }

    @Override
    public void initData() {
        folder = (RecMeetingFolder) getIntent().getSerializableExtra(Extra.KData);
        mInfinityId = getIntent().getStringExtra(Extra.KId);
        mTitle = getIntent().getStringExtra(Extra.KTitle);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "会议文件夹", this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvTitle = findView(R.id.meeting_folder_header_tv_title);
        mTvNum = findView(R.id.meeting_folder_header_tv_meeting_num);
    }

    @Override
    public void getDataFromNet() {
        if (TextUtil.isEmpty(mInfinityId)) {
            exeNetworkReq(KFolder, NetFactory.meetFolder(folder.getString(TRecMeetingFolder.id)));
        } else {
            exeNetworkReq(KFolderResource, NetFactory.folderResource(mInfinityId));
        }
    }


    @Override
    public IListResult<IMeet> parseNetworkResponse(int id, String text) throws JSONException {
        ListResult result = new ListResult();
        if (id == KFolder) {
            Result<Meet> r = JsonParser.ev(text, Meet.class);
            if (r.isSucceed()) {
                Meet meet = r.getData();
                List<IMeet> meets = new ArrayList<>();
                meets.addAll(meet.getList(TMeet.infinityTreeList));
                meets.addAll(meet.getList(TMeet.meetInfoDTOList));
                result.setData(meets);
            }
        } else {
            result = JsonParser.evs(text, Meeting.class);
        }
        return result;
    }

    @Override
    public void setViews() {
        super.setViews();

        if (TextUtil.isEmpty(mTitle)) {
            mTitle = folder.getString(TRecMeetingFolder.infinityName);
        }
        mTvTitle.setText(mTitle);
        mTvNum.setText(String.format("%d个会议", folder.getString(TRecMeetingFolder.meetCount)));
        getAdapter().hideUnitNum();
    }

    @Override
    public View createEmptyFooterView() {
        return null;
    }
}