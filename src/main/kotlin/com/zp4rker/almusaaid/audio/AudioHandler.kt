package com.zp4rker.almusaaid.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

/**
 * @author zp4rker
 */
class AudioHandler(private val audioPlayer: AudioPlayer) : AudioSendHandler {

    private var frame: AudioFrame? = null

    override fun canProvide(): Boolean {
        frame = audioPlayer.provide()
        return frame != null
    }

    override fun provide20MsAudio(): ByteBuffer {
        return ByteBuffer.wrap(frame!!.data)
    }

    override fun isOpus(): Boolean {
        return true
    }

}