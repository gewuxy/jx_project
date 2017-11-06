package lib.platform.model;

import inject.annotation.builder.Builder;

/**
 * @auther yuansui
 * @since 2017/11/6
 */
@Builder
public class AuthParams {

    String mGender;
    String mIcon;
    String mId;
    String mName;

    protected AuthParams() {
    }

    public String getGender() {
        return mGender;
    }

    public String getIcon() {
        return mIcon;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public static AuthParamsBuilder newBuilder() {
        return AuthParamsBuilder.create();
    }
}
