package com.caiyu.bubblemessagetoast

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.caiyu.bubblemessagetoast.databinding.ActivityMainBinding
import com.monke.mopermission.MoPermission
import com.monke.mopermission.MoPermissionDialog
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

        }, MoPermissionDialog::class.java, Manifest.permission.SYSTEM_ALERT_WINDOW)

        mBinding.toastBtn1.setOnClickListener {
            BubbleMessageToast.show(this, "hello", BubbleMessageToast.COMMON)
        }

        mBinding.toastBtn2.setOnClickListener {
            BubbleMessageToast.show(this, "success", BubbleMessageToast.SUCCESS)
        }

        mBinding.toastBtn3.setOnClickListener {
            BubbleMessageToast.show(this, "failure", BubbleMessageToast.FAILED)
        }
    }
}