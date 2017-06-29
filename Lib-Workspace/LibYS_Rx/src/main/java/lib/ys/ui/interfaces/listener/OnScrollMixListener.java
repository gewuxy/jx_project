package lib.ys.ui.interfaces.listener;

import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 能让ListView和RecyclerView都共用的一个监听类
 *
 * @author yuansui
 */
public class OnScrollMixListener extends RecyclerView.OnScrollListener implements OnScrollListener {

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    }
}
