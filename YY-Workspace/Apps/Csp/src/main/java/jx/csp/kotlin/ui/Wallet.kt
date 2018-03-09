package jx.csp.kotlin.ui

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import jx.csp.R
import jx.csp.constant.Constants
import jx.csp.kotlin.KotlinUtil
import jx.csp.util.Util
import lib.jx.network.Result
import lib.jx.ui.activity.base.BaseSRGroupListActivity
import lib.network.model.NetworkResp
import lib.network.model.interfaces.IResult
import lib.ys.adapter.GroupAdapterEx
import lib.ys.adapter.VH.ViewHolderEx
import lib.ys.model.EVal
import lib.ys.model.group.GroupEx
import lib.ys.ui.other.NavBar
import lib.ys.ui.other.PopupWindowEx
import lib.ys.util.TextUtil

/**
 * 我的钱包
 *
 * @auther : GuoXuan
 * @since : 2018/3/9
 */
/**
 * 缴税说明框
 */
class TaxPopup(context: Context) : PopupWindowEx(context) {
    var url: String = Constants.KEmpty
    var layout: View? = null
    var tVTax1: TextView? = null
    var tVTax2: TextView? = null

    override fun getContentViewId(): Int {
        return R.layout.popup_wallet_tax
    }

    override fun findViews() {
        layout = findView(R.id.popup_wallet_tax_layout)
        tVTax1 = findView(R.id.wallet_tv_taw_law1)
        tVTax2 = findView(R.id.wallet_tv_taw_law2)
    }

    override fun setViews() {
        layout?.setOnClickListener(this)
        tVTax2?.setOnClickListener(this)
        tVTax2?.setOnClickListener(this)
        setTouchOutsideDismissEnabled(true)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.wallet_tv_taw_law1,
            R.id.wallet_tv_taw_law2 -> {
                if (TextUtil.isNotEmpty(url)) {
                    KotlinUtil.startWebActivity(url)
                }
            }
            R.id.popup_wallet_tax_layout -> {
                dismiss()
            }
        }
    }

    override fun getWindowWidth(): Int {
        return MATCH_PARENT
    }

    override fun getWindowHeight(): Int {
        return WRAP_CONTENT
    }

}

class GroupWallet : GroupEx<Wallet>() {
    var tag: String? = null
}

class Wallet : EVal<Wallet.TWallet>() {
    enum class TWallet {

    }
}

class WalletVH(itemView: View) : ViewHolderEx(itemView) {
    fun getGroupTv(): TextView = findView(R.id.wallet_item_group_tv)

    fun getChildTvCurrency(): TextView = findView(R.id.wallet_item_child_tv_currency)

    fun getChildTvMoney(): TextView = findView(R.id.wallet_item_child_tv_money)

    fun getChildTvTime(): TextView = findView(R.id.wallet_item_child_tv_time)

    fun getChildTvOrganizer(): TextView = findView(R.id.wallet_item_child_tv_organizer)

    fun getChildTvTitle(): TextView = findView(R.id.wallet_item_child_tv_title)

    fun getChildTvContent(): TextView = findView(R.id.wallet_item_child_tv_content)
}

class WalletAdapter : GroupAdapterEx<GroupWallet, Wallet, WalletVH>() {

    override fun getGroupConvertViewResId(): Int {
        return R.layout.layout_wallet_item_group
    }

    override fun refreshGroupView(groupPosition: Int, isExpanded: Boolean, holder: WalletVH?) {
        holder?.getGroupTv()?.text = getGroup(groupPosition)?.tag
    }

    override fun getChildConvertViewResId(): Int {
        return R.layout.layout_wallet_item_child
    }

    override fun refreshChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, holder: WalletVH?) {
    }

}

class WalletActivity : BaseSRGroupListActivity<GroupWallet, Wallet, WalletAdapter>() {
    val popup: TaxPopup by lazy {
        TaxPopup(this)
    }

    override fun initData() {
        popup.url = "www.baidu.com"
    }

    override fun initNavBar(bar: NavBar?) {
        Util.addBackIcon(bar, R.string.my_wallet, this)
        bar?.addViewRight<FrameLayout>(inflate(R.layout.layout_nav_bar_wallet_right), {
            popup.showAsDropDown(bar)
        })
    }

    override fun getDataFromNet() {
        exeNetworkReq(KotlinUtil.get())
    }

    override fun parseNetworkResponse(id: Int, text: String?): IResult<GroupWallet> {
        return super.parseNetworkResponse(id, "{\n" +
                "                                                            \"code\": \"0\",\n" +
                "                                                            \"data\": {}}")
    }

    override fun getContentHeaderViewId(): Int {
        return R.layout.layout_wallet_header
    }

    override fun getEmptyText(): String {
        return getString(R.string.wallet_empty)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (popup.isShowing) popup.dismiss()
    }
}