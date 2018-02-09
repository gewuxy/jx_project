package jx.csp.adapter.share;

import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.adapter.VH.share.EditorVH;
import jx.csp.constant.Constants;
import jx.csp.model.editor.Theme;
import jx.csp.model.editor.Theme.TTheme;
import lib.ys.ConstantsEx;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.network.image.shape.CornerRenderer;

/**
 * @auther HuoXuYu
 * @since 2018/2/1
 */

public class EditorAdapter extends RecyclerAdapterEx<Theme, EditorVH> {

    private int mLastPosition = Constants.KInvalidValue;

    @Override
    protected void initView(int position, EditorVH holder, int itemType) {
        if (getItem(position).getBoolean(TTheme.select)) {
            mLastPosition = position;
        }
    }

    @Override
    protected void refreshView(int position, EditorVH holder) {
        Theme item = getItem(position);

        View layout = holder.getItemLayout();
        layout.setSelected(item.getBoolean(TTheme.select));

        holder.getItemIv().placeHolder(R.drawable.ic_default_main_grid)
                .url(item.getString(TTheme.imgUrl))
                .renderer(new CornerRenderer())
                .load();

        TextView text = holder.getItemText();
        if (layout.isSelected()) {
            showView(text);
        } else {
            hideView(text);
        }

        setOnViewClickListener(position, layout);
        setOnViewClickListener(position, text);
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_editor_theme_item;
    }

    @Override
    protected void onViewClick(int position, View v) {
        switch (v.getId()) {
            case R.id.editor_theme_layout: {
                if (mLastPosition != position) {
                    if (mLastPosition != ConstantsEx.KInvalidValue) {
                        //再次点击取消
                        getItem(mLastPosition).put(Theme.TTheme.select, false);
                        invalidate(mLastPosition);
                    }
                    getItem(position).put(Theme.TTheme.select, true);
                    mLastPosition = position;
                } else {
                    boolean select = !getItem(position).getBoolean(Theme.TTheme.select);
                    getItem(position).put(Theme.TTheme.select, select);
                }
                invalidate(position);
            }
            break;
        }
    }

    public void setLastPosition(int lastPosition) {
        mLastPosition = lastPosition;
    }

}
