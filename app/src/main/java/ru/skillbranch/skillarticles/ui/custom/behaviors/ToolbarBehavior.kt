package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.MaterialToolbar
import kotlin.math.min

class ToolbarBehavior() : CoordinatorLayout.Behavior<MaterialToolbar>() {
    constructor(context: Context, attrs: AttributeSet) : this()

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: MaterialToolbar,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: MaterialToolbar,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        val offset = min(0f, min(child.height.toFloat(), child.translationY - dy))
        if (offset != child.translationY) child.translationY = offset
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }
}