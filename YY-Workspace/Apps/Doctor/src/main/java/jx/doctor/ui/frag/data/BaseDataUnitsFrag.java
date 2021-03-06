package jx.doctor.ui.frag.data;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import inject.annotation.router.Arg;
import lib.ys.ui.other.NavBar;
import lib.jx.ui.frag.base.BaseSRListFrag;
import jx.doctor.R;
import jx.doctor.adapter.data.DataUnitAdapter;
import jx.doctor.model.data.DataUnit;
import jx.doctor.model.data.DataUnit.TDataUnit;
import jx.doctor.network.NetworkApiDescriptor.DataAPI;
import jx.doctor.ui.activity.data.BaseDataUnitsActivity;
import jx.doctor.ui.activity.data.ClinicsActivityRouter;
import jx.doctor.ui.activity.data.DataUnitsSearchActivityRouter;
import jx.doctor.ui.activity.data.DrugsActivityRouter;
import jx.doctor.util.UISetter;

/**
 * 数据中心和收藏的Frag
 *
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
    @Retention(RetentionPolicy.SOURCE)
    public @interface DataType {
        int un_know = -1; // 未知
        int meeting = 0; // 代表会议
        int thomson = 1; // 汤森
        int drug = 2; // 药品目录
        int clinic = 3; // 代表临床
    }

    @IntDef({
            DataFrom.data,
            DataFrom.collection,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DataFrom {
        int data = 0;  // 来自数据中心
        int collection = 1;  // 来自收藏
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
            setOnClickListener(getHeaderView());
            if (mPreId == null) {
                mTvSearch.setText(getSearchId());
            } else {
                goneView(getHeaderView());
            }
        }

        setOnAdapterClickListener((position, v) -> {
            DataUnit item = getItem(position);
            if (!item.getBoolean(TDataUnit.isFile)) {
                /**
                 * 拼接path
                 * 第一级环境为{@link jx.doctor.ui.activity.MainActivity}
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
                UISetter.onDataUnitClick(item, getDataType(), getContext());
            }
        });

        getAdapter().setDataType(getDataType(), DataFrom.data);
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
        DataUnitsSearchActivityRouter.create(getDataType()).route(getContext());
    }
}
