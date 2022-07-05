package com.ybs.myapplication

/**
 * Created by DanYue on 2022/7/2 14:36.
 */
data class PictureFolder(
    var folderName: String = "", //文件夹名
    var folderNumber: Int = 0, //文件夹图片数量
    var folderPath: String = "", //文件夹路径
    var folderFirst: PictureFile?, //文件夹首张图片实体
    var folderList: MutableList<PictureFile> = mutableListOf(), //文件夹图片实体列表
    val checkNumber: Int = 0 //选中的图片数量
)