package com.zp4rker.persistant.command.audio

import com.zp4rker.discore.command.Command
import com.zp4rker.discore.extenstions.embed
import com.zp4rker.persistant.PLAYER
import com.zp4rker.persistant.audio.translateMillis
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
object Jump : Command(aliases = arrayOf("jump", "j"), args = arrayOf("")) {
    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val track = PLAYER.playingTrack ?: run {
            channel.sendMessage(embed {
                title { text = "No track playing right now!" }

                description = "The player needs to be playing."

                footer {
                    text = "Requested by ${message.author.name}"
                    iconUrl = message.author.effectiveAvatarUrl
                }

                color = "#ec644b"
            }).queue()
            return
        }

        val input = args[0].split(":")

        val mins = if (input.size == 1) 0L else input[0].toLong()
        val seconds = (if (input.size == 1) input[0] else input[1]).toLong()

        val millis = TimeUnit.SECONDS.toMillis(seconds) + TimeUnit.MINUTES.toMillis(mins)

        track.position = millis

        channel.sendMessage(embed {
            title { text = "Jumped in track" }

            description = "Now playing from `[${translateMillis(millis)}/${translateMillis(track.duration)}]`"
        }).queue()
    }
}