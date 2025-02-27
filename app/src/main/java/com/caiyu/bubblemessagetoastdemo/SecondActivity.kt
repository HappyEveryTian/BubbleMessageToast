package com.caiyu.bubblemessagetoastdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.caiyu.bubblemessagetoastdemo.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private val mBinding by lazy {
        ActivitySecondBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initView()
    }

    private fun initView() {
        mBinding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}