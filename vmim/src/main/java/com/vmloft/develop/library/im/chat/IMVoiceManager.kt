package com.vmloft.develop.library.im.chat

import android.media.MediaPlayer
import android.widget.ImageView

import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.tools.utils.logger.VMLog
import com.vmloft.develop.library.tools.widget.VMWaveView

/**
 * Create by lzan13 on 2022/7/3
 * 描述：语音消息播放管理类
 */
object IMVoiceManager {

    private var currMessage: IMMessage? = null // 当前播放的消息
    private var currPlayIV: ImageView? = null // 当前播放的图标
    private var currWaveView: VMWaveView? = null // 当前播放的波形图

    private var mediaPlayer: MediaPlayer? = null

    /**
     * 初始化媒体播放
     */
    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnCompletionListener {
            VMLog.i("播放结束")
            stop()
        }
        mediaPlayer?.setOnErrorListener { player, what, extra ->
            VMLog.e("播放出错 $what $extra")
            stop()
            true
        }
        mediaPlayer?.setOnPreparedListener {
            VMLog.i("准备好了")
            play()
        }
    }

    /**
     * 准备播放
     */
    fun preparePlay(msg: IMMessage, playIV: ImageView, waveView: VMWaveView) {
        // 如果是同一个，则不处理停止，因为stop会重置，这里要放在前边
        if (msg == currMessage) {
            stop()
            return
        }
        // 如果之前存在播放中的消息，则需要停止
        currMessage?.let { stop() }

        if (mediaPlayer == null) {
            initMediaPlayer()
        }

        currMessage = msg
        currPlayIV = playIV
        currWaveView = waveView

        val attachment = msg.attachments[0]
        attachment.uri?.let {
            // 设置音源并异步准备
            mediaPlayer?.setDataSource(playIV.context, it)
            mediaPlayer?.prepareAsync()
        }
    }

    /**
     * 开始播放，这里主要是显示动画
     */
    fun play() {
        currPlayIV?.setImageResource(R.drawable.ic_pause)
        currWaveView?.start()

        mediaPlayer?.start()
    }

    /**
     * 停止播放
     */
    fun stop() {
        currMessage = null
        currPlayIV?.setImageResource(R.drawable.ic_play)
        currPlayIV = null
        currWaveView?.stop()
        currWaveView = null

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}