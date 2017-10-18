package yy.doctor.ui.activity.data;

import android.view.View;
import android.widget.EditText;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.data.DataUnitAdapter;
import yy.doctor.model.data.DataUnit;
import yy.doctor.network.NetworkApiDescriptor.DataAPI;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;

/**
 * 药品搜索
 *
 * @author CaiXiang
 * @since 2017/7/14
 */
@Route
public class DataUnitsSearchActivity extends BaseSRListActivity<DataUnit, DataUnitAdapter> {

    protected String mSearchContent; // 搜索内容
    protected EditText mEtSearch; // 搜索框

    @Arg
    @DataType
    int mType;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);

        View view = inflate(R.layout.layout_meeting_nav_bar_search);
        mEtSearch = (EditText) view.findViewById(R.id.meeting_search_nav_bar_et);
        bar.addViewMid(view, null);

        bar.addTextViewRight(R.string.search, v -> {
            mSearchContent = mEtSearch.getText().toString().trim();
            if (TextUtil.isEmpty(mSearchContent)) {
                showToast(R.string.please_input_search_content);
                return;
            }
            // 键盘收取
            if (KeyboardUtil.isActive()) {
                KeyboardUtil.hideFromView(mEtSearch);
            }
            refresh();
        });
    }

    @Override
    public void setViews() {
        super.setViews();

        switch (mType) {
            case DataType.drug: {
                mEtSearch.setHint(R.string.drug_list_search_hint);
            }
            break;
            case DataType.clinic: {
                mEtSearch.setHint(R.string.clinical_guide_search_hint);
            }
            break;
        }

        setOnAdapterClickListener((position, v) -> UISetter.onDataUnitClick(getItem(position), mType, this));
        setRefreshEnabled(false);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(DataAPI.search(mSearchContent, mType, getOffset(), getLimit()).build());
    }

    @Override
    public void onNetRefreshError() {
        super.onNetRefreshError();
        setViewState(ViewState.error);
    }

    @Override
    protected String getEmptyText() {
        switch (mType) {
            case DataType.drug: {
                return "暂无相关药品";
            }
            case DataType.clinic: {
                return "暂无相关临床指南";
            }
            default:
                return super.getEmptyText();
        }
    }

    @Override
    public boolean enableInitRefresh() {
        return false;
    }
}
