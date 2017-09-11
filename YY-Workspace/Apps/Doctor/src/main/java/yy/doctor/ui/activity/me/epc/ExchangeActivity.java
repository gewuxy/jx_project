package yy.doctor.ui.activity.me.epc;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.OnFormObserver;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CornerRenderer;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.EpcAPI;
import yy.doctor.util.Util;

/**
 * 兑换
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
@Route
public class ExchangeActivity extends BaseFormActivity implements OnFormObserver {

    @Arg(opt = true)
    int mGoodId;
    @Arg(opt = true)
    String mGoodName;
    @Arg(opt = true)
    int mEpn;
    @Arg(opt = true)
    String mUrl;

    private NetworkImageView mIvGoods;
    private TextView mTvName;
    private TextView mTvEpn;
    private TextView mTvPayEpn;
    private TextView mTvExchange;

    @IntDef({
            RelatedId.receiver,
            RelatedId.mobile,
            RelatedId.province_city,
            RelatedId.address,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int receiver = 0;
        int mobile = 1;
        int province_city = 2;
        int address = 3;
    }

    private Set<Integer> mStatus;
    private int mEnableSize;

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.exchange, this);
    }

    @Override
    public void initData() {
        super.initData();

        mStatus = new HashSet<>();
        mEnableSize = RelatedId.class.getDeclaredFields().length;

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et)
                .related(RelatedId.receiver)
                .layout(R.layout.form_edit_no_text)
                .observer(this)
                .hint(R.string.receiver));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.et_phone_number)
                .related(RelatedId.mobile)
                .observer(this)
                .hint(R.string.phone_num));

        addItem(Form.create(FormType.divider_large));

        addItem(Form.create(FormType.et)
                .related(RelatedId.province_city)
                .layout(R.layout.form_edit_no_text)
                .observer(this)
                .hint(R.string.province_city));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.et)
                .related(RelatedId.address)
                .layout(R.layout.form_edit_no_text)
                .observer(this)
                .hint(R.string.address));
    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.layout_epc_item);
    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.layout_exchange_footer);
    }

    @Override
    public void findViews() {
        super.findViews();

        mIvGoods = findView(R.id.epc_item_iv);
        mTvName = findView(R.id.epc_item_tv_name);
        mTvEpn = findView(R.id.epc_item_tv_epn);
        mTvPayEpn = findView(R.id.exchange_tv);
        mTvExchange = findView(R.id.exchange_tv_btn);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvName.setText(mGoodName);
        mTvEpn.setText(mEpn + getString(R.string.epn));
        mTvPayEpn.setText(mEpn + getString(R.string.epn));

        mIvGoods.placeHolder(R.drawable.ic_default_epc)
                .renderer(new CornerRenderer(fitDp(3)))
                .url(mUrl)
                .load();

        setOnClickListener(R.id.exchange_tv_btn);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.exchange_tv_btn: {
                if (!check()) {
                    return;
                }

                //检查手机号格式是否正确
                if (!Util.isMobileCN(getRelatedItem(RelatedId.mobile).getText().trim())) {
                    showToast(R.string.phone_error);
                    return;
                }

                //检查象数是否够
                if (Profile.inst().getInt(TProfile.credits) < mEpn) {
                    showToast(R.string.no_enough_epn);
                    return;
                }

                refresh(RefreshWay.dialog);
                NetworkReq r = EpcAPI.exchange()
                        .goodsId(mGoodId)
                        .price(mEpn)
                        .receiver(getRelatedItem(RelatedId.receiver).getText())
                        .phone(getRelatedItem(RelatedId.mobile).getText())
                        .province(getRelatedItem(RelatedId.province_city).getText())
                        .address(getRelatedItem(RelatedId.address).getText())
                        .build();
                exeNetworkReq(r);
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            stopRefresh();

            showToast(R.string.exchange_success);
            int epn = Profile.inst().getInt(TProfile.credits) - mEpn;
            Profile.inst().put(TProfile.credits, epn);
            Profile.inst().saveToSp();
            notify(NotifyType.profile_change);
            startActivity(OrderActivity.class);
            finish();
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void callback(Object... params) {
        int related = (int) params[0];
        boolean valid = (boolean) params[1];

        if (valid) {
            if (!mStatus.contains(related)) {
                mStatus.add(related);
            }
        } else {
            mStatus.remove(related);
        }

        setBtnStatus();
    }

    /**
     * 根据填写的资料完成度设置兑换按钮是否可以点击
     */
    private void setBtnStatus() {
        if (mTvExchange == null) {
            return;
        }
        if (mStatus.size() == mEnableSize) {
            // 按钮可以点击
            mTvExchange.setEnabled(true);
        } else {
            // 按钮不能点击
            mTvExchange.setEnabled(false);
        }
    }

}
