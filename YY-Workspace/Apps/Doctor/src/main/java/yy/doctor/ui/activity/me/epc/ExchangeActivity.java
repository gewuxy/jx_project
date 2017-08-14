package yy.doctor.ui.activity.me.epc;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import router.annotation.AutoIntent;
import router.annotation.Extra;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CornerRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.RegexUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 兑换
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
@AutoIntent
public class ExchangeActivity extends BaseFormActivity {

    private NetworkImageView mIvGoods;
    private TextView mTvName;
    private TextView mTvEpn;
    private TextView mTvPayEpn;

    @Extra
    int mGoodId;
    @Extra
    String mGoodName;
    @Extra
    int mEpn;
    @Extra
    String mUrl;

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

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.exchange, this);
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.divider_large));

        addItem(Form.create(FormType.et)
                .related(RelatedId.receiver)
                .layout(R.layout.form_edit_no_text)
                .hint(R.string.receiver));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.et_phone_number)
                .related(RelatedId.mobile)
                .hint(R.string.phone_num));

        addItem(Form.create(FormType.divider_large));

        addItem(Form.create(FormType.et)
                .related(RelatedId.province_city)
                .layout(R.layout.form_edit_no_text)
                .hint(R.string.province_city));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.et)
                .related(RelatedId.address)
                .layout(R.layout.form_edit_no_text)
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
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvName.setText(mGoodName);
        mTvEpn.setText(mEpn + getString(R.string.epn));
        mTvPayEpn.setText(mEpn + getString(R.string.epn));

        mIvGoods.placeHolder(R.mipmap.ic_default_epc)
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
                if (!RegexUtil.isMobileCN(getRelatedItem(RelatedId.mobile).getText())) {
                    showToast(R.string.phone_error);
                }

                //检查象数是否够
                if (Profile.inst().getInt(TProfile.credits) < mEpn) {
                    showToast(R.string.no_enough_epn);
                    return;
                }

                refresh(RefreshWay.dialog);
                NetworkReq r = NetFactory.newExchangeBuilder()
                        .goodsId(mGoodId)
                        .price(mEpn)
                        .receiver(getRelatedItem(RelatedId.receiver).getText())
                        .phone(getRelatedItem(RelatedId.mobile).getText())
                        .province(getRelatedItem(RelatedId.province_city).getText())
                        .address(getRelatedItem(RelatedId.address).getText())
                        .builder();
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

}
