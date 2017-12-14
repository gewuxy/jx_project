package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;

import jx.csp.R;
import jx.csp.constant.BindId;
import jx.csp.model.Profile;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.MainActivity;
import lib.network.model.interfaces.IResult;
import lib.platform.Platform;
import lib.platform.Platform.Type;

/**
 * @auther WangLan
 * @since 2017/11/6
 */
public class AuthLoginOverseaActivity extends BaseAuthLoginActivity {

    private final int KIdFaceBook = 3;
    private final int KIdTwitter = 4;


    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login_oversea;
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.login_facebook);
        setOnClickListener(R.id.login_twitter);
        setOnClickListener(R.id.login_mail);
        setOnClickListener(R.id.login_jx);
    }

    @Override
    protected int getVideoViewId() {
        return R.id.login_oversea_video_view;
    }

    @Override
    protected int getVideoBgId() {
        return R.id.login_oversea_video_bg;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.login_facebook: {
                Platform.auth(Type.facebook, newListener(KIdFaceBook, BindId.facebook));
            }
            break;
            case R.id.login_twitter: {
                Platform.auth(Type.twitter, newListener(KIdTwitter, BindId.twitter));
            }
            break;
            case R.id.login_jx: {
                startActivity(YaYaAuthorizeLoginActivity.class);
            }
            break;
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        super.onNetworkSuccess(id, r);
        if (id == KIdFaceBook || id == KIdTwitter) {
            if (r.isSucceed()) {
                Profile.inst().update((Profile) r.getData());
                SpUser.inst().updateProfileRefreshTime();
                startActivity(MainActivity.class);
                finish();
            } else {
                onNetworkError(id, r.getError());
            }
        }
    }
}
