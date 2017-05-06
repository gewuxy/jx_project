package lib.ys.adapter;

import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;

import lib.ys.adapter.interfaces.IViewHolder;

abstract public class ViewHolderEx implements IViewHolder {

    private View mConvertView;

    private SparseArray<View> mMap;

    public ViewHolderEx(@NonNull View convertView) {
        if (convertView == null) {
            throw new IllegalStateException("convertView can not be null");
        }
        mConvertView = convertView;
        mMap = new SparseArray<>();
    }

    @Override
    public View getConvertView() {
        return mConvertView;
    }

    @Override
    public <T extends View> T findView(int id) {
        return (T) mConvertView.findViewById(id);
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
