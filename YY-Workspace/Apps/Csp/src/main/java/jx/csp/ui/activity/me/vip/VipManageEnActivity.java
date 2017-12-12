package jx.csp.ui.activity.me.vip;

import android.support.v7.widget.GridLayoutManager;

import jx.csp.R;

/**
 * @auther Huoxuyu
 * @since 2017/12/11
 */

public class VipManageEnActivity extends BaseVipManageActivity{

    @Override
    public void setViews() {
        super.setViews();
        switch (mPackageId) {
            case KNorm: {
                mIvCard.setImageResource(R.drawable.vip_ic_norm_card);
            }
            break;
            case KAdvanced: {
                mIvCard.setImageResource(R.drawable.vip_ic_advanced_card);
            }
            break;
            case KProfession: {
                mIvCard.setImageResource(R.drawable.vip_ic_profession_card);
            }
            break;
        }
    }

    @Override
    protected void getPermissionLayout() {
        mRvPermission.setLayoutManager(new GridLayoutManager(this, 3));
    }

}
