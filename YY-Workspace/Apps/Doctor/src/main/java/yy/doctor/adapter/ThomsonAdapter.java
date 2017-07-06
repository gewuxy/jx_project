package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.ThomsonVH;
import yy.doctor.model.data.ThomsonDetail;
import yy.doctor.model.data.ThomsonDetail.TThomsonDetail;
import yy.doctor.ui.activity.me.DownloadDataActivity;
import yy.doctor.util.CacheUtil;

/**
 * 汤森路透的adapter
 *
 * @author CaiXiang
 * @since 2017/4/27
 */
public class ThomsonAdapter extends AdapterEx<ThomsonDetail, ThomsonVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_thomson_item;
    }

    @Override
    protected void refreshView(int position, ThomsonVH holder) {

        ThomsonDetail item = getItem(position);
        holder.getTv().setText(getItem(position).getString(TThomsonDetail.title));
        String size = item.getLong(TThomsonDetail.fileSize) + "K";
        holder.getTvSize().setText(size);

        String filePath = CacheUtil.getThomsonCacheDir(item.getString(TThomsonDetail.categoryId));
        long fileSize = item.getInt(TThomsonDetail.fileSize) * 1024;
        String fileName = item.getString(TThomsonDetail.title);
        String url = item.getString(TThomsonDetail.filePath);

        holder.getThomsonItemLayout().setOnClickListener(v ->
                DownloadDataActivity.nav(getContext(), filePath, fileName, url, "pdf", fileSize));
    }

}
