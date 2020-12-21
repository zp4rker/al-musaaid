package com.zp4rker.almusaaid.command.audio

import com.zp4rker.almusaaid.TSCHEDULER
import com.zp4rker.almusaaid.audio.translateMillis
import com.zp4rker.discore.command.Command
import com.zp4rker.discore.extenstions.embed
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.time.Instant

/**
 * @author zp4rker
 */
object QueueCommand : Command(aliases = arrayOf("queue", "q")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val trackList = TSCHEDULER.getQueue().map { it.track }

        val listString = if (trackList.isEmpty()) {
            "No items currently in queue."
        } else {
            trackList.mapIndexed { i, t ->
                "${if (i > 0) "\n$i. " else ""}${t.info.title} ${if (i == 0) "[Currently playing]" else ""}"
            }.joinToString("\n\n")
        }

        channel.sendMessage(embed {
            title { text = "Current queue" }

            description = "```$listString```"

            field {
                title = "Total duration"
                text = translateMillis(trackList.sumOf { it.duration })
            }

            val durationRemaining = trackList[0].run { duration - position } + trackList.drop(1).sumOf { it.duration }

            field {
                title = "Tracks remaining"
                text = "${trackList.size - 1} (${translateMillis(durationRemaining)})"
            }

            footer { text = "Finishes" }

            timestamp = Instant.now().plusMillis(durationRemaining)
        }).queue()
    }

}