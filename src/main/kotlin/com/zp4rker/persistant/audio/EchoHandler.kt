package com.zp4rker.persistant.audio

import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.audio.CombinedAudio
import java.nio.ByteBuffer
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author zp4rker
 */
class EchoHandler : AudioReceiveHandler, AudioSendHandler {
    private val queue = LinkedBlockingQueue<ByteArray>()

    private val toAdd = mutableListOf<ByteArray>()
    private var lastSound: Instant = Instant.now()

    override fun canReceiveCombined(): Boolean = queue.size < 10

    override fun handleCombinedAudio(combinedAudio: CombinedAudio) {
        if (combinedAudio.users.isNotEmpty()) {
            toAdd.add(combinedAudio.getAudioData(1.0))
            if (lastSound.until(Instant.now(), ChronoUnit.MILLIS) > 500) lastSound = Instant.now()
        }

        if (combinedAudio.users.isEmpty() && toAdd.isNotEmpty() && lastSound.until(Instant.now(), ChronoUnit.MILLIS) > 500) {
            queue.addAll(toAdd)
            toAdd.clear()
        }
    }

    override fun canProvide(): Boolean = !queue.isEmpty()

    override fun provide20MsAudio(): ByteBuffer? = queue.poll()?.let { ByteBuffer.wrap(it) }

    override fun isOpus(): Boolean = false
}