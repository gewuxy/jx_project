package jx.csp.ui.activity.me.vip;

import android.support.v7.widget.GridLayoutManager;

import jx.csp.R;
import jx.csp.constant.LangType;
import jx.csp.sp.SpApp;

/**
 * @auther Huoxuyu
 * @since 2017/12/8
 */
public class VipManageActivity extends BaseVipManageActivity {

    @Override
    public void setViews() {
        super.setViews();
        if (LangType.cn_simplified == SpApp.inst().getLangType()) {
            switch (mPackageId) {
                case KNorm: {
                    mIvCard.setImageResource(R.drawable.vip_ic_norm_card_cn);
                }
                break;
                case KAdvanced: {
                    mIvCard.setImageResource(R.drawable.vip_ic_advanced_card_cn);
                }
                break;
                case KProfession: {
                    mIvCard.setImageResource(R.drawable.vip_ic_profession_card_cn);
                }
                break;
            }
        }else {
            switch (mPackageId) {
                case KNorm: {
                    mIvCard.setImageResource(R.drawable.vip_ic_norm_card_tw);
                }
                break;
                case KAdvanced: {
                    mIvCard.setImageResource(R.drawable.vip_ic_advanced_card_tw);
                }
                break;
                case KProfession: {
                    mIvCard.setImageResource(R.drawable.vip_ic_profession_card_tw);
                }
                break;
            }
        }
    }

    @Override
    protected void getPermissionLayout() {
        mRvPermission.setLayoutManager(new GridLayoutManager(this, 4));
    }
}
