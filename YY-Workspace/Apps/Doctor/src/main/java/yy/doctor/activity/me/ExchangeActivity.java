package yy.doctor.activity.me;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CornerRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseFormActivity;
import lib.yy.network.Resp;
import yy.doctor.Extra;
import yy.doctor.R;
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

    private int mGoodId;
    private String mGoodName;
    private int mEpn;

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

    public static void nav(Context context, int goodId, String goodName, int epn) {
        Intent i = new Intent(context, ExchangeActivity.class);
        i.putExtra(Extra.KData, goodId);
        i.putExtra(Extra.KName, goodName);
        i.putExtra(Extra.KNum, epn);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initNavBar(NavBar bar) {

        bar.addBackIcon(R.mipmap.nav_bar_ic_back, mGoodName, this);

    }

    @Override
    public void initData() {
        super.initData();

        Intent i = getIntent();
        mGoodName = i.getStringExtra(Extra.KName);
        mGoodId = i.getIntExtra(Extra.KData, 0);
        mEpn = i.getIntExtra(Extra.KNum, 0);

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.receiver)
                .hint("收货人")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.mobile)
                .hint("手机号")
                .build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.province_city)
                .hint("广东 广州")
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
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvName.setText(mGoodName);
        mTvEpn.setText(mEpn + "象数");

        setOnClickListener(R.id.exchange_tv_btn);

        mIvGoods.placeHolder(R.mipmap.ic_default_epc)
                .renderer(new CornerRenderer(fitDp(3)))
                .load();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.exchange_tv_btn: {

                if (!check()) {
                    return;
                };
                NetworkReq r = NetFactory.newExchangeBuilder()
                        .goodsId("000001")
                        .price("85")
                        .receiver("都是广")
                        .phone("15860062000")
                        .province("hsj")
                        .address("故事机加快速度啊速度快解放的看法")
                        .builder();
                refresh(RefreshWay.dialog);
                exeNetworkReq(0, r);

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
        Resp<Exchange> r = (Resp<Exchange>) result;

        if (r.isSucceed()) {
            showToast("兑换成功");
            startActivity(OrderActivity.class);
        } else {
            showToast(r.getError());
        }
    }
}
