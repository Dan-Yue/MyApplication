package com.ybs.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File

/**
 * Created by DanYue on 2022/7/2 14:40.
 */
object PictureUtil {
    private const val mt = MediaStore.Images.Media.MIME_TYPE
    private const val dm = MediaStore.Images.Media.DATE_MODIFIED
    private const val _d = MediaStore.Images.Media.DATA
    private const val dn = MediaStore.Images.Media.DISPLAY_NAME
    private val ecu: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    private val imageTypes = mutableListOf("image/jpeg", "image/png", "image/gif", "image/bmp")

    @SuppressLint("Recycle")
    private fun getImages(context: Context) {
        val paths = mutableListOf<PictureFile>()
        val sql = "$mt=? or $mt=?"
        val types = imageTypes.toTypedArray()
        val cursor = context.contentResolver.query(ecu, null, sql, types, dm)
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
            }
        }
    }

    private fun getFolder(fileList: MutableList<PictureFile>) {
        val folderMap = mutableMapOf<String, PictureFolder>()
        fileList.forEach { picture ->
            if (folderMap.containsKey(picture.folderPath)) {
                val pictureFolder = folderMap[picture.folderPath]
                pictureFolder!!.folderNumber += 1
                pictureFolder!!.folderList.add(picture)
            } else {
                val pictureFolder = PictureFolder(
                    picture.folderName, 1, picture.folderPath, picture,
                    mutableListOf(picture)
                )
                folderMap[picture.folderPath] = pictureFolder
            }
        }
    }

    private fun getDate(fileList: MutableList<PictureFile>) {
        val dateMap = mutableMapOf<String, PictureDate>()
        fileList.forEach { picture ->
            if (dateMap.containsKey(getDateStr(picture.fileDate))) {
                val pictureDate = dateMap[getDateStr(picture.fileDate)]
                pictureDate!!.folderList.add(picture)
            } else {
                val pictureDate = PictureDate(getDateStr(picture.fileDate), mutableListOf(picture))
                dateMap[getDateStr(picture.fileDate)] = pictureDate
            }
        }
    }

    private fun getDateStr(time: Int): String {
        return ""
    }
}