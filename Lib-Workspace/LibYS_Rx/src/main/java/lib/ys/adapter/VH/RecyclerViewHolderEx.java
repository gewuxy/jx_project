package lib.ys.adapter.VH;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import android.view.View;

import lib.ys.adapter.interfaces.IViewHolder;

/**
 * RecycleView使用的机制跟原来的list完全不同, 需要一个单独的Ex
 *
 * @author yuansui
 */
public class RecyclerViewHolderEx extends ViewHolder implements IViewHolder {

    private View mBaseView;
    private SparseArray<View> mMap;

    public RecyclerViewHolderEx(View itemView) {
        super(itemView);

        mBaseView = itemView;
        mMap = new SparseArray<>();
    }

    @Override
    public View getConvertView() {
        return mBaseView;
    }

    @Override
    public <T extends View> T findView(int id) {
        return (T) mBaseView.findViewById(id);
    }

    @Override
    public <T extends View> T getView(int id) {
        View v = mMap.get(id);
        if (v == null) {
            v = findView(id);
            mMap.put(id, v);
        }

        return (T) v;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mMap.clear();
        mMap = null;
    }
}
