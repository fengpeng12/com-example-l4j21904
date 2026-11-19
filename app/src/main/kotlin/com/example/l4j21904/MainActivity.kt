package com.example.l4j21904

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView

/** 空白页面：全面屏（内容铺到状态栏下方、状态栏透明），从零开始搭界面。 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = FrameLayout(this)
        val hint = TextView(this).apply {
            text = "哈哈哈"
            textSize = 22f
            gravity = Gravity.CENTER
        }
        root.addView(hint, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT))
        setContentView(root)
        goEdgeToEdge()
    }

    /**
     * 全面屏：内容铺到状态栏与导航栏下面，两条系统栏透明，并允许内容进入刘海区域。
     * **要在 setContentView 之后调用**：DecorView 是那时才创建的，过早调用会抛 NullPointerException；
     * 这里再兜一层：拿不到 DecorView 就直接跳过，避免启动闪退。
     */
    @Suppress("DEPRECATION")
    private fun goEdgeToEdge() {
        val decor: View? = window.decorView
        if (decor == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
    }

    /** 让指定 View 自己补上状态栏高度（顶部栏用它，标题就不会被状态栏盖住）。 */
    @Suppress("DEPRECATION")
    private fun padTopForStatusBar(view: View) {
        val base = view.paddingTop
        view.setOnApplyWindowInsetsListener { v, insets ->
            val top = if (Build.VERSION.SDK_INT >= 30) {
                insets.getInsets(WindowInsets.Type.statusBars()).top
            } else {
                insets.systemWindowInsetTop
            }
            v.setPadding(v.paddingLeft, base + top, v.paddingRight, v.paddingBottom)
            insets
        }
    }

    /** 让指定 View 自己补上导航栏高度（底部栏用它，就不会被手势条盖住）。 */
    @Suppress("DEPRECATION")
    private fun padBottomForNavBar(view: View) {
        view.setOnApplyWindowInsetsListener { v, insets ->
            val bottom = if (Build.VERSION.SDK_INT >= 30) {
                insets.getInsets(WindowInsets.Type.navigationBars()).bottom
            } else {
                insets.systemWindowInsetBottom
            }
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottom)
            insets
        }
    }
}
