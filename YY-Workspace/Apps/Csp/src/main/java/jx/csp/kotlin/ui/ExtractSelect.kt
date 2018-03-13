package jx.csp.kotlin.ui

import android.view.View
import android.widget.TextView
import jx.csp.R
import jx.csp.kotlin.KotlinUtil
import jx.csp.util.Util
import lib.jx.ui.activity.base.BaseSRListActivity
import lib.network.model.NetworkResp
import lib.network.model.interfaces.IResult
import lib.ys.adapter.AdapterEx
import lib.ys.adapter.VH.ViewHolderEx
import lib.ys.model.EVal
import lib.ys.ui.other.NavBar

/**
 * 提现列表
 *
 * @auther : GuoXuan
 * @since : 2018/3/11
 */
class ExtractSelect : EVal<ExtractSelect.TExtractSelect>() {
    enum class TExtractSelect {

    }
}

open class ExtractSelectVH(itemView: View) : ViewHolderEx(itemView) {
    fun getTvCurrency(): TextView = findView(R.id.royalty_item_tv_currency)

    fun getTvMoney(): TextView = findView(R.id.royalty_item_tv_money)

    fun getTvTime(): TextView = findView(R.id.royalty_item_tv_time)

    fun getTvOrganizer(): TextView = findView(R.id.royalty_item_tv_organizer)

    fun getTvTitle(): TextView = findView(R.id.royalty_item_tv_title)

    fun getTvContent(): TextView = findView(R.id.royalty_item_tv_content)

}

class ExtractSelectAdapter : AdapterEx<ExtractSelect, ExtractSelectVH>() {
    override fun getConvertViewResId(): Int = R.layout.layout_extract_select_item

    override fun refreshView(position: Int, holder: ExtractSelectVH?) {
    }

}

class ExtractSelectActivity : BaseSRListActivity<ExtractSelect, ExtractSelectAdapter>() {
    var tvExtract: TextView? = null

    override fun initData() {
    }

    override fun initNavBar(bar: NavBar?) {
        Util.addBackIcon(bar, R.string.select, this)
        tvExtract = bar?.addTextViewRight(R.string.wallet_extract, null)
    }

    override fun getDataFromNet() {
        exeNetworkReq(KotlinUtil.getExtractSelect(offset, limit))
    }

    override fun findViews() {
        super.findViews()

//        extract_select_iv_select_all
    }

    override fun getContentViewId(): Int = R.layout.activity_extract_select

    override fun createFooterView(): View? = inflate(R.layout.layout_footer_no_more)
}