package jx.csp.kotlin.ui

import android.view.View
import android.widget.TextView
import jx.csp.R
import jx.csp.util.Util
import lib.jx.ui.activity.base.BaseSRRecyclerActivity
import lib.ys.adapter.VH.RecyclerViewHolderEx
import lib.ys.adapter.recycler.MultiRecyclerAdapterEx
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

class ExtractSelectVH(itemView: View) : RecyclerViewHolderEx(itemView) {

}

class ExtractSelectAdapter : MultiRecyclerAdapterEx<ExtractSelect, ExtractSelectVH>() {
    override fun getConvertViewResId(itemType: Int): Int = R.layout.layout_extract_select_item

    override fun refreshView(position: Int, holder: ExtractSelectVH?, itemType: Int) {
    }

}

class ExtractSelectActivity : BaseSRRecyclerActivity<ExtractSelect, ExtractSelectAdapter>() {
    var tvExtract: TextView? = null

    override fun initData() {
    }

    override fun initNavBar(bar: NavBar?) {
        Util.addBackIcon(bar, R.string.select, this)
        tvExtract = bar?.addTextViewRight(R.string.wallet_extract, null)
    }

    override fun getDataFromNet() {
    }

    override fun findViews() {
        super.findViews()

//        extract_select_iv_select_all
    }

    override fun getContentViewId(): Int = R.layout.activity_extract_select

    override fun createFooterView(): View? = inflate(R.layout.layout_footer_no_more)
}