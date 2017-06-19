package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TextUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.FileDataVH;
import yy.doctor.model.unitnum.FileData;
import yy.doctor.model.unitnum.FileData.TFileData;

/**
 * 资料列表的adapter
 *
 * @author CaiXiang
 * @since 2017/5/3
 */
public class FileDataAdapter extends AdapterEx<FileData, FileDataVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_file_data_item;
    }

    @Override
    protected void refreshView(int position, FileDataVH holder) {

        if ( !TextUtil.isEmpty(getItem(position).getString(TFileData.materialName)) ) {
            holder.getTv().setText(getItem(position).getString(TFileData.materialName));
        } else {
            holder.getTv().setText(getItem(position).getString(TFileData.name));
        }
    }

}
