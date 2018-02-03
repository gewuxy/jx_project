package jx.csp.adapter.main;

import jx.csp.R;
import jx.csp.adapter.VH.main.PhotoVH;
import jx.csp.model.main.Photo;
import lib.ys.adapter.recycler.RecyclerAdapterEx;

/**
 * 相册的Adapter
 *
 * @auther : GuoXuan
 * @since : 2018/2/2
 */
public class PhotoAdapter extends RecyclerAdapterEx<Photo, PhotoVH> {

    public static final int KSpanCount = 4;

    @Override
    protected void refreshView(int position, PhotoVH holder) {
        holder.getIvPic()
                .placeHolder(R.drawable.bg_default_iv_photo)
                .storage(getItem(position).getString(Photo.TPhoto.path))
                .resize(fit(88), fit(88))
                .load();

        holder.getIvSelect()
                .setSelected(getItem(position).getBoolean(Photo.TPhoto.choice, false));

        if (getItem(position).getBoolean(Photo.TPhoto.cover, false)) {
            showView(holder.getIvCover());
        } else {
            goneView(holder.getIvCover());
        }

        setOnViewClickListener(position, holder.getIvPic());
        setOnViewClickListener(position, holder.getIvCover()); // 阻挡点击效果
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_photo_item;
    }
}
