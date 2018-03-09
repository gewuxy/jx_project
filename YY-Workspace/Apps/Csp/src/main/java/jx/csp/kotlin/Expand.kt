package jx.csp.kotlin

import android.content.Context
import android.text.ClipboardManager

/**
 * kotlin 的拓展
 * @auther : GuoXuan
 * @since : 2018/3/8
 */

fun String.copyToBoard(context: Context) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.text = this
}
