package com.zp4rker.almusaaid.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.zp4rker.almusaaid.PLAYER
import com.zp4rker.almusaaid.PLAYERMANAGER
import com.zp4rker.almusaaid.TRACKS
import com.zp4rker.disbot.API
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author zp4rker
 */
class TrackHandler : AudioEventAdapter() {

    private val queue = LinkedBlockingQueue<AudioTrack>()

    init {
        PLAYER.addListener(this)
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) nextTrack()
    }

    fun queue(track: AudioTrack) {
        if (!PLAYER.startTrack(track, true)) {
            queue.offer(track)
        }
    }

    fun nextTrack() {
        if (queue.peek() == null) API.guilds.forEach { it.audioManager.closeAudioConnection() }
        else PLAYER.startTrack(queue.poll(), false)
    }

    fun restartTrack() {
        PLAYER.playingTrack.position = 0
    }

    fun getQueue(): Array<AudioTrack> = queue.toArray(arrayOf())

    fun clearQueue() {
        queue.clear()
    }

    var volume: Int
        set(value) {
            PLAYER.volume = value
        }
        get() = PLAYER.volume

    var paused: Boolean
        set(value) {
            PLAYER.isPaused = value
        }
        get() = PLAYER.isPaused

}