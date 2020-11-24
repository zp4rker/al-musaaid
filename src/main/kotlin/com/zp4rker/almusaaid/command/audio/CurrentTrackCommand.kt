package com.zp4rker.almusaaid.command.audio

import com.zp4rker.almusaaid.PLAYER
import com.zp4rker.disbot.command.Command
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
object CurrentTrackCommand : Command(aliases = arrayOf("currenttrack", "nowplaying")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val track = PLAYER.playingTrack

        if (track == null) {
            channel.sendMessage("No tracks playing currently!").queue()
        } else {
            val position = toTimeString(track.position)
            val duration = toTimeString(track.duration)
            channel.sendMessage("Currently playing: `${track.info.title}` [$position/$duration]").queue()
        }
    }

    private fun toTimeString(millis: Long) = run {
        var seconds = millis / 1000
        val minutes = TimeUnit.SECONDS.toMinutes(seconds).also { seconds -= TimeUnit.MINUTES.toSeconds(it) }
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }

}