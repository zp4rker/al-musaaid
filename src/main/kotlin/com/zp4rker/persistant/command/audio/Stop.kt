package com.zp4rker.persistant.command.audio

import com.zp4rker.discore.command.Command
import com.zp4rker.discore.extensions.embed
import com.zp4rker.persistant.PLAYER
import com.zp4rker.persistant.TSCHEDULER
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object Stop : Command() {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        if (PLAYER.playingTrack == null) {
            channel.sendMessage(embed {
                title { text = "No track playing right now!" }

                description = "No tracks playing to be stopped."

                footer {
                    text = "Requested by ${message.author.name}"
                    iconUrl = message.author.effectiveAvatarUrl
                }

                color = "#ec644b"
            }).queue()
            return
        }

        PLAYER.stopTrack()
        TSCHEDULER.clearQueue()
        channel.guild.audioManager.closeAudioConnection()

        channel.sendMessage(embed {
            title { text = "Stopped player" }

            description = "Stopped player and cleared queue."

            footer {
                text = "Requested by ${message.author.name}"
                iconUrl = message.author.effectiveAvatarUrl
            }
        }).queue()
    }

}