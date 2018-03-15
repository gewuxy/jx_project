package jx.csp.ui

import android.view.View
import android.widget.TextView
import jx.csp.App
import jx.csp.Extra
import jx.csp.R
import jx.csp.constant.Constants
import jx.csp.constant.InvoiceType
import jx.csp.constant.RoyaltyType
import jx.csp.network.JsonParser
import jx.csp.ui.ExtractDetail.TExtractDetail
import jx.csp.ui.Royalty.TRoyalty
import jx.csp.util.KotlinUtil
import jx.csp.util.Util
import jx.csp.util.setTextColorRes
import jx.csp.util.topMargin
import kotlinx.android.synthetic.main.activity_extract_detail.*
import kotlinx.android.synthetic.main.layout_extract_detail_company.*
import kotlinx.android.synthetic.main.layout_extract_detail_new.*
import kotlinx.android.synthetic.main.layout_extract_detail_person.*
import lib.jx.ui.activity.base.BaseActivity
import lib.network.model.NetworkError
import lib.network.model.NetworkResp
import lib.network.model.interfaces.IResult
import lib.ys.config.AppConfig
import lib.ys.model.EVal
import lib.ys.ui.decor.DecorViewEx
import lib.ys.ui.other.NavBar
import lib.ys.util.TimeFormatter

/**
 * 提现详情
 *
 * @auther : GuoXuan
 * @since : 2018/3/12
 */
class ExtractDetail : EVal<TExtractDetail>() {
    enum class TExtractDetail {
        acceptName, //"YaYa测试",
        account, //"666666666",
        arrivalMoney, //1779,
        arrivalTime,
        serverTime,
        avatar, //"http:\/\/medcn.synology.me:8886\/file\/headimg\/1498814382638.jpg",
        bankName, //"浦发银行",
        courseName, //"神经内科病例分享",
        createTime, //1520840366000,
        expressId, //"123456789",
        expressName, //"顺丰快递",
        id, //"5",
        invoiceType, //"0",
        money, //2000,
        orderId, //"1",
        orderType, //0,
        payeeName, //"收钱名称",
        rate, //0,
        state, //3
    }
}

/**
 * 可改用form
 */
abstract class BaseExtractDetailActivity : BaseActivity() {
    protected var tvExtract: TextView? = null
        private set

    var id: String? = null
    var state: Int? = null

    override fun initData() {
        id = intent?.getStringExtra(Extra.KId)
        state = intent?.getIntExtra(Extra.KState, RoyaltyType.get_royalty)
    }

    override fun getContentViewId() = R.layout.activity_extract_detail

    override fun initNavBar(bar: NavBar?) {
        Util.addBackIcon(bar, R.string.extract_detail, this)
        tvExtract = bar?.addTextViewRight(
                if (state == RoyaltyType.succeed) R.string.extract_detail_extracted else R.string.wallet_extract,
                R.color.extract_nav_bar_tv_selector, {
            // todo
        })
        tvExtract?.isEnabled = state == RoyaltyType.get_royalty || state == RoyaltyType.reject
        Util.setTextViewBackground(tvExtract)
    }

    override fun findViews() {
        // do nothing
    }

    override fun setViews() {
        extract_detail_layout_detail.addView(inflate(getDetailId()))
    }

    protected fun getDataFromNet() {
        exeNetworkReq(KotlinUtil.getExtractDetail(id))
    }

    override fun onNetworkResponse(id: Int, resp: NetworkResp?): IResult<*> {
        return JsonParser.ev(resp?.text, ExtractDetail::class.java)
    }

    override fun onNetworkSuccess(id: Int, r: IResult<*>?) {
        if (r?.isSucceed == true) {
            onNetworkSuccess(r.data as? ExtractDetail)
            viewState = DecorViewEx.ViewState.normal
        } else {
            onNetworkError(id, r?.error)
        }
    }

    override fun onNetworkError(id: Int, error: NetworkError?) {
        super.onNetworkError(id, error)

        viewState = DecorViewEx.ViewState.error
    }

    override fun onRetryClick(): Boolean {
        if (!super.onRetryClick()) {
            refresh(AppConfig.RefreshWay.embed)
            getDataFromNet()
        }
        return true
    }

