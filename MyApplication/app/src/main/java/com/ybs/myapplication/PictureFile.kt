package com.ybs.myapplication

/**
 * Created by DanYue on 2022/7/2 14:33.
 */
data class PictureFile(
    var fileName: String = "", //图片名
    var filePath: String = "", //图片路径
    var fileDate: Int = 0, //图片日期
    var folderName: String = "", //图片文件夹名
    var folderPath: String = "", //图片文件夹路径
    var isCheck: Boolean = false //图片是否选中
)
