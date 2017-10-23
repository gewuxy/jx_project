package yy.doctor.ui.activity;

import inject.annotation.router.Route;

/**
 * @auther : GuoXuan
 * @since : 2017/10/23
 */
@Route
public class AdWebViewActivity extends CommonWebViewActivity {

    @Override
    public void finish() {
        super.finish();

        AdActivity.afterAd(this);
    }
}
