package lib.ys.adapter;


import lib.ys.adapter.interfaces.IViewHolder;

abstract public class AdapterEx<T, VH extends IViewHolder> extends MultiAdapterEx<T, VH> {

    @Override
    public final int getConvertViewResId(int itemType) {
        return getConvertViewResId();
    }

    @Override
    protected final void initView(int position, VH holder, int itemType) {
        initView(position, holder);
    }

    protected void initView(int position, VH holder) {
    }

    @Override
    protected final void refreshView(int position, VH holder, int itemType) {
        refreshView(position, holder);
    }

    @Override
    public final int getItemViewType(int position) {
        return 0;
    }

    @Override
    public final int getViewTypeCount() {
        return 1;
    }

    abstract public int getConvertViewResId();

    abstract protected void refreshView(int position, VH holder);

}
