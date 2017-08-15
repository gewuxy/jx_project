package lib.ys.config;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import inject.annotation.builder.Builder;
import lib.ys.ConstantsEx.ListConstants;
import lib.ys.view.swipeRefresh.footer.BaseFooter;
import lib.ys.view.swipeRefresh.header.BaseHeader;

/**
 * @author yuansui
 */
@Builder
public class ListConfig {

    /**
     * 列表的加载更多数据时的翻页规则设置
     *
     * @author yuansui
     */
    @IntDef({
            PageDownType.offset,
            PageDownType.page,
            PageDownType.last_id,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PageDownType {
        int offset = 0;
        int page = 1;
        int last_id = 2;
    }

    @PageDownType
    int mType = PageDownType.offset; // 默认根据个数偏移

    int mInitOffset = ListConstants.KDefaultInitOffset;
    int mLimit = ListConstants.KDefaultLimit;

    Class<? extends BaseHeader> mHeaderClz = null;
    Class<? extends BaseFooter> mFooterClz = null;

    @PageDownType
    public int getType() {
        return mType;
    }

    public int getInitOffset() {
        return mInitOffset;
    }

    public int getLimit() {
        return mLimit;
    }

    public Class<? extends BaseHeader> getHeaderClz() {
        return mHeaderClz;
    }

    public Class<? extends BaseFooter> getFooterClz() {
        return mFooterClz;
    }
}
