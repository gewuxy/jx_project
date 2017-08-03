package yy.doctor.ui.frag.data;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.R;
import yy.doctor.adapter.data.DrugListAdapter;
import yy.doctor.model.data.DrugCategoryData;
import yy.doctor.model.data.DrugCategoryData.TCategoryData;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.data.DataUnitsActivity;
import yy.doctor.ui.activity.data.DrugSearchActivity;

/**
 * @author CaiXiang
 * @since 2017/4/24
 */
public class DrugListFrag extends BaseSRListFrag<DrugCategoryData, DrugListAdapter> {

    private TextView mTvSearch;
    private boolean mLeaf = false;  //下一级是否是文件夹, 下一级为文件返回true,下一级是文件夹返回false.第一级传null或空字符串
    private boolean mFlag;
    private int mType = 1;  //type=0代表汤森,type=1代表药品目录，type=2代表临床
    private String mPreId;

    @Override
    public void initData() {
        mFlag = true;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Nullable
    @Override
    public View createHeaderView() {
        if (mFlag) {
            mFlag = false;
            return inflate(R.layout.layout_data_header);
        }
        return null;
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvSearch = findView(R.id.data_header_tv_search);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.data_header_search_layout);
        mTvSearch.setText(R.string.drug_list_search_hint);

    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.drugCategory(mPreId, mType, mLeaf,getOffset(), getLimit()));
    }

    @Override
    public int getLimit() {
        return 50;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.data_header_search_layout) {
            startActivity(DrugSearchActivity.class);
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        DrugCategoryData item = getItem(position);
        String dataFileId = item.getString(TCategoryData.isFolder);
        String fileName = item.getString(TCategoryData.isFolder);
        boolean leaf = item.getBoolean(TCategoryData.isFolder);
        DataUnitsActivity.nav(getContext(), dataFileId, fileName, leaf);
    }

    @Override
    public boolean enableInitRefresh() {
        return true;
    }

}
