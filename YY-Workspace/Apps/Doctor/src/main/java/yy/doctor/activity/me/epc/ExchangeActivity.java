package yy.doctor.activity.me.epc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.FormEx.TFormElem;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CornerRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.RegexUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.form.Builder;
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
    @Retention(RetentionPolicy.SOURCE)
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
        Util.addBackIcon(bar, R.string.exchange, this);
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
                .hint(R.string.receiver)
                .build());

        // FIXME: 2017/6/30 caixiang  要添加到。。。
        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et_number)
                .related(RelatedId.mobile)
                .hint(R.string.phone_num)
                .build());

        addItem(new Builder(FormType.divider_large).build());
        addItem(new Builder(FormType.et_register)
                .related(RelatedId.province_city)
                .hint(R.string.province_city)
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et_register)
                .related(RelatedId.address)
                .hint(R.string.address)
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

                // FIXME: 2017/6/30 caixiang
                //检查手机号格式是否正确
                if (!RegexUtil.isMobileCN(getRelatedItem(RelatedId.mobile).getString(TFormElem.text))) {
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
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        stopRefresh();
        Result r = (Result) result;
        if (r.isSucceed()) {
            showToast(R.string.exchange_success);
            int epn = Profile.inst().getInt(TProfile.credits) - mEpn;
            Profile.inst().put(TProfile.credits, epn);
            Profile.inst().saveToSp();
            notify(NotifyType.profile_change);
            startActivity(OrderActivity.class);
            finish();
        } else {
            showToast(r.getError());
        }
    }

}
