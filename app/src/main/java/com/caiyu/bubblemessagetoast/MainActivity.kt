package com.caiyu.bubblemessagetoast

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caiyu.bubblemessagetoastdemo.databinding.ActivityMainBinding
import com.monke.mopermission.MoPermission
import com.monke.mopermission.OnRequestNecessaryPermissionListener

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
    }

    private fun initView() {
        MoPermission.requestNecessaryPermission(this, "请求窗口权限", "","同意", "拒绝", object : OnRequestNecessaryPermissionListener {
            override fun fail(permissions: List<String>) {

            }

            override fun success(permissions: List<String>) {
                Toast.makeText(this@MainActivity, "权限获取成功", Toast.LENGTH_SHORT).show()
            }

        }, CustomPermissionDialog::class.java, Manifest.permission.SYSTEM_ALERT_WINDOW)

        mBinding.toastBtn1.setOnClickListener {
            BubbleMessageToast.show(this, "hello", BubbleMessageToast.COMMON)
        }

        mBinding.toastBtn2.setOnClickListener {
            BubbleMessageToast.show(this, "success", BubbleMessageToast.SUCCESS)
//            BubbleMessageToast.show(this, "success", BubbleMessageToast.LENGTH_SHORT, R.drawable.toast_background, R.drawable.success_icon)
        }

        mBinding.toastBtn3.setOnClickListener {
            BubbleMessageToast.show(this, "success", BubbleMessageToast.FAILED)
//            BubbleMessageToast.show(this, "failure", BubbleMessageToast.LENGTH_SHORT, R.drawable.toast_background, R.drawable.failed_icon)
        }

        mBinding.navigate.setOnClickListener {
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
        }
    }
}