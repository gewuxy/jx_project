package jx.csp.model.me;

import jx.csp.kotlin.ui.Wallet;
import lib.ys.model.EVal;

/**
 * 钱包信息
 *
 * @auther : GuoXuan
 * @since : 2018/3/12
 */
public class WalletInfo extends EVal<WalletInfo.TWalletInfo> {
    public enum TWalletInfo {
        cash, // 余额

        serveTime,

        @Bind(asList = Wallet.class)
        list,
    }

}
