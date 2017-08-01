package yy.doctor.ui.activity.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.data.DrugListAdapter;
import yy.doctor.util.Util;

/**
 *
 * 药品二级目录分类
 *
 * @auther HuoXuYu
 * @since 2017/7/25
 */

public class DrugListCategoryTwoLevelActivity extends BaseListActivity<String, DrugListAdapter> {

    private EditText mEtPath;
    private String mPath;

    public static void nav(Context context, String path) {
        Intent i = new Intent(context, DrugListCategoryTwoLevelActivity.class);
        i.putExtra(Extra.KData, path);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mPath = getIntent().getStringExtra(Extra.KData);
        for (int i = 0; i < 10; ++i) {
            addItem(i + "");
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.drug_list, this);
        bar.addViewRight(R.mipmap.nav_bar_ic_data, v -> DrugListCategoryTwoLevelActivity.this.notify(NotifyType.data_finish));
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
    public void onItemClick(View v, int position) {
        startActivity(DrugListCategoryThreeLevelActivity.class);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.data_finish) {
            finish();
        }
    }

}
