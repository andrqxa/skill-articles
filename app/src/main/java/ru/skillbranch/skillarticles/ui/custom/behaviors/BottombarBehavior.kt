package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.MaterialToolbar
import ru.skillbranch.skillarticles.ui.custom.Bottombar
import kotlin.math.min

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
        val offset = when (child) {
            is Bottombar -> MathUtils.clamp(child.translationY + dy, 0f, child.height.toFloat())
            is MaterialToolbar -> min(0f, min(child.height.toFloat(), child.translationY - dy))
            else -> 0f
        }
        if (offset != child.translationY) child.translationY = offset
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }
}
