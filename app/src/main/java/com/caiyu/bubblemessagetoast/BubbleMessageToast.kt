package com.caiyu.bubblemessagetoast

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.caiyu.bubblemessagetoast.databinding.BubbleMessageToastLayoutBinding
import java.lang.ref.WeakReference


class BubbleMessageToast private constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr), DefaultLifecycleObserver {
    private val mBinding by lazy {
        BubbleMessageToastLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }
    private val dismissAction = Runnable { dismiss() }
    private val mainHandler = Handler(Looper.getMainLooper())

    companion object {
        const val SUCCESS = 0
        const val FAILED = -1
        const val COMMON = 1
        const val LENGTH_SHORT = 2000L
        const val LENGTH_LONG = 4000L
        private var currentRef: WeakReference<BubbleMessageToast>? = null

        @JvmStatic
        fun show(context: Context, message: String, type: Int = SUCCESS, duration: Long = LENGTH_SHORT) {
            require(context is Activity || context is LifecycleOwner) {
                "Context must be an Activity and implement LifecycleOwner"
            }

            currentRef?.get()?.cancel()
            currentRef = WeakReference(BubbleMessageToast(context))
            currentRef?.get()?.apply {
                bindLifeCycle(context)
                mBinding.toastMessage.text = message
                when (type) {
                    SUCCESS -> {
                        mBinding.toastIcon.setImageResource(R.drawable.success_icon)
                    }
                    FAILED -> {
                        mBinding.toastIcon.setImageResource(R.drawable.failed_icon)
                    }
                    COMMON -> {
                        mBinding.toastIcon.visibility = View.GONE
                    }
                }
                show(message, duration)
            }
        }

        @JvmStatic
        fun show(context: Context, message: String, duration: Long, @DrawableRes rootBg: Int, @DrawableRes icon: Int) {
            require(context is Activity || context is LifecycleOwner) {
                "Context must be an Activity and implement LifecycleOwner"
            }

            currentRef?.get()?.cancel()
            currentRef = WeakReference(BubbleMessageToast(context))
            currentRef?.get()?.apply {
                bindLifeCycle(context)
                mBinding.toastMessage.text = message
                mBinding.root.setBackgroundResource(rootBg)
                mBinding.toastIcon.setImageResource(icon)
                show(message, duration)
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        cancel()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        cancel()
        owner.lifecycle.removeObserver(this)
    }

    private fun bindLifeCycle(context: Context) {
        (context as? LifecycleOwner)?.lifecycle?.addObserver(this)
    }

    private fun show(message: String, duration: Long = LENGTH_SHORT) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post { show(message, duration) }
            return
        }

        val rootView = (context as Activity).window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val params = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.CENTER_HORIZONTAL
        params.topMargin = getStatusBarHeight(context)

        rootView.addView(this, params)
        show(duration)
    }

    private fun show(duration: Long) {
        mainHandler.removeCallbacks(dismissAction)

        mainHandler.postDelayed(dismissAction, duration)
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)

                alpha = 0f
                translationY = -height.toFloat()
                this@BubbleMessageToast.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .start()
            }
        })
    }

    private fun cancel() {
        if (currentRef?.get() == this) currentRef = null
        mainHandler.post {
            removeFromParent()
        }
    }

    private fun dismiss() {
        if (currentRef?.get() == this) currentRef = null

        animate()
            .alpha(0f)
            .translationY(-height.toFloat())
            .setDuration(200)
            .withEndAction {
                removeFromParent()
            }
            .start()
    }

    private fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0 ) {
            context.resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    private fun removeFromParent() {
        parent?.let {
            (it as ViewGroup).removeView(this)
        }
    }

    override fun onDetachedFromWindow() {
        mainHandler.removeCallbacksAndMessages(null)
        mBinding.toastIcon.setImageDrawable(null)
        mBinding.toastIcon.background = null
        super.onDetachedFromWindow()
    }
}