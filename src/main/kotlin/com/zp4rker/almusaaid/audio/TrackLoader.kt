package com.zp4rker.almusaaid.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.zp4rker.almusaaid.TRACKS
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
class TrackLoader(private val channel: TextChannel): AudioLoadResultHandler {

    override fun trackLoaded(track: AudioTrack) {
        channel.sendMessage("Loaded track: ${track.info.title}").queue()
        TRACKS.queue(track)
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        // add playlist load msg
        playlist.tracks.forEach(TRACKS::queue)
    }

    override fun noMatches() {
        // add no match msg
    }

    override fun loadFailed(exception: FriendlyException) {
        // add fail msg
    }
}