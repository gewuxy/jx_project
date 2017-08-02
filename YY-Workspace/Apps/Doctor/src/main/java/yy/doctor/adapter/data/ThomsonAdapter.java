package yy.doctor.adapter.data;

import android.view.View;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.data.DataVH;
import yy.doctor.model.data.ThomsonDetail;
import yy.doctor.model.data.ThomsonDetail.TThomsonDetail;
import yy.doctor.ui.activity.me.DownloadDataActivityIntent;
import yy.doctor.util.CacheUtil;

/**
 * 汤森路透的adapter
 *
 * @author CaiXiang
 * @since 2017/4/27
 */
public class ThomsonAdapter extends AdapterEx<ThomsonDetail, DataVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_data_item;
    }

    @Override
    protected void refreshView(int position, DataVH holder) {

        if (position != 0) {
            goneView(holder.getDivider());
        }

        ThomsonDetail item = getItem(position);
        holder.getTvName().setText(getItem(position).getString(TThomsonDetail.title));
        String size = item.getLong(TThomsonDetail.fileSize) + "K";
        holder.getTvDetail().setText(size);

        setOnViewClickListener(position, holder.getDataItemLayout());
    }


    @Override
    protected void onViewClick(int position, View v) {
        ThomsonDetail item = getItem(position);
        String filePath = CacheUtil.getThomsonCacheDir(item.getString(TThomsonDetail.categoryId));
        long fileSize = item.getInt(TThomsonDetail.fileSize) * 1024;
        String fileName = item.getString(TThomsonDetail.title);
        String url = item.getString(TThomsonDetail.filePath);

        DownloadDataActivityIntent.create(
                "pdf",
                fileSize,
                url,
                fileName,
                filePath
        ).start(getContext());
    }

}

