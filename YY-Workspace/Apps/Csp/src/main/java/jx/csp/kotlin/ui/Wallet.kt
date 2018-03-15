package jx.csp.kotlin.ui

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ExpandableListView
import android.widget.FrameLayout
import android.widget.TextView
import jx.csp.Extra
import jx.csp.R
import jx.csp.constant.Constants
import jx.csp.constant.ExtractType
import jx.csp.constant.RoyaltyType
import jx.csp.kotlin.KotlinUtil
import jx.csp.kotlin.ui.Royalty.TRoyalty
import jx.csp.model.me.WalletInfo
import jx.csp.model.me.WalletInfo.TWalletInfo
import jx.csp.network.JsonParser
import jx.csp.util.Util
import kotlinx.android.synthetic.main.layout_wallet_header.*
import lib.jx.network.Result
import lib.jx.ui.activity.base.BaseSRGroupListActivity
import lib.network.model.interfaces.IResult
import lib.ys.adapter.GroupAdapterEx
import lib.ys.model.MapList
import lib.ys.model.group.GroupEx
import lib.ys.ui.other.NavBar
import lib.ys.ui.other.PopupWindowEx
import lib.ys.util.TextUtil
import lib.ys.util.TimeFormatter
import java.util.*

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
    var url = Constants.KEmpty
    var layout: View? = null
    private var tVTax1: TextView? = null
    private var tVTax2: TextView? = null

    override fun getContentViewId() = R.layout.popup_wallet_tax

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

    override fun getWindowWidth() = MATCH_PARENT

    override fun getWindowHeight() = WRAP_CONTENT

}

class GroupWallet : GroupEx<Royalty>() {
    var tag: String? = null
}

class WalletVH(itemView: View) : RoyaltyVH(itemView) {
    fun getGroupTv(): TextView = findView(R.id.wallet_item_group_tv)
}

class WalletAdapter : GroupAdapterEx<GroupWallet, Royalty, WalletVH>() {
    var severTime = 0L

    override fun getGroupConvertViewResId() = R.layout.layout_wallet_item_group

    override fun refreshGroupView(groupPosition: Int, isExpanded: Boolean, holder: WalletVH?) {
        holder?.getGroupTv()?.text = getGroup(groupPosition)?.tag
    }

    override fun getChildConvertViewResId() = R.layout.layout_wallet_item_child

    override fun refreshChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, holder: WalletVH?) {
        val royalty = getChild(groupPosition, childPosition)
        royaltyVHSet(holder, royalty, severTime)
    }

}

class WalletActivity : BaseSRGroupListActivity<GroupWallet, Royalty, WalletAdapter>() {
    private val pageSize = 15

    private val popup: TaxPopup by lazy {
        TaxPopup(this)
    }

    private var mapList: MapList<String, GroupWallet> = MapList() // 只记录判断有没有更多的limit
    private var allData: MapList<String, GroupWallet> = MapList() // 只记录group,

    private var money = 0
        set(value) {
            field = money
            runOnUIThread({
                wallet_header_tv_money.text = value.toString()
                wallet_header_tv_money.isEnabled = value > 0
                wallet_header_tv_extract.isEnabled = value > 0
            })
        }

    private var severTime = 0L

    override fun initData() {
//        TODO("后台确认")
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
        setFloatingGroupEnabled(false)
        wallet_header_tv_extract.setOnClickListener { startActivity(ExtractSelectActivity::class.java) }
    }

    override fun getDataFromNet() {
        exeNetworkReq(KotlinUtil.getWallet(offset, pageSize))
    }

    override fun parseNetworkResponse(id: Int, text: String?): IResult<GroupWallet> {
        val retResult = Result<GroupWallet>()

        val r = JsonParser.ev(text, WalletInfo::class.java)
        retResult.code = r.code
        retResult.message = r.message

        if (!r.isSucceed) {
            return retResult
        }

        mapList = MapList()

        val info = r.data as? WalletInfo
        money = info?.getInt(TWalletInfo.cash) ?: 0
        severTime = info?.getLong(TWalletInfo.serveTime, System.currentTimeMillis()) ?: 0
        adapter.severTime = severTime
        val ws = info?.getList<List<Royalty>>(TWalletInfo.list) ?: ArrayList()
        if (ws.isEmpty()) {
            retResult.setData(mapList)
            return retResult
        }

        for (w in ws) {
            val tag = month(w.getLong(TRoyalty.createTime, 0))
            var g: GroupWallet? = allData.getByKey(tag)
            if (g == null) {
                g = mapList.getByKey(tag)
                if (g == null) {
                    g = GroupWallet()
                    g.tag = tag
                    mapList.add(tag, g)
                    allData.add(tag, g)
                }
            }
            g.addChild(w)
        }
        retResult.setData(mapList)
        return retResult
    }

    private fun month(time: Long): String {
        val severYear = TimeFormatter.milli(severTime, "yyyy")
        val timeYear = TimeFormatter.milli(time, "yyyy")
        var timeMonth = TimeFormatter.milli(time, "MM")
        if (severYear == timeYear) {
            val severMonth = TimeFormatter.milli(severTime, "MM")
            if (timeMonth == severMonth) {
                return "本月"
            } else {
                timeMonth = if (timeMonth.startsWith("0")) timeMonth.replace("0", "") else timeMonth
                return timeMonth.plus("月")
            }
        } else {
            timeMonth = if (timeMonth.startsWith("0")) timeMonth.replace("0", "") else timeMonth
            return timeYear.plus("年").plus(timeMonth).plus("月")
        }
    }

    override fun onDataSetChanged() {
        super.onDataSetChanged()

        expandAllGroup()
    }

    override fun createHeaderView(): View? = inflate(R.layout.layout_wallet_header)

    override fun createFooterView(): View? = inflate(R.layout.layout_footer_no_more)

    override fun onGroupClick(parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long): Boolean = true

    override fun onChildClick(parent: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        val child = getChild(groupPosition, childPosition)
        val i = Intent(this, if (child?.getInt(TRoyalty.state) == RoyaltyType.get_royalty) {
            ExtractDetailNewActivity::class.java
        } else {
            if (child?.getInt(TRoyalty.orderType) == ExtractType.company) {
                ExtractDetailCompanyActivity::class.java
            } else {
                ExtractDetailPersonActivity::class.java
            }
        }).putExtra(Extra.KId, child.getString(TRoyalty.id))
                .putExtra(Extra.KState, child.getInt(TRoyalty.state))
                .putExtra(Extra.KData, child)
        startActivity(i)
        return super.onChildClick(parent, v, groupPosition, childPosition, id)
    }

    override fun createEmptyFooterView(): View = inflate(R.layout.layout_empty_foot)

    override fun getEmptyText() = getString(R.string.wallet_empty)

    override fun onDestroy() {
        super.onDestroy()

        if (popup.isShowing) popup.dismiss()
    }

    override fun getLimit() = if (mapList.size == 0) pageSize else mapList.size

}