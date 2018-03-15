package jx.csp.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import jx.csp.R
import jx.csp.model.me.ExtractSelectInfo
import jx.csp.network.JsonParser
import jx.csp.util.KotlinUtil
import jx.csp.util.Util
import kotlinx.android.synthetic.main.activity_extract_select.*
import lib.jx.network.Result
import lib.jx.ui.activity.base.BaseSRListActivity
import lib.network.model.interfaces.IResult
import lib.ys.adapter.AdapterEx
import lib.ys.adapter.MultiAdapterEx
import lib.ys.ui.other.NavBar
import org.json.JSONException

/**
 * 提现列表
 *
 * @auther : GuoXuan
 * @since : 2018/3/11
 */
class ExtractSelectVH(itemView: View) : RoyaltyVH(itemView) {
    fun getIvSelect(): ImageView = findView(R.id.extract_select_item_iv)

    fun getLayout(): View = findView(R.id.extract_select_item_layout)

}

class ExtractSelectAdapter : AdapterEx<Royalty, ExtractSelectVH>() {
    var severTime = 0L

    override fun getConvertViewResId() = R.layout.layout_extract_select_item

    override fun refreshView(position: Int, holder: ExtractSelectVH?) {
        holder?.getIvSelect()?.isSelected = getItem(position).getBoolean(Royalty.TRoyalty.select)
        royaltyVHSet(holder, getItem(position), severTime)
        setOnViewClickListener(position, holder?.getLayout())
    }

    override fun onViewClick(position: Int, v: View?) {
        when (v?.id) {
            R.id.extract_select_item_layout -> {
                // 取反
                val select = getItem(position).getBoolean(Royalty.TRoyalty.select)
                getItem(position).put<Royalty>(Royalty.TRoyalty.select, !select)
                invalidate(position)
            }
        }
    }

}

class ExtractSelectActivity : BaseSRListActivity<Royalty, ExtractSelectAdapter>(), MultiAdapterEx.OnAdapterClickListener {
    private var tvExtract: TextView? = null

    override fun initData() {
        // do nothing
    }

    override fun initNavBar(bar: NavBar?) {
        Util.addBackIcon(bar, R.string.select, this)
        tvExtract = bar?.addTextViewRight(R.string.wallet_extract, R.color.extract_nav_bar_tv_selector, {
            // todo
        })
        tvExtract?.isEnabled = false
        Util.setTextViewBackground(tvExtract)
    }

    override fun getDataFromNet() {
        exeNetworkReq(KotlinUtil.getExtractSelect(offset, limit))
    }

    override fun setViews() {
        super.setViews()

        setRefreshEnabled(false)
        setOnAdapterClickListener(this)
        extract_select_iv_select_all.setOnClickListener {
            val selected = !extract_select_iv_select_all.isSelected
            for (i in 0 until count) {
                getItem(i).put<Royalty>(Royalty.TRoyalty.select, selected)
            }
            invalidate()
            extract_select_iv_select_all.isSelected = selected
            tvExtract?.isEnabled = selected
        }
    }

    @Throws(JSONException::class)
    override fun parseNetworkResponse(id: Int, text: String?): IResult<*> {
        val result = Result<Royalty>()

        val r = JsonParser.ev(text, ExtractSelectInfo::class.java)
        result.code = r.code
        result.message = r.message
        if (r.isSucceed) {
            val info = r.data as? ExtractSelectInfo
            val list = info?.getList<List<Royalty>>(ExtractSelectInfo.TExtractSelectInfo.list)
            adapter.severTime = info?.getLong(ExtractSelectInfo.TExtractSelectInfo.severTime) ?: 0
            result.setData(list)
        }
        return result
    }

    override fun getContentViewId() = R.layout.activity_extract_select

    override fun createFooterView(): View? = inflate(R.layout.layout_footer_no_more)

    override fun onAdapterClick(position: Int, v: View?) {
        changeSelect()
        // 提现按钮的状态
        var enabled = false
        for (i in 0 until count) {
            enabled = enabled || getItem(i).getBoolean(Royalty.TRoyalty.select)
            if (enabled) break
        }
        tvExtract?.isEnabled = enabled
    }

    override fun onDataSetChanged() {
        super.onDataSetChanged()

        changeSelect()
    }

    /**
     * 改变全选的选择状态
     */
    private fun changeSelect() {
        var select = true
        for (i in 0 until count) {
            select = select && getItem(i).getBoolean(Royalty.TRoyalty.select)
            if (!select) break
        }
        extract_select_iv_select_all.isSelected = select
    }

    override fun getLimit() = 15
}