package jx.csp.kotlin.ui

import android.widget.TextView
import jx.csp.R
import jx.csp.util.Util
import kotlinx.android.synthetic.main.activity_extract_detail.*
import lib.jx.ui.activity.base.BaseActivity
import lib.ys.config.AppConfig
import lib.ys.ui.other.NavBar

/**
 * 提现详情
 *
 * @auther : GuoXuan
 * @since : 2018/3/12
 */
/**
 * 可改用form
 */
abstract class BaseExtractDetailActivity : BaseActivity() {
    var tvExtract: TextView? = null
        private set

    override fun initData() {
        // do nothing
    }

    override fun getContentViewId(): Int = R.layout.activity_extract_detail

    override fun initNavBar(bar: NavBar?) {
        Util.addBackIcon(bar, R.string.extract_detail, this)
        tvExtract = bar?.addTextViewRight(R.string.wallet_extract, R.color.extract_nav_bar_tv_selector, null)
        tvExtract?.isEnabled = false
        Util.setTextViewBackground(tvExtract)
    }

    override fun findViews() {
        // do nothing
    }

    override fun setViews() {
        extract_detail_layout_detail.addView(inflate(getDetailId()))
        refresh(AppConfig.RefreshWay.embed)
        getDataFromNet()
    }

    protected fun getDataFromNet() {

    }

    override fun onRetryClick(): Boolean {
        if (!super.onRetryClick()) {
            refresh(AppConfig.RefreshWay.embed)
            getDataFromNet()
        }
        return true
    }

    abstract fun getDetailId(): Int

}

/**
 * 新的提现
 */
class ExtractDetailNewActivity : BaseExtractDetailActivity() {
    override fun getDetailId(): Int = R.layout.layout_extract_detail_new

}

/**
 * 个人提现
 */
class ExtractDetailPersonActivity : BaseExtractDetailActivity() {
    override fun getDetailId(): Int = R.layout.layout_extract_detail_person

}

/**
 * 企业提现
 */
class ExtractDetailCompanyActivity : BaseExtractDetailActivity() {
    override fun getDetailId(): Int = R.layout.layout_extract_detail_company

}