package lib.ys.adapter.recycler;

/**
 * @author yuansui
 */
abstract public class RecyclerAdapterEx<T, VH extends RecyclerViewHolderEx> extends MultiRecyclerAdapterEx<T, VH> {

    @Override
    protected final void refreshView(int position, VH holder, int itemType) {
        refreshView(position, holder);
    }

    @Override
    public final int getConvertViewResId(int itemType) {
        return getConvertViewResId();
    }

    @Override
    public final int getItemViewType(int position) {
        return 0;
    }

    abstract protected void refreshView(int position, VH holder);

    abstract protected int getConvertViewResId();
}
