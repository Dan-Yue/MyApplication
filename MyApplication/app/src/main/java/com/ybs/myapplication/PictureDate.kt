package com.ybs.myapplication

/**
 * Created by DanYue on 2022/7/5 10:13.
 */
data class PictureDate(
    val dateStr: String = "",//日期文字
    var folderList: MutableList<PictureFile> = mutableListOf() //文件夹图片实体列表
)