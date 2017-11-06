package lib.platform.model;

import inject.annotation.builder.Builder;

/**
 * @auther yuansui
 * @since 2017/11/6
 */
@Builder
public class ShareParams {
    String mTitle;
    String mUrl;
    String mText;
    String mImageUrl;

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getText() {
        return mText;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public static ShareParamsBuilder newBuilder() {
        return ShareParamsBuilder.create();
    }
}
