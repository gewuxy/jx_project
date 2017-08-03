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
import yy.doctor.model.data.ThomsonDetail;
import yy.doctor.util.Util;

/**
 * 药品三级目录分类
 *
 * @author CaiXiang
 * @since 2017/7/17
 */

public class DrugListCategoryLevel3Activity extends BaseSRListActivity<ThomsonDetail, DrugCategoryListAdapter> {

    private EditText mEtPath;
    private String mId;
    private String mFileName;

    public static void nav(Context context, String id, String fileName) {
        Intent i = new Intent(context, DrugListCategoryLevel3Activity.class)
                .putExtra(Extra.KId, id)
                .putExtra(Extra.KName, fileName);
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
        bar.addViewRight(R.mipmap.nav_bar_ic_data, v -> DrugListCategoryLevel3Activity.this.notify(NotifyType.data_finish));
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
    }

    @Override
    public void getDataFromNet() {

    }

    @Override
    public void onItemClick(View v, int position) {
        startActivity(DrugDetailActivity.class);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.data_finish) {
            finish();
        }
    }

}
