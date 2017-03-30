package lib.ys.config;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.view.swipeRefresh.footer.BaseFooter;
import lib.ys.view.swipeRefresh.header.BaseHeader;

/**
 * @author yuansui
 */
public class ListConfig {

    /**
     * 列表的加载更多数据时的翻页规则设置
     *
     * @author yuansui
     */
    @IntDef({
            PageDownType.offset,
            PageDownType.page,
            PageDownType.last_item_id,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PageDownType {
        int offset = 0;
        int page = 1;
        int last_item_id = 2;
    }

    @PageDownType
    private static int mListPageDownType = PageDownType.offset; // 默认根据个数偏移

    private static Class<? extends BaseHeader> mHeaderClz = null;
    private static Class<? extends BaseFooter> mFooterClz = null;

    @PageDownType
    public static int getType() {
        return mListPageDownType;
    }

    public static void type(@PageDownType int type) {
        mListPageDownType = type;
    }

    public static void headerClz(Class<? extends BaseHeader> clz) {
        mHeaderClz = clz;
    }

    public static Class<? extends BaseHeader> getHeaderClz() {
        return mHeaderClz;
    }

    public static Class<? extends BaseFooter> getFooterClz() {
        return mFooterClz;
    }

    public static void footerClz(Class<? extends BaseFooter> clz) {
        mFooterClz = clz;
    }
}
