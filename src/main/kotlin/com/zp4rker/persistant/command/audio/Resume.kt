package com.zp4rker.persistant.command.audio

import com.zp4rker.discore.command.Command
import com.zp4rker.discore.extensions.embed
import com.zp4rker.persistant.PLAYER
import com.zp4rker.persistant.TSCHEDULER
import com.zp4rker.persistant.audio.translateMillis
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object Resume : Command() {

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