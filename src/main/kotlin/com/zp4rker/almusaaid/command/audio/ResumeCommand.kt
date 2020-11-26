package com.zp4rker.almusaaid.command.audio

import com.zp4rker.almusaaid.PLAYER
import com.zp4rker.almusaaid.TSCHEDULER
import com.zp4rker.almusaaid.audio.translateMillis
import com.zp4rker.disbot.command.Command
import com.zp4rker.disbot.extenstions.embed
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object ResumeCommand : Command(aliases = arrayOf("resume")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val track = PLAYER.playingTrack ?: run {
            channel.sendMessage(embed {
                title { text = "No track paused right now!" }

                description = "The player needs to be playing to be resumed."

                footer {
                    text = "Requested by ${message.author.name}"
                    iconUrl = message.author.effectiveAvatarUrl
                }

                color = "#ec644b"
            }).queue()
            return
        }

        if (!TSCHEDULER.paused) {
            channel.sendMessage(embed {
                title { text = "Track already playing!" }

                description = "The player is not paused right now."

                footer {
                    text = "Requested by ${message.author.name}"
                    iconUrl = message.author.effectiveAvatarUrl
                }

                color = "#ec644b"
            }).queue()
            return
        }

        TSCHEDULER.paused = false

        channel.sendMessage(embed {
            title { text = "Resumed player" }

            field {
                name = "Current position"
                value = "[${translateMillis(track.position)}/${translateMillis(track.duration)}]"
            }

            footer {
                text = "Requested by ${message.author.name}"
                iconUrl = message.author.effectiveAvatarUrl
            }
        }).queue()
    }

}