package yy.doctor.activity.me.epc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.FormItemEx.TFormElem;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CornerRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.Notifier.NotifyType;
import lib.yy.activity.base.BaseFormActivity;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.model.me.Exchange;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * 兑换
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class ExchangeActivity extends BaseFormActivity {

    private NetworkImageView mIvGoods;
    private TextView mTvName;
    private TextView mTvEpn;
    private TextView mTvPayEpn;

    private int mGoodId;
    private String mGoodName;
    private int mEpn;
    private String mUrl;

    @IntDef({
            RelatedId.receiver,
            RelatedId.mobile,
            RelatedId.province_city,
            RelatedId.address,
    })
    private @interface RelatedId {
        int receiver = 0;
        int mobile = 1;
        int province_city = 2;
        int address = 3;
    }

    public static void nav(Context context, int goodId, String goodName, int epn, String url) {
        Intent i = new Intent(context, ExchangeActivity.class);
        i.putExtra(Extra.KData, goodId);
        i.putExtra(Extra.KName, goodName);
        i.putExtra(Extra.KNum, epn);
        i.putExtra(Extra.KUrl, url);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addBackIcon(R.mipmap.nav_bar_ic_back, "兑换", this);
    }

    @Override
    public void initData() {
        super.initData();

        Intent i = getIntent();
        mGoodName = i.getStringExtra(Extra.KName);
        mGoodId = i.getIntExtra(Extra.KData, 0);
        mEpn = i.getIntExtra(Extra.KNum, 0);
        mUrl = i.getStringExtra(Extra.KUrl);

        addItem(new Builder(FormType.divider_large).build());
        addItem(new Builder(FormType.et_register)
                .related(RelatedId.receiver)
                .hint("收货人")
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et_register)
                .related(RelatedId.mobile)
                .hint("手机号码")
                .build());

        addItem(new Builder(FormType.divider_large).build());
        addItem(new Builder(FormType.et_register)
                .related(RelatedId.province_city)
                .hint("省市")
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et_register)
                .related(RelatedId.address)
                .hint("详细地址")
                .build());
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
        mTvEpn.setText(mEpn + "象数");
        mTvPayEpn.setText(mEpn + "象数");

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
                //检查象数是否够
                if (Profile.inst().getInt(TProfile.credits) < mEpn) {
                    showToast("象数不足,无法兑换");
                    return;
                }
                refresh(RefreshWay.dialog);
                NetworkReq r = NetFactory.newExchangeBuilder()
                        .goodsId(mGoodId)
                        .price(mEpn)
                        .receiver(getRelatedItem(RelatedId.receiver).getString(TFormElem.text))
                        .phone(getRelatedItem(RelatedId.mobile).getString(TFormElem.text))
                        .province(getRelatedItem(RelatedId.province_city).getString(TFormElem.text))
                        .address(getRelatedItem(RelatedId.address).getString(TFormElem.text))
                        .builder();
                exeNetworkReq(r);
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Exchange.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        stopRefresh();
        Result<Exchange> r = (Result<Exchange>) result;
        if (r.isSucceed()) {
            showToast("兑换成功");
            int epn = Profile.inst().getInt(TProfile.credits) - mEpn;
            Profile.inst().update(Profile.inst().put(TProfile.credits, epn));
            notify(NotifyType.profile_change);
            startActivity(OrderActivity.class);
            finish();
        } else {
            showToast(r.getError());
        }
    }
}
