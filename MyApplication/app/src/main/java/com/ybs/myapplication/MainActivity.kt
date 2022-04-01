package com.ybs.myapplication

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import me.xfans.lib.voicewaveview.VoiceWaveView


class MainActivity : AppCompatActivity() {
    val tags = arrayOf(
        "com.hdyj.tooltest",
        "com.hdyj.settingmodule.ui.activity.NoticeActivity",
        "com.hdyj.tooltest.setting.NOTICE",
        "com.hdyj.settingmodule",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val voiceWaveView: VoiceWaveView = findViewById(R.id.voiceWaveView0)
        voiceWaveView.post {
            val unitWidth: Float = voiceWaveView.measuredWidth / 1052f
            voiceWaveView.apply {
                lineWidth = unitWidth * 20
                lineSpace = unitWidth * 66
                addBody(14)
                addBody(25)
                addBody(31)
                addBody(48)
                addBody(49)
                addBody(43)
                addBody(81)
                addBody(43)
                addBody(49)
                addBody(49)
                addBody(31)
                addBody(25)
                addBody(14)
                start()
            }
        }
        findViewById<Button>(R.id.notice_btn).setOnClickListener {
            var t = findViewById<EditText>(R.id.notice_edt).text.toString()
            if (t.isEmpty()) {
                t = "1"
            }
            val intent = Intent()
            val comp = ComponentName(tags[0], tags[1])
            intent.component = comp
            intent.putExtra("notice_type", t.toInt())
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.action = tags[2]
            startActivity(intent)
        }
    }
}