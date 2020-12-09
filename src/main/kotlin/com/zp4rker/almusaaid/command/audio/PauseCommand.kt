package com.zp4rker.almusaaid.command.audio

import com.zp4rker.almusaaid.PLAYER
import com.zp4rker.almusaaid.TSCHEDULER
import com.zp4rker.almusaaid.audio.translateMillis
import com.zp4rker.dsc.core.command.Command
import com.zp4rker.dsc.core.extenstions.embed
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object PauseCommand : Command(aliases = arrayOf("pause")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val track = PLAYER.playingTrack ?: run {
            channel.sendMessage(embed {
                title { text = "No track playing right now!" }

                description = "The player needs to be playing to be paused."

                footer {
                    text = "Requested by ${message.author.name}"
                    iconUrl = message.author.effectiveAvatarUrl
                }

                color = "#ec644b"
            }).queue()
            return
        }

        if (TSCHEDULER.paused) {
            channel.sendMessage(embed {
                title { text = "Track already paused!" }

                description = "The player is not playing right now."

                footer {
                    text = "Requested by ${message.author.name}"
                    iconUrl = message.author.effectiveAvatarUrl
                }

                color = "#ec644b"
            }).queue()
            return
        }

        TSCHEDULER.paused = true

        channel.sendMessage(embed {
            title { text = "Paused player" }

            field {
                title = "Current position"
                text = "[${translateMillis(track.position)}/${translateMillis(track.duration)}]"
            }

            footer {
                text = "Requested by ${message.author.name}"
                iconUrl = message.author.effectiveAvatarUrl
            }
        }).queue()
    }

}