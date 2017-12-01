package jx.doctor.adapter.me;

import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.util.TextUtil;
import jx.doctor.R;
import jx.doctor.adapter.VH.me.FileVH;
import jx.doctor.model.unitnum.File;
import jx.doctor.model.unitnum.File.TFile;

/**
 * 资料列表的adapter
 *
 * @author CaiXiang
 * @since 2017/5/3
 */
public class FileAdapter extends RecyclerAdapterEx<File, FileVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_file_data_item;
    }

    @Override
    protected void refreshView(int position, FileVH holder) {

        if (!TextUtil.isEmpty(getItem(position).getString(TFile.materialName))) {
            holder.getTv().setText(getItem(position).getString(TFile.materialName));
        } else {
            holder.getTv().setText(getItem(position).getString(TFile.name));
        }
    }

}
