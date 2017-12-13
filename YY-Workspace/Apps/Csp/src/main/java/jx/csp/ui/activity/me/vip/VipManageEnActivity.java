package jx.csp.ui.activity.me.vip;

import android.support.v7.widget.GridLayoutManager;

import jx.csp.R;

/**
 * @auther Huoxuyu
 * @since 2017/12/11
 */

public class VipManageEnActivity extends BaseVipManageActivity{

    @Override
    protected void setLayout() {
        mRvPermission.setLayoutManager(new GridLayoutManager(this, 3));
    }

    @Override
    protected void setVipCard(int id) {
        switch (id) {
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

}
