package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat

class BottombarBehavior<V : View>() : CoordinatorLayout.Behavior<V>() {
    constructor(context: Context, attrs: AttributeSet) : this()

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        val offset = MathUtils.clamp(child.translationY + dy, 0f, child.height.toFloat())
        if (offset != child.translationY) child.translationY = offset
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }
}
