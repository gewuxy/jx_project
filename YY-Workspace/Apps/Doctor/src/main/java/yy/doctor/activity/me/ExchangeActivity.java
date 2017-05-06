package yy.doctor.activity.me;

import android.view.View;

import lib.network.model.NetworkRequest;
import lib.network.model.NetworkResponse;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ex.NavBar;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CornerRenderer;
import lib.yy.activity.base.BaseFormActivity;
import lib.yy.network.Response;
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

    @Override
    public void initNavBar(NavBar bar) {

        bar.addBackIcon(R.mipmap.nav_bar_ic_back, "xxx商品", this);

    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et_register)
                .hint("收货人")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .hint("手机号")
                .build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et_register)
                .hint("广东 广州")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
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

        mIvGoods = findView(R.id.epc_iv);
    }

    @Override
    public void setViews() {
        super.setViews();

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

                NetworkRequest r = NetFactory.newExchangeBuilder()
                        .goodsId("000001")
                        .price("85")
                        .receiver("hell")
                        .phone("15860062000")
                        .province("hsj")
                        .address("sdfjijidsofj")
                        .builder();
                refresh(RefreshWay.dialog);
                exeNetworkRequest(0 , r);

            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {

        return JsonParser.ev(nr.getText() , Exchange.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        stopRefresh();
        Response<Exchange> r = (Response<Exchange>) result;

        if (r.isSucceed()) {
            showToast("兑换成功");
            startActivity(OrderActivity.class);
        } else {
            showToast(r.getError());
        }
    }
}
