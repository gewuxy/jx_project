package yy.doctor.ui.frag.data;

import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import lib.processor.annotation.Arg;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.Constants.FileSuffix;
import yy.doctor.R;
import yy.doctor.adapter.data.DataUnitAdapter;
import yy.doctor.model.data.DataUnit;
import yy.doctor.model.data.DataUnit.FileOpenType;
import yy.doctor.model.data.DataUnit.TDataUnit;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.data.BaseDataUnitsActivity;
import yy.doctor.ui.activity.data.ClinicsActivityIntent;
import yy.doctor.ui.activity.data.DataUnitDetailActivityIntent;
import yy.doctor.ui.activity.data.DataUnitsSearchActivityIntent;
import yy.doctor.ui.activity.data.DownloadFileActivityIntent;
import yy.doctor.ui.activity.data.DrugsActivityIntent;
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
                        DrugsActivityIntent.create()
                                .id(item.getString(TDataUnit.id))
                                .fileName(item.getString(TDataUnit.title))
                                .leaf(item.getBoolean(TDataUnit.leaf))
                                .path(path)
                                .start(getContext());
                    }
                    break;
                    case DataType.clinic: {
                        ClinicsActivityIntent.create()
                                .id(item.getString(TDataUnit.id))
                                .fileName(item.getString(TDataUnit.title))
                                .leaf(item.getBoolean(TDataUnit.leaf))
                                .path(path)
                                .start(getContext());
                    }
                    break;
                }

            } else {
                switch (item.getInt(TDataUnit.openType)) {
                    case FileOpenType.details: {
                        String dataFileId = item.getString(TDataUnit.id);
                        String fileName = item.getString(TDataUnit.title);
                        DataUnitDetailActivityIntent.create(
                                dataFileId,
                                fileName,
                                getDataType()
                        ).start(getContext());
                    }
                    break;
                    case FileOpenType.pdf: {
                        String filePath = CacheUtil.getThomsonCacheDir(item.getString(TDataUnit.id));
                        long fileSize = item.getInt(TDataUnit.fileSize) * 1024;
                        String fileName = item.getString(TDataUnit.title);
                        String url = item.getString(TDataUnit.filePath);
                        String dataFileId = item.getString(TDataUnit.id);

                        DownloadFileActivityIntent.create()
                                .filePath(filePath)
                                .fileName(fileName)
                                .url(url)
                                .fileSuffix(FileSuffix.KPdf)
                                .dataType(getDataType())
                                .fileSize(fileSize)
                                .dataFileId(dataFileId)
                                .start(getContext());
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
        exeNetworkReq(NetFactory.dataUnits(mPreId, getDataType(), mLeaf, getOffset(), getLimit()));
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
            DataUnitsSearchActivityIntent.create(getDataType()).start(getContext());
        }
    }
}
