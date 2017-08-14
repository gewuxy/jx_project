package yy.doctor.ui.activity.data;

import android.view.View;
import android.widget.EditText;

import inject.annotation.router.Route;
import inject.annotation.router.Arg;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.Constants.FileSuffix;
import yy.doctor.R;
import yy.doctor.adapter.data.DataUnitAdapter;
import yy.doctor.model.data.DataUnit;
import yy.doctor.model.data.DataUnit.FileOpenType;
import yy.doctor.model.data.DataUnit.TDataUnit;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import yy.doctor.util.CacheUtil;
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
        bar.addViewLeft(view, null);
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
            search();
        });
    }

    @Override
    public void findViews() {
        super.findViews();

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

        if (!TextUtil.isEmpty(mSearchContent)) {
            mEtSearch.setText(mSearchContent);
            search();
        } else {
            searchEmpty();
        }

        setOnAdapterClickListener((position, v) -> {
            DataUnit item = getItem(position);
            switch (item.getInt(TDataUnit.openType)) {
                case FileOpenType.details: {
                    String dataFileId = item.getString(TDataUnit.id);
                    String fileName = item.getString(TDataUnit.title);
                    DataUnitDetailActivityRouter.create(
                            dataFileId, fileName, mType
                    )
                            .route(this);
                }
                break;
                case FileOpenType.pdf: {
                    String filePath = CacheUtil.getThomsonCacheDir(item.getString(TDataUnit.id));
                    long fileSize = item.getInt(TDataUnit.fileSize) * 1024;
                    String fileName = item.getString(TDataUnit.title);
                    String url = item.getString(TDataUnit.filePath);
                    String dataFileId = item.getString(TDataUnit.id);

                    DownloadFileActivityRouter.create()
                            .filePath(filePath)
                            .fileName(fileName)
                            .url(url)
                            .fileSuffix(FileSuffix.KPdf)
                            .dataType(mType)
                            .fileSize(fileSize)
                            .dataFileId(dataFileId)
                            .route(this);
                }
                break;
            }
        });
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.drugSearch(mSearchContent, mType, getOffset(), getLimit()));
    }

    protected void searchEmpty() {
    }

    private void search() {
        removeAll(); // 搜索前清空之前的数据集
        invalidate();

        refresh(RefreshWay.embed);
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
