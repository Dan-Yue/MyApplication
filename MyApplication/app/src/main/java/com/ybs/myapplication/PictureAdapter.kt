package com.ybs.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by DanYue on 2022/7/1 16:52.
 */
class PictureAdapter(mData: MutableList<PictureFile>, mContext: Context) : BaseAdapter() {
    private var mData: MutableList<PictureFile> = mutableListOf()
    private val mContext: Context
    private var block: (Int) -> Unit = {}

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(position: Int): Any {
        return mData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val contentView = convertView
            ?: LayoutInflater.from(mContext).inflate(
                R.layout.listview_item,
                parent, false
            )
        val img = contentView.findViewById<ImageView>(R.id.img)
        val txt = contentView.findViewById<TextView>(R.id.txt)
        val bean = mData[position]
        val path = bean.filePath
        img.setImageURI(Uri.parse(path))
        txt.text = bean.fileName
        return contentView
    }

    fun setList(list: MutableList<PictureFile>) {
        mData.addAll(list)
    }

    fun setOnClick(block: (Int) -> Unit) {
        this.block = block

//        adapter.setOnClick {
//            val intent = Intent()
//            intent.putExtra("image", list[it])
//            setResult(AppCompatActivity.RESULT_OK, intent)
//            finish()
//        }
    }

    init {
        this.mData = mData
        this.mContext = mContext
    }
}
