package jx.csp.adapter.share;

import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.adapter.VH.share.EditorVH;
import jx.csp.model.editor.Theme;
import jx.csp.model.editor.Theme.TTheme;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.network.image.shape.CornerRenderer;

/**
 * @auther HuoXuYu
 * @since 2018/2/1
 */

public class EditorAdapter extends RecyclerAdapterEx<Theme, EditorVH> {

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

}
