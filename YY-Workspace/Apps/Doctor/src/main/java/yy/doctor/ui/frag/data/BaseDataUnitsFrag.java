package yy.doctor.ui.frag.data;

import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import inject.annotation.router.Arg;
import lib.ys.model.FileSuffix;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.R;
import yy.doctor.adapter.data.DataUnitAdapter;
import yy.doctor.model.data.DataUnit;
import yy.doctor.model.data.DataUnit.FileOpenType;
import yy.doctor.model.data.DataUnit.TDataUnit;
import yy.doctor.network.NetworkAPISetter.DataAPI;
import yy.doctor.ui.activity.data.BaseDataUnitsActivity;
import yy.doctor.ui.activity.data.ClinicsActivityRouter;
import yy.doctor.ui.activity.data.DataUnitDetailActivityRouter;
import yy.doctor.ui.activity.data.DataUnitsSearchActivityRouter;
import yy.doctor.ui.activity.data.DownloadFileActivityRouter;
import yy.doctor.ui.activity.data.DrugsActivityRouter;
import yy.doctor.util.CacheUtil;

/**
 * @auther Huoxuyu
 * @since 2017/8/3
 */

abstract public class BaseDataUnitsFrag extends BaseSRListFrag<DataUnit, DataUnitAdapter> {

    @IntDef({
            DataType.un_know,
            DataType.thomson,
            DataType.drug,
            DataType.clinic,
            DataType.meeting,
    })
    public @interface DataType {
        int un_know = -1; // 未知
        int meeting = 0; // 代表会议
        int thomson = 1; // 汤森
        int drug = 2; // 药品目录
        int clinic = 3; // 代表临床
    }

    private TextView mTvSearch;

    @Arg
    String mPreId;

    @Arg
    boolean mLeaf;

    @CallSuper
    @Override
    public void initData() {
    }

    @Override
    public final void initNavBar(NavBar bar) {
    }

    @Nullable
    @Override
    public int getContentHeaderViewId() {
        return R.layout.layout_data_header;
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvSearch = findView(R.id.data_header_tv_search);
    }

    @Override
    public void setViews() {
        super.setViews();

        if (getContentHeaderViewId() != 0) {
            setOnClickListener(R.id.data_header_search_layout);
            mTvSearch.setText(getSearchId());
        }

        setOnAdapterClickListener((position, v) -> {
            DataUnit item = getItem(position);
            if (!item.getBoolean(TDataUnit.isFile)) {
                /**
                 * 拼接path
                 * 第一级环境为{@link yy.doctor.ui.activity.MainActivity}
                 * 其他的都是 {@link BaseDataUnitsActivity}
                 */
                String path = null;
                if (getActivity() instanceof BaseDataUnitsActivity) {
                    BaseDataUnitsActivity activity = (BaseDataUnitsActivity) getActivity();
                    path = activity.buildPath(item.getString(TDataUnit.title));
                } else {
                    path = item.getString(TDataUnit.title);
                }

                switch (getDataType()) {
                    case DataType.drug: {
                        DrugsActivityRouter
                                .create()
                                .id(item.getString(TDataUnit.id))
                                .fileName(item.getString(TDataUnit.title))
                                .leaf(item.getBoolean(TDataUnit.leaf))
                                .path(path)
                                .route(getContext());
                    }
                    break;
                    case DataType.clinic: {
                        ClinicsActivityRouter.create()
                                .id(item.getString(TDataUnit.id))
                                .fileName(item.getString(TDataUnit.title))
                                .leaf(item.getBoolean(TDataUnit.leaf))
                                .path(path)
                                .route(getContext());
                    }
                    break;
                }

            } else {
                switch (item.getInt(TDataUnit.openType)) {
                    case FileOpenType.details: {
                        String dataFileId = item.getString(TDataUnit.id);
                        String fileName = item.getString(TDataUnit.title);
                        DataUnitDetailActivityRouter.create(
                                dataFileId,
                                fileName,
                                getDataType()
                        ).route(getContext());
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
                                .fileSuffix(FileSuffix.pdf)
                                .dataType(getDataType())
                                .fileSize(fileSize)
                                .dataFileId(dataFileId)
                                .route(getContext());
                    }
                    break;
                    case FileOpenType.html: {

                    }
                    break;
                }

            }
        });
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(DataAPI.units(mPreId, getDataType(), mLeaf, getOffset(), getLimit()).build());
    }

    @DataType
    abstract protected int getDataType();

    @StringRes
    protected int getSearchId() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.data_header_search_layout) {
            DataUnitsSearchActivityRouter.create(getDataType()).route(getContext());
        }
    }
}
