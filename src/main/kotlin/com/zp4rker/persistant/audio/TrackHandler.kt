package com.zp4rker.persistant.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.zp4rker.persistant.PLAYER
import com.zp4rker.discore.API
import com.zp4rker.discore.extenstions.embed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author zp4rker
 */
class TrackHandler : AudioEventAdapter() {

    private val queue = LinkedBlockingQueue<TrackData>()

    init {
        PLAYER.addListener(this)
    }

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
        val data = queue.peek()
        data.channel.sendMessage(embed {
            title { text = "Started playing track" }

            description = "```${track.info.title}```"

            field {
                title = "Duration"
                text = translateMillis(track.duration)
            }

            footer {
                text = "Requested by ${data.requester.name}"
                iconUrl = data.requester.effectiveAvatarUrl
            }
        }).queue()
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        val data = queue.take()

        if (endReason != AudioTrackEndReason.STOPPED) {
            data.channel.sendMessage(embed {
                title { text = "Finished playing track" }

                description = "```${track.info.title}```"

                footer {
                    text = "Requested by ${data.requester.name}"
                    iconUrl = data.requester.effectiveAvatarUrl
                }
            }).queue()
        }

        if (endReason.mayStartNext) nextTrack()
    }

    fun queue(track: AudioTrack, channel: TextChannel, requester: User) {
        val data = TrackData(track, channel, requester)
        queue.offer(data)
        PLAYER.startTrack(track, true)
    }

    fun nextTrack() {
        if (queue.peek() == null) API.guilds.forEach { it.audioManager.closeAudioConnection() }
        else PLAYER.startTrack(queue.peek()?.track, false)
    }

    fun restartTrack() {
        PLAYER.playingTrack?.position = 0
    }

    fun getQueue(): Array<TrackData> = queue.toArray(arrayOf())

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