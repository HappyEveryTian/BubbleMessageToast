package com.caiyu.bubblemessagetoast

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.caiyu.bubblemessagetoast.databinding.BubbleMessageToastLayoutBinding
import java.lang.ref.WeakReference


class BubbleMessageToast private constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr) {
    private val mBinding by lazy {
        BubbleMessageToastLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }
    private val dismissAction = Runnable { dismiss() }

    companion object {
        const val SUCCESS = 0
        const val FAILED = -1
        const val COMMON = 1
        const val LENGTH_SHORT = 2000L
        const val LENGTH_LONG = 4000L
        private var currentRef: WeakReference<BubbleMessageToast>? = null

        @JvmStatic
        fun show(context: Context, message: String, type: Int = SUCCESS, duration: Long = LENGTH_SHORT) {
            BubbleMessageToast(context).show(message, type, duration)
        }
    }

    private fun show(message: String, type: Int, duration: Long = LENGTH_SHORT) {
        mBinding.toastMessage.text = message
        when (type) {
            SUCCESS -> {
                mBinding.toastIcon.setBackgroundResource(R.drawable.success_icon)
            }
            FAILED -> {
                mBinding.toastIcon.setBackgroundResource(R.drawable.failed_cion)
            }
            else -> {
                mBinding.root.removeView(mBinding.toastIcon)
            }
        }

        if (Looper.myLooper() != Looper.getMainLooper()) {
            val handler = Handler(Looper.getMainLooper())
            handler.post { show(context, message) }
            return
        }

        currentRef?.get()?.dismiss()

        val rootView = (context as Activity).window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val params = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.CENTER_HORIZONTAL
        params.topMargin = getStatusBarHeight(context)

        rootView.addView(this, params)
        currentRef = WeakReference(this)
        show(duration)
    }

    private fun show(duration: Long) {
        removeCallbacks(dismissAction)

        postDelayed(dismissAction, duration)

        alpha = 0f
        translationY = -height.toFloat()
        animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .start()
    }

    private fun dismiss() {
        animate()
            .alpha(0f)
            .translationY(-height.toFloat())
            .setDuration(200)
            .withEndAction {
                parent?.let {
                    (it as ViewGroup).removeView(this)
                }
            }
            .start()
    }

    private fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0 ) {
            context.resources.getDimensionPixelSize(resourceId)
        } else 0
    }
}