package yy.doctor.network.builder;

import lib.network.model.NetworkRequest;
import lib.network.model.NetworkRequest.Builder;
import yy.doctor.network.NetFactory;
import yy.doctor.network.NetFactory.EpcExchangePara;
import yy.doctor.network.UrlUtil.UrlEpc;

public class ExchangeBuilder {

    private Builder mBuilder;

    public ExchangeBuilder() {
        mBuilder = NetFactory.newPost(UrlEpc.exchange);
    }

    /**
     * 商品id
     *
     * @param goodsId
     * @return
     */
    public ExchangeBuilder goodsId(String goodsId) {
        mBuilder.param(EpcExchangePara.goodsId, goodsId);
        return this;
    }

    /**
     * 价格
     *
     * @param price
     * @return
     */
    public ExchangeBuilder price(String price) {
        mBuilder.param(EpcExchangePara.price, price);
        return this;
    }

    /**
     * 收货人
     *
     * @param receiver
     * @return
     */
    public ExchangeBuilder receiver(String receiver) {
        mBuilder.param(EpcExchangePara.receiver, receiver);
        return this;
    }

    /**
     * 手机号码
     *
     * @param phone
     * @return
     */
    public ExchangeBuilder phone(String phone) {
        mBuilder.param(EpcExchangePara.phone, phone);
        return this;
    }

    /**
     * 省份
     *
     * @param province
     * @return
     */
    public ExchangeBuilder province(String province) {
        mBuilder.param(EpcExchangePara.province, province);
        return this;
    }

    /**
     * 地址
     *
     * @param address
     * @return
     */
    public ExchangeBuilder address(String address) {
        mBuilder.param(EpcExchangePara.address, address);
        return this;
    }

    /**
     * 限购数量
     *
     * @param buyLimit
     * @return
     */
    public ExchangeBuilder buyLimit(String buyLimit) {
        mBuilder.param(EpcExchangePara.buyLimit, buyLimit);
        return this;
    }

    public NetworkRequest builder() {
        return mBuilder.build();
    }

}