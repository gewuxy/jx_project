package jx.csp.kotlin.ui

import android.content.Context
import android.view.View
import android.widget.ExpandableListView
import android.widget.FrameLayout
import android.widget.TextView
import jx.csp.R
import jx.csp.constant.Constants
import jx.csp.constant.ExtractType
import jx.csp.constant.RoyaltyType
import jx.csp.kotlin.KotlinUtil
import jx.csp.kotlin.setTextColorRes
import jx.csp.model.me.WalletInfo
import jx.csp.network.JsonParser
import jx.csp.util.Util
import kotlinx.android.synthetic.main.layout_wallet_header.*
import lib.jx.network.Result
import lib.jx.ui.activity.base.BaseSRGroupListActivity
import lib.network.model.interfaces.IResult
import lib.ys.adapter.GroupAdapterEx
import lib.ys.model.EVal
import lib.ys.model.MapList
import lib.ys.model.group.GroupEx
import lib.ys.ui.other.NavBar
import lib.ys.ui.other.PopupWindowEx
import lib.ys.util.TextUtil
import lib.ys.util.TimeFormatter
import java.util.concurrent.TimeUnit

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

class GroupWallet : GroupEx<Wallet>() {
    var tag: String? = null
}

class Wallet : EVal<Wallet.TWallet>() {
    enum class TWallet {
        acceptName, // "YaYa测试", 单位号名称
        arrivalMoney, // 1779, 到账金额
        arrivalTime, // 到账时间
        avatar, // "http://medcn.synology.me:8886/file/headimg/1498814382638.jpg", 头像
        courseName, // "神经内科病例分享", 会议名称
        createTime, // 1520840366000, 投稿时间
        id, // "5", 提现id
        money, // 2000, 提现金额
        /**
         * [RoyaltyType]
         */
        state, // 0 提现状态
        /**
         * [ExtractType]
         */
        orderType, // 0 提现类型
    }
}

class WalletVH(itemView: View) : ExtractSelectVH(itemView) {
    fun getGroupTv(): TextView = findView(R.id.wallet_item_group_tv)
}

class WalletAdapter : GroupAdapterEx<GroupWallet, Wallet, WalletVH>() {
    var severTime = 0L

    override fun getGroupConvertViewResId() = R.layout.layout_wallet_item_group

    override fun refreshGroupView(groupPosition: Int, isExpanded: Boolean, holder: WalletVH?) {
        holder?.getGroupTv()?.text = getGroup(groupPosition)?.tag
    }

    override fun getChildConvertViewResId() = R.layout.layout_wallet_item_child

    override fun refreshChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, holder: WalletVH?) {
        val wallet = getChild(groupPosition, childPosition)
        holder?.getTvMoney()?.text = wallet.getString(Wallet.TWallet.money)
        val createTime = wallet.getLong(Wallet.TWallet.createTime, 0)
        holder?.getTvTime()?.text = format(createTime, "MM-dd HH:mm", TimeFormatter.TimeFormat.from_y_to_m_24)
        holder?.getTvOrganizer()?.text = wallet.getString(Wallet.TWallet.acceptName)
        holder?.getTvTitle()?.text = wallet.getString(Wallet.TWallet.courseName)
        when (wallet.getInt(Wallet.TWallet.state, 0)) {
            RoyaltyType.get_royalty -> {
                holder?.getTvMoney()?.isEnabled = true
                holder?.getTvCurrency()?.isEnabled = true
                holder?.getTvContent()?.setTextColorRes(R.color.text_9699a2)
                holder?.getTvContent()?.setText(R.string.wallet_state_get_royalty)
            }
            RoyaltyType.check -> {
                holder?.getTvMoney()?.isEnabled = true
                holder?.getTvCurrency()?.isEnabled = true
                holder?.getTvContent()?.setTextColorRes(R.color.text_9699a2)
                holder?.getTvContent()?.setText(R.string.wallet_state_check)
            }
            RoyaltyType.reject -> {
                holder?.getTvMoney()?.isEnabled = false
                holder?.getTvCurrency()?.isEnabled = false
                holder?.getTvContent()?.setTextColorRes(R.color.text_e43939)
                holder?.getTvContent()?.setText(R.string.wallet_state_reject)
            }
            RoyaltyType.succeed -> {
                holder?.getTvMoney()?.isEnabled = false
                holder?.getTvCurrency()?.isEnabled = false
                holder?.getTvContent()?.setTextColorRes(R.color.text_9699a2)
                val arrivalTime = wallet.getLong(Wallet.TWallet.arrivalTime, 0)
                val time = format(arrivalTime, "MM-dd", TimeFormatter.TimeFormat.simple_ymd)
                holder?.getTvContent()?.text = KotlinUtil.format(context.getString(R.string.wallet_state_succeed), time, wallet.getInt(Wallet.TWallet.arrivalMoney))
            }
        }
    }

    private fun format(time: Long, style1: String, style2: String): String {
        val style = if (time > severTime - TimeUnit.DAYS.toMillis(365)) style1 else style2
        return TimeFormatter.milli(time, style)
    }

}

class WalletActivity : BaseSRGroupListActivity<GroupWallet, Wallet, WalletAdapter>() {
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

        val info = r.data as WalletInfo
        money = info.getInt(WalletInfo.TWalletInfo.cash)
        severTime = info.getLong(WalletInfo.TWalletInfo.serveTime, System.currentTimeMillis())
        adapter.severTime = severTime
        val ws = info.getList<List<Wallet>>(WalletInfo.TWalletInfo.list)
        if (ws.isEmpty()) {
            retResult.setData(mapList)
            return retResult
        }

        for (w in ws) {
            val tag = month(w.getLong(Wallet.TWallet.createTime, 0))
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
        if (child?.getInt(Wallet.TWallet.state) == RoyaltyType.get_royalty) {
            //startActivity(ExtractDetailNewActivity::class.java)
        } else {
            if (child?.getInt(Wallet.TWallet.orderType) == ExtractType.company) {
                //startActivity(ExtractDetailCompanyActivity::class.java)
            } else {
                //startActivity(ExtractDetailPersonActivity::class.java)
            }
        }
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