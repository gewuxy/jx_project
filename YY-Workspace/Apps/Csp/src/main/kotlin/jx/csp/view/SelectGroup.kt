package jx.csp.view

import android.content.Context
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RelativeLayout

/**
 * 右上角添加选择器
 * @auther : GuoXuan
 * @since : 18/3/9
 */
class SelectGroup(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val mNativeView: ImageView by lazy {
        ImageView(context)
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)

        for (i in 0 until childCount) {
            getChildAt(i)?.isSelected = selected
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        val half = 2
        val halfWith = mNativeView.measuredWidth / half
        val halfHeight = mNativeView.measuredHeight / half

        val left = measuredWidth - halfWith
        val right = measuredWidth + halfWith
        val top = -halfHeight
        val bottom = halfHeight

        mNativeView.layout(left, top, right, bottom)
    }

    fun addSelectRes(@DrawableRes drawableRes: Int, w: Int, h: Int) {
        addView(mNativeView, w, h)
        mNativeView.setImageResource(drawableRes)
    }

}
