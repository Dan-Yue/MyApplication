package com.ybs.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File


class MainActivity : AppCompatActivity() {
    private val adapter: PictureAdapter = PictureAdapter(mutableListOf(), this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter
        permissionOpen()
    }

    private fun permissionOpen(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //动态申请读写权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                101
            )
        } else {
            getImages() //有权限的话直接去获取手机图片
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImages() //申请权限成功之后，去获取手机图片
            } else {
                Toast.makeText(this, "权限申请失败", Toast.LENGTH_LONG).show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private val mt = MediaStore.Images.Media.MIME_TYPE
    private val ecu = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    private val dm = MediaStore.Images.Media.DATE_MODIFIED
    private val _d = MediaStore.Images.Media.DATA
    private val dn = MediaStore.Images.Media.DISPLAY_NAME

    @SuppressLint("Recycle")
    private fun getImages() {
        val paths = mutableListOf<PictureFile>()
        val sql = "$mt=? or $mt=?"
        val types = mutableListOf("image/jpeg", "image/png").toTypedArray()
        val cursor = contentResolver.query(ecu, null, sql, types, dm)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获取图片的名称
                val fileName = cursor.getString(cursor.getColumnIndexOrThrow(dn))
                // 获取图片的绝对路径
                val filePath = cursor.getString(cursor.getColumnIndexOrThrow(_d))
                // 获取图片的日期
                val fileDate = cursor.getInt(cursor.getColumnIndexOrThrow(dm))
                // 获取文件夹名
                val parentName = File(filePath).parentFile?.name ?: ""
                // 获取文件夹路径
                val parentPath = File(filePath).parent ?: ""
                paths.add(PictureFile(fileName, filePath, fileDate, parentName, parentPath))
                Log.i(
                    "GetImagesPath",
                    "size = ${paths.size}: name = $fileName  path = $filePath desc = $fileDate"
                )
            }
        }
        adapter.setList(paths)
        adapter.notifyDataSetChanged()
    }
}