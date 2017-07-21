package lib.ys.ui.interfaces;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

/**
 * 可以滑动的
 *
 * @author yuansui
 */
public interface IScrollable<T> {

    void findViews(@NonNull View contentView,
                   @IdRes int scrollableId,
                   @Nullable View header,
                   @Nullable View footer,
                   @Nullable View empty);

    void setViews();

    <VIEW extends View> VIEW getScrollableView();

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

    void onDestroy();
}
