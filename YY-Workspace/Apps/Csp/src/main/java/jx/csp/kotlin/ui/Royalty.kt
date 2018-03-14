package jx.csp.kotlin.ui

import android.view.View
import android.widget.TextView
import jx.csp.App
import jx.csp.R
import jx.csp.constant.ExtractType
import jx.csp.constant.RoyaltyType
import jx.csp.kotlin.KotlinUtil
import jx.csp.kotlin.setTextColorRes
import lib.ys.adapter.VH.ViewHolderEx
import lib.ys.model.EVal
import lib.ys.util.TimeFormatter
import java.util.concurrent.TimeUnit

/**
 *
 * @auther : GuoXuan
 * @since : 2018/3/13
 */
open class RoyaltyVH(itemView: View) : ViewHolderEx(itemView) {
    fun getTvCurrency(): TextView = findView(R.id.royalty_item_tv_currency)

    fun getTvMoney(): TextView = findView(R.id.royalty_item_tv_money)

    fun getTvTime(): TextView = findView(R.id.royalty_item_tv_time)

    fun getTvOrganizer(): TextView = findView(R.id.royalty_item_tv_organizer)

    fun getTvTitle(): TextView = findView(R.id.royalty_item_tv_title)

    fun getTvContent(): TextView = findView(R.id.royalty_item_tv_content)
}

class Royalty : EVal<Royalty.TRoyalty>() {
    enum class TRoyalty {
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
        /**
         * 本地字段
         */
        select,
    }
}

fun royaltyVHSet(holder: RoyaltyVH?, royalty: Royalty, severTime: Long) {
    holder?.getTvMoney()?.text = royalty.getString(Royalty.TRoyalty.money)
    val createTime = royalty.getLong(Royalty.TRoyalty.createTime, 0)
    holder?.getTvTime()?.text =
            format(createTime, severTime, "MM-dd HH:mm", TimeFormatter.TimeFormat.from_y_to_m_24)
    holder?.getTvOrganizer()?.text = royalty.getString(Royalty.TRoyalty.acceptName)
    holder?.getTvTitle()?.text = royalty.getString(Royalty.TRoyalty.courseName)

    when (royalty.getInt(Royalty.TRoyalty.state, 0)) {
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
            holder?.getTvMoney()?.isEnabled = true
            holder?.getTvCurrency()?.isEnabled = true
            holder?.getTvContent()?.setTextColorRes(R.color.text_e43939)
            holder?.getTvContent()?.setText(R.string.wallet_state_reject)
        }
        RoyaltyType.succeed -> {
            holder?.getTvMoney()?.isEnabled = false
            holder?.getTvCurrency()?.isEnabled = false
            holder?.getTvContent()?.setTextColorRes(R.color.text_9699a2)
            val arrivalTime = royalty.getLong(Royalty.TRoyalty.arrivalTime, 0)
            val time = format(arrivalTime, severTime, "MM-dd", TimeFormatter.TimeFormat.simple_ymd)
            holder?.getTvContent()?.text = KotlinUtil.format(App.ct().getString(R.string.wallet_state_succeed),
                    time, royalty.getInt(Royalty.TRoyalty.arrivalMoney))
        }
    }
}

fun format(time1: Long, time2: Long, style1: String, style2: String): String {
    val style = if (time1 > time2 - TimeUnit.DAYS.toMillis(365)) style1 else style2
    return TimeFormatter.milli(time1, style)
}