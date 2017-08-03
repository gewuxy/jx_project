package yy.doctor.ui.activity.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.data.DrugCategoryListAdapter;
import yy.doctor.model.data.DrugCategoryData;
import yy.doctor.model.data.DrugCategoryData.TCategoryData;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @auther Huoxuyu
 * @since 2017/8/2
 */

public class DataUnitsActivity extends BaseSRListActivity<DrugCategoryData, DrugCategoryListAdapter> {
    private final int KDrugCategory = 0;

    private EditText mEtPath;
    private String mId;
    private String mFileName;


    public static void nav(Context context, String id, String fileName, boolean leaf) {
        Intent i = new Intent(context, DataUnitsActivity.class)
                .putExtra(Extra.KId, id)
                .putExtra(Extra.KName, fileName)
                .putExtra(Extra.KLeaf, leaf);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mId = getIntent().getStringExtra(Extra.KId);
        mFileName = getIntent().getStringExtra(Extra.KName);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.drug_list, this);
        bar.addViewRight(R.mipmap.nav_bar_ic_data, v -> this.notify(NotifyType.data_finish));
    }

    @Nullable
    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_data_path);
    }

    @Override
    public void findViews() {
        super.findViews();

        mEtPath = findView(R.id.data_path_et);
    }

    @Override
    public void setViews() {
        super.setViews();

        mEtPath.setText(mFileName + " > ");
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(KDrugCategory, NetFactory.drugAllFile(mId, getOffset(), getLimit()));
    }

    @Override
    public int getLimit() {
        return 500;
    }

    @Override
    public void onItemClick(View v, int position) {
        DrugCategoryData item = getItem(position);
        String dataFileId = item.getString(TCategoryData.isFile);
        String fileName = item.getString(TCategoryData.isFile);
        boolean leaf = item.getBoolean(TCategoryData.isFile);
        DataUnitsActivity.nav(this, dataFileId, fileName, leaf);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.data_finish) {
            finish();
        }
    }

}
