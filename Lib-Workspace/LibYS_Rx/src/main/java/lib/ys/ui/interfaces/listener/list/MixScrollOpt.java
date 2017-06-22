package lib.ys.ui.interfaces.listener.list;

import android.view.View;

import java.util.List;

/**
 * 为了能让SRWidget共用RecyclerWidget和ListWidget而建立的一些共有方法
 *
 * @author yuansui
 */
public interface MixScrollOpt<T> {

    void addFooterView(View v);

    void showFooterView();

    void hideFooterView();

    void showHeaderView();

    void hideHeaderView();

    boolean isEmpty();

    void addEmptyViewIfNonNull();

    void setData(List<T> data);

    void addAll(List<T> data);

    void invalidate();

}
