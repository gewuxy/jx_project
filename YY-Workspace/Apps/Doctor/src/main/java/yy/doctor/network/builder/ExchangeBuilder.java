package yy.doctor.network.builder;

import lib.network.model.NetworkRequest;
import lib.network.model.NetworkRequest.Builder;
import yy.doctor.network.NetFactory;
import yy.doctor.network.NetFactory.EpcExchangePara;
import yy.doctor.network.UrlUtil.UrlEpc;

/**
 * 兑换的Builder
 */
public class ExchangeBuilder {

    private Builder mBuilder;

    public ExchangeBuilder() {
        mBuilder = NetFactory.newPost(UrlEpc.KExchange);
    }

    /**
     * 商品id
     *
     * @param goodsId
     * @return
     */
    public ExchangeBuilder goodsId(String goodsId) {
        mBuilder.param(EpcExchangePara.KGoodsId, goodsId);
        return this;
    }

    /**
     * 价格
     *
     * @param price
     * @return
     */
    public ExchangeBuilder price(String price) {
        mBuilder.param(EpcExchangePara.KPrice, price);
        return this;
    }

    /**
     * 收货人
     *
     * @param receiver
     * @return
     */
    public ExchangeBuilder receiver(String receiver) {
        mBuilder.param(EpcExchangePara.KReceiver, receiver);
        return this;
    }

    /**
     * 手机号码
     *
     * @param phone
     * @return
     */
    public ExchangeBuilder phone(String phone) {
        mBuilder.param(EpcExchangePara.KPhone, phone);
        return this;
    }

    /**
     * 省份
     *
     * @param province
     * @return
     */
    public ExchangeBuilder province(String province) {
        mBuilder.param(EpcExchangePara.KProvince, province);
        return this;
    }

    /**
     * 地址
     *
     * @param address
     * @return
     */
    public ExchangeBuilder address(String address) {
        mBuilder.param(EpcExchangePara.KAddress, address);
        return this;
    }

    /**
     * 限购数量
     *
     * @param buyLimit
     * @return
     */
    public ExchangeBuilder buyLimit(String buyLimit) {
        mBuilder.param(EpcExchangePara.KBuyLimit, buyLimit);
        return this;
    }

    public NetworkRequest builder() {
        return mBuilder.build();
    }

}