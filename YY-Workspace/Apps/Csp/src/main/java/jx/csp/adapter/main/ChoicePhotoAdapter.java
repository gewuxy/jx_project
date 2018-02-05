package jx.csp.adapter.main;

import jx.csp.R;
import jx.csp.adapter.VH.main.ChoicePhotoVH;
import jx.csp.constant.UploadType;
import jx.csp.model.main.photo.ChoicePhoto;
import jx.csp.model.main.photo.IUpload;
import lib.ys.adapter.recycler.MultiRecyclerAdapterEx;
import lib.ys.network.image.shape.CornerRenderer;

/**
 * @auther : GuoXuan
 * @since : 2018/2/1
 */
public class ChoicePhotoAdapter extends MultiRecyclerAdapterEx<IUpload, ChoicePhotoVH> {

    public static final int KSpanCount = 2;

    @Override
    protected void refreshView(int position, ChoicePhotoVH holder, int itemType) {
        // 处理底部的分割线
        if (Math.ceil((position + 1) / (float) KSpanCount) == Math.ceil(getCount() / (float) KSpanCount)) {
            showView(holder.getDividerBottom());
        } else {
            goneView(holder.getDividerBottom());
        }
        if (itemType == UploadType.photo) {
            ChoicePhoto item = (ChoicePhoto) getItem(position);
            holder.getIvPhoto()
                    .placeHolder(R.drawable.bg_default_iv_photo)
                    .storage(item.getString(ChoicePhoto.TChoicePhoto.path))
                    .renderer(new CornerRenderer(fit(10)))
                    .load();

            setOnViewClickListener(position, holder.getIvDelete());
        } else {
            setOnViewClickListener(position, holder.getLayoutAdd());
        }
    }

    @Override
    public int getConvertViewResId(int itemType) {
        if (itemType == UploadType.photo) {
            return R.layout.layout_choice_photo_item_photo;
        } else {
            return R.layout.layout_choice_photo_item_camera;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

}
