package jx.doctor.ui.activity.me.unitnum;

import android.os.Bundle;
import android.view.View;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.ui.other.NavBar;
import lib.jx.ui.activity.base.BaseSRRecyclerActivity;
import jx.doctor.Extra.FileFrom;
import jx.doctor.R;
import jx.doctor.adapter.me.FileAdapter;
import jx.doctor.model.unitnum.File;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.network.NetworkApiDescriptor.UnitNumAPI;
import jx.doctor.util.Util;

/**
 * 资料列表
 *
 * @author CaiXiang
 * @since 2017/5/3
 */
@Route
public class FileActivity extends BaseSRRecyclerActivity<File, FileAdapter> {

    @Arg
    String mId;

    @Arg
    @FileFrom
    int mType;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.file_data, this);
    }

    @Override
    public void getDataFromNet() {
        if (mType == FileFrom.unit_num) {
            exeNetworkReq(UnitNumAPI.unitNumData(mId, getOffset(), getLimit()).build());
        } else {
            exeNetworkReq(MeetAPI.meetData(mId, getOffset(), getLimit()).build());
        }
    }

    @Override
    public void onSwipeRefreshAction() {
    }

    @Override
    public void onItemClick(View v, int position) {
        Util.openFile(getItem(position), mType, mId);
    }

}
