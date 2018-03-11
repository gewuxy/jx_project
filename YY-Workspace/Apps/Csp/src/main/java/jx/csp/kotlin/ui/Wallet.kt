package jx.csp.kotlin.ui

import android.content.Context
import android.view.View
import android.widget.ExpandableListView
import android.widget.FrameLayout
import android.widget.TextView
import jx.csp.R
import jx.csp.constant.Constants
import jx.csp.kotlin.KotlinUtil
import jx.csp.util.Util
import lib.jx.ui.activity.base.BaseSRGroupListActivity
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

    override fun getContentViewId(): Int = R.layout.popup_wallet_tax

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
        setBackground(null)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.wallet_tv_taw_law1,
            R.id.wallet_tv_taw_law2 -> {
                if (TextUtil.isNotEmpty(url)) {
                    KotlinUtil.startWebActivity(url)
                    dismiss()
                }
            }
            R.id.popup_wallet_tax_layout -> {
                dismiss()
            }
        }
    }

    override fun getWindowWidth(): Int = MATCH_PARENT

    override fun getWindowHeight(): Int = WRAP_CONTENT

}

class GroupWallet : GroupEx<Wallet>() {
    var tag: String? = null
}

class Wallet : EVal<Wallet.TWallet>() {
    enum class TWallet {
        alpha, // 首字母
        headimg, // 单位号头像
        id, // 单位号id
        nickname, // 单位号昵称
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

    override fun getGroupConvertViewResId(): Int = R.layout.layout_wallet_item_group

    override fun refreshGroupView(groupPosition: Int, isExpanded: Boolean, holder: WalletVH?) {
        holder?.getGroupTv()?.text = getGroup(groupPosition)?.tag
    }

    override fun getChildConvertViewResId(): Int = R.layout.layout_wallet_item_child

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

    override fun setViews() {
        super.setViews()

        setRefreshEnabled(false)
    }

    override fun getDataFromNet() {
        exeNetworkReq(KotlinUtil.get())
    }

    override fun parseNetworkResponse(id: Int, text: String?): IResult<GroupWallet> {
        return KotlinUtil.groupIndex("{\"code\":\"0\",\"data\":[{\"alpha\":\"A\",\"headimg\":\"\",\"id\":620993,\"nickname\":\"安庆市立医院实习、进修培训平台\"},{\"alpha\":\"A\",\"headimg\":\"https://www.medcn.cn/file/headimg/17062316273505837613.jpg\",\"id\":620995,\"nickname\":\"安庆市立医院住院医师规范化培训学员平台\"},{\"alpha\":\"F\",\"headimg\":\"\",\"id\":625207,\"nickname\":\"福建省福州儿童医院医师培训\"},{\"alpha\":\"F\",\"headimg\":\"\",\"id\":933760,\"nickname\":\"福建省福州儿童医院规范化培训\"},{\"alpha\":\"G\",\"headimg\":\"https://www.medcn.cn/file/headimg/17062316273517281990.jpg\",\"id\":928349,\"nickname\":\"广州CDC死因与肿瘤监测科\"},{\"alpha\":\"Y\",\"headimg\":\"https://www.medcn.cn/file/headimg/1498814382638.jpg\",\"id\":1200007,\"nickname\":\"YaYa测试\"}]}"
                , GroupWallet::class.java, Wallet.TWallet.alpha)
    }

    override fun onDataSetChanged() {
        super.onDataSetChanged()

        expandAllGroup()
    }

    override fun createHeaderView(): View? = inflate(R.layout.layout_wallet_header)

    override fun createFooterView(): View? = inflate(R.layout.layout_footer_no_more)

    override fun onGroupClick(parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long): Boolean = true

    override fun getEmptyText(): String = getString(R.string.wallet_empty)

    override fun onDestroy() {
        super.onDestroy()

        if (popup.isShowing) popup.dismiss()
    }
}