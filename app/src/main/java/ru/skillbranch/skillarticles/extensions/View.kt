package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

fun View.setMarginOptionally(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) {
    val param = this.layoutParams as MarginLayoutParams
    param.setMargins(left, top, right, bottom)
    this.layoutParams = param
}

//fun View.setMarginOptionally(
//    left: Int = marginLeft,
//    top : Int = marginTop,
//    right : Int = marginRight,
//    bottom : Int = marginBottom) {
//    layoutParams<MarginLayoutParams> {
//        left.run { leftMargin = this }
//        top.run { topMargin = this }
//        right.run { rightMargin = this }
//        bottom.run { bottomMargin = this }
//    }
//}
//
//inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
//    if (layoutParams is T) block(layoutParams as T)
//}
