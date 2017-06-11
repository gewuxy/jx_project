package yy.doctor.network.builder;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkReq.Builder;
import yy.doctor.network.NetFactory;
import yy.doctor.network.NetFactory.EpcExchangeParam;
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
    public ExchangeBuilder goodsId(int goodsId) {
        mBuilder.param(EpcExchangeParam.KGoodsId, goodsId);
        return this;
    }

    /**
     * 价格
     *
     * @param price
     * @return
     */
    public ExchangeBuilder price(int price) {
        mBuilder.param(EpcExchangeParam.KPrice, price);
        return this;
    }

    /**
     * 收货人
     *
     * @param receiver
     * @return
     */
    public ExchangeBuilder receiver(String receiver) {
        mBuilder.param(EpcExchangeParam.KReceiver, receiver);
        return this;
    }

    /**
     * 手机号码
     *
     * @param phone
     * @return
     */
    public ExchangeBuilder phone(String phone) {
        mBuilder.param(EpcExchangeParam.KPhone, phone);
        return this;
    }

    /**
     * 省份
     *
     * @param province
     * @return
     */
    public ExchangeBuilder province(String province) {
        mBuilder.param(EpcExchangeParam.KProvince, province);
        return this;
    }

    /**
     * 地址
     *
     * @param address
     * @return
     */
    public ExchangeBuilder address(String address) {
        mBuilder.param(EpcExchangeParam.KAddress, address);
        return this;
    }

    /**
     * 限购数量
     *
     * @param buyLimit
     * @return
     */
    public ExchangeBuilder buyLimit(String buyLimit) {
        mBuilder.param(EpcExchangeParam.KBuyLimit, buyLimit);
        return this;
    }

    public NetworkReq builder() {
        return mBuilder.build();
    }

}