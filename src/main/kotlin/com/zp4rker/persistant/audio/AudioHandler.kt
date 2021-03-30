package com.zp4rker.persistant.audio

import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import com.zp4rker.persistant.PLAYER
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

/**
 * @author zp4rker
 */
class AudioHandler : AudioSendHandler {
    private var frame: AudioFrame? = null

    override fun canProvide(): Boolean {
        frame = PLAYER.provide()
        return frame != null
    }

    override fun provide20MsAudio(): ByteBuffer {
        return ByteBuffer.wrap(frame!!.data)
    }

    override fun isOpus(): Boolean {
        return true
    }
}