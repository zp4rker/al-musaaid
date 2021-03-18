package com.zp4rker.persistant.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.zp4rker.persistant.AHANDLER
import com.zp4rker.persistant.TSCHEDULER
import com.zp4rker.discore.extensions.embed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
class TrackLoader(private val channel: TextChannel, private val requester: User) : AudioLoadResultHandler {

    override fun trackLoaded(track: AudioTrack) {
        if (!TSCHEDULER.getQueue().isNotEmpty()) {
            channel.sendMessage(embed {
                title { text = "Added track to queue" }

                description = "```${track.info.title}```"

                field {
                    title = "Duration"
                    text = translateMillis(track.duration)
                }
            }).queue()
        }

        val audioManager = channel.guild.audioManager
        if (audioManager.sendingHandler != AHANDLER) audioManager.sendingHandler = AHANDLER
        if (!audioManager.isConnected) audioManager.openAudioConnection(channel.guild.voiceChannels.first())

        TSCHEDULER.queue(track, channel, requester)
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        channel.sendMessage(embed {
            title { text = "Added tracks from playlist to queue" }

            description = "```${playlist.name}```"

            field {
                title = "Amount of tracks"
                text = "${playlist.tracks.size}"
            }

            field {
                title = "Total duration of playlist"
                text = translateMillis(playlist.tracks.sumOf { it.duration })
            }

            footer {
                text = "Requested by ${requester.name}"
                iconUrl = requester.effectiveAvatarUrl
            }
        }).queue()

        playlist.tracks.forEach { TSCHEDULER.queue(it, channel, requester) }
    }

    override fun noMatches() {
        channel.sendMessage(embed {
            title { text = "No matches found from that URL" }

            footer {
                text = "Requested by ${requester.name}"
                iconUrl = requester.effectiveAvatarUrl
            }

            color = "#ec644b"
        }).queue()
    }

    override fun loadFailed(exception: FriendlyException) {
        channel.sendMessage(embed {
            title { text = "Failed to load track!" }

            description = "```${exception.message}```"

            footer {
                text = "Requested by ${requester.name}"
                iconUrl = requester.effectiveAvatarUrl
            }

            color = "#ec644b"
        }).queue()
    }
}

fun translateMillis(millis: Long): String {
    var seconds = millis / 1000
    val minutes = TimeUnit.SECONDS.toMinutes(seconds).also { seconds -= TimeUnit.MINUTES.toSeconds(it) }

    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}