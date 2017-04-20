package lib.ys.widget.list.mix;

import android.view.View;

import java.util.List;

/**
 * 为了能让SRWidget共用RecyclerWidget和ListWidget而建立的一些共有方法
 *
 * @author yuansui
 */
public interface IMixScrollWidget<T> {

    void addFooterView(View v);

    void showFooterView();

    void hideFooterView();

    void showHeaderView();

    void hideHeaderView();

    boolean isEmpty();

    void addEmptyViewIfNoNull();

    void setData(List<T> data);

    void addAll(List<T> data);

    void invalidate();

}
