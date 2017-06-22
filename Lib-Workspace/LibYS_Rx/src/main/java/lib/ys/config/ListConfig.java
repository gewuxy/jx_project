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
            PageDownType.last_id,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PageDownType {
        int offset = 0;
        int page = 1;
        int last_id = 2;
    }

    @PageDownType
    private int mType = PageDownType.offset; // 默认根据个数偏移

    private Class<? extends BaseHeader> mHeaderClz = null;
    private Class<? extends BaseFooter> mFooterClz = null;

    public static Builder newBuilder() {
        return new Builder();
    }

    @PageDownType
    public int getType() {
        return mType;
    }

    public Class<? extends BaseHeader> getHeaderClz() {
        return mHeaderClz;
    }

    public Class<? extends BaseFooter> getFooterClz() {
        return mFooterClz;
    }


    public static class Builder {

        @PageDownType
        private int mType = PageDownType.offset; // 默认根据个数偏移

        private Class<? extends BaseHeader> mHeaderClz = null;
        private Class<? extends BaseFooter> mFooterClz = null;

        public Builder type(@PageDownType int type) {
            mType = type;
            return this;
        }

        public Builder footerClz(Class<? extends BaseFooter> clz) {
            return this;
        }

        public Builder headerClz(Class<? extends BaseHeader> clz) {
            return this;
        }

        public ListConfig build() {
            ListConfig config = new ListConfig();

            config.mType = mType;
            config.mHeaderClz = mHeaderClz;
            config.mFooterClz = mFooterClz;

            return config;
        }
    }
}
