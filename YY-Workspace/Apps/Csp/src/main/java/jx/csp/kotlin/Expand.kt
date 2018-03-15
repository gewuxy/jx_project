package jx.csp.kotlin

import android.content.ClipboardManager
import android.content.Context
import android.support.annotation.ColorRes
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import lib.ys.util.res.ResLoader

/**
 * kotlin 的拓展
 * @auther : GuoXuan
 * @since : 2018/3/8
 */

fun String.copyToBoard(context: Context) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    cm?.text = this
}

fun TextView.setTextColorRes(@ColorRes resId: Int) {
    setTextColor(ResLoader.getColor(resId))
}

fun View.topMargin(top: Int) {
    when (layoutParams) {
        is ViewGroup.MarginLayoutParams -> {
            (layoutParams as ViewGroup.MarginLayoutParams).topMargin = top
        }
    }
}