    fun royaltyState(state: Int, succeedText: String?) {
        when (state) {
            RoyaltyType.get_royalty -> {
                extract_detail_tv_money.isEnabled = true
                extract_detail_tv_currency.isEnabled = true
                extract_detail_tv_extract_state.setTextColorRes(R.color.text_9699a2)
                extract_detail_tv_extract_state.setText(R.string.wallet_state_get_royalty)
            }
            RoyaltyType.check -> {
                extract_detail_tv_money.isEnabled = true
                extract_detail_tv_currency.isEnabled = true
                extract_detail_tv_extract_state.setTextColorRes(R.color.text_9699a2)
                extract_detail_tv_extract_state.setText(R.string.wallet_state_check)
            }
            RoyaltyType.reject -> {
                extract_detail_tv_money.isEnabled = true
                extract_detail_tv_currency.isEnabled = true
                extract_detail_tv_extract_state.setTextColorRes(R.color.text_e43939)
                extract_detail_tv_extract_state.setText(R.string.wallet_state_reject)
            }
            RoyaltyType.succeed -> {
                extract_detail_tv_money.isEnabled = false
                extract_detail_tv_currency.isEnabled = false
                extract_detail_tv_extract_state.setTextColorRes(R.color.text_9699a2)
                extract_detail_tv_extract_state.text = succeedText
            }
        }
    }

    open fun onNetworkSuccess(detail: ExtractDetail?) {
        val arrivalTime = detail?.getLong(TExtractDetail.arrivalTime, 0) ?: 0
        val serverTime = detail?.getLong(TExtractDetail.serverTime, 0) ?: 0
        val time = format(arrivalTime, serverTime, "MM-dd", TimeFormatter.TimeFormat.simple_ymd)
        val format = KotlinUtil.format(App.ct().getString(R.string.wallet_state_succeed),
                time, detail?.getInt(TExtractDetail.arrivalMoney))
        royaltyState(detail?.getInt(TExtractDetail.state, 0) ?: 0, format)
    }

    abstract fun getDetailId(): Int

}

/**
 * 新的提现详情
 */
class ExtractDetailNewActivity : BaseExtractDetailActivity() {
    var royalty: Royalty? = null

    override fun initData() {
        super.initData()

        royalty = intent?.getSerializableExtra(Extra.KData) as? Royalty
    }

    override fun setViews() {
        super.setViews()

        tvExtract?.isEnabled = true
        extract_detail_tv_money.text = royalty?.getString(TRoyalty.money)
        extract_detail_layout_new.visibility = View.VISIBLE
        royaltyState(RoyaltyType.get_royalty, Constants.KEmpty)
        extract_detail_new_tv_title.text = royalty?.getString(TRoyalty.courseName)
    }

    override fun getDetailId() = R.layout.layout_extract_detail_new

}

/**
 * 个人提现详情
 */
class ExtractDetailPersonActivity : BaseExtractDetailActivity() {
    override fun setViews() {
        super.setViews()

        refresh(AppConfig.RefreshWay.embed)
        getDataFromNet()
    }

    override fun getDetailId() = R.layout.layout_extract_detail_person

    override fun onNetworkSuccess(detail: ExtractDetail?) {
        super.onNetworkSuccess(detail)

        extract_detail_tv_money.text = detail?.getString(TExtractDetail.arrivalMoney)
        extract_detail_person_tv_money.text = detail?.getString(TExtractDetail.money)
        val arrival = detail?.getLong(TExtractDetail.arrivalMoney) ?: 0
        val money = detail?.getLong(TExtractDetail.money) ?: 0
        extract_detail_person_tv_tax.text = (arrival - money).toString()
        extract_detail_person_tv_name.text = detail?.getString(TExtractDetail.payeeName)
        extract_detail_person_tv_alipay.text = detail?.getString(TExtractDetail.account)
    }

}

/**
 * 企业提现详情
 */
class ExtractDetailCompanyActivity : BaseExtractDetailActivity() {
    override fun setViews() {
        super.setViews()

        extract_detail_layout_extract_state.topMargin(fit(2f))
        extract_detail_layout_invoice.visibility = View.VISIBLE
        extract_detail_layout_invoice.setOnClickListener {
            // todo
        }

        refresh(AppConfig.RefreshWay.embed)
        getDataFromNet()
    }

    override fun getDetailId() = R.layout.layout_extract_detail_company

    override fun onNetworkSuccess(detail: ExtractDetail?) {
        super.onNetworkSuccess(detail)

        extract_detail_tv_money.text = detail?.getString(TExtractDetail.money)
        extract_detail_company_tv_company_name.text = detail?.getString(TExtractDetail.payeeName)
        extract_detail_company_tv_bank_of_deposit.text = detail?.getString(TExtractDetail.bankName)
        extract_detail_company_tv_bank_account.text = detail?.getString(TExtractDetail.account)
        if (detail?.getInt(TExtractDetail.invoiceType) == InvoiceType.electronic) {
            extract_detail_company_tv_invoice_type.setText(R.string.extract_detail_invoice_electronic)
        } else {
            extract_detail_company_tv_invoice_type.setText(R.string.extract_detail_invoice_paper)
            extract_detail_company_layout_courier_company.visibility = View.VISIBLE
            extract_detail_company_layout_courier_number.visibility = View.VISIBLE
            extract_detail_company_tv_courier_company.text = detail?.getString(TExtractDetail.expressName)
            extract_detail_company_tv_courier_number.text = detail?.getString(TExtractDetail.expressId)
        }
    }

}