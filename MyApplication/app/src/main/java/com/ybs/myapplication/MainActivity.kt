package com.ybs.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    val REQUEST_CODE = 11
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<RecorderCrop>(R.id.crop).setClick { b, s, e ->
            val str = "$s - $e"
            if (b) {
                Toast.makeText(this, "保留 - $str", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "删除 - $str", Toast.LENGTH_LONG).show()
            }
        }
        findViewById<Button>(R.id.button).setOnClickListener {
            checkSetting()
        }
    }


    private fun checkSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT).show()
                startActivityForResult(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    ), REQUEST_CODE
                )
            } else {
                show()
            }
        } else {
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show()
                } else {
                    show()
                }
            }
        }
    }

    private fun show() {
        MyWindowManager.createSmallWindow(this)
    }
}