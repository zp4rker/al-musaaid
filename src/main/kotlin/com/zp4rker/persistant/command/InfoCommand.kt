package com.zp4rker.persistant.command

import com.zp4rker.persistant.startTime
import com.zp4rker.discore.API
import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.MANIFEST
import com.zp4rker.discore.command.Command
import com.zp4rker.discore.extenstions.embed
import com.zp4rker.persistant.config
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.lang.StringBuilder
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
object InfoCommand : Command(aliases = arrayOf("info")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val self = API.selfUser
        val creator = API.getUserByTag(config.owner) ?: return
        val runtime = Instant.now().epochSecond - startTime.epochSecond

        val embed = embed {
            author {
                name = self.name
                iconUrl = self.avatarUrl
            }

            field {
                title = "Version"
                text = InfoCommand::class.java.`package`.implementationVersion
            }

            field {
                title = "Discore Version"
                text = MANIFEST.getValue("Discore-Version")
            }

            field {
                title = "JDA Version"
                text = MANIFEST.getValue("JDA-Version")
            }

            field {
                title = "Runtime"
                text = displayTime(runtime)
            }

            footer {
                text = "Created by ${creator.asTag}"
                iconUrl = creator.avatarUrl
            }
        }

        LOGGER.debug("should be sending embed")

        channel.sendMessage(embed).queue()
    }

    private fun displayTime(totalSeconds: Long): String {
        var seconds = totalSeconds
        val days = TimeUnit.SECONDS.toDays(seconds).also { seconds -= TimeUnit.DAYS.toSeconds(it) }
        val hours = TimeUnit.SECONDS.toHours(seconds).also { seconds -= TimeUnit.HOURS.toSeconds(it) }
        val minutes = TimeUnit.SECONDS.toMinutes(seconds).also { seconds -= TimeUnit.MINUTES.toSeconds(it) }
        return StringBuilder().apply {
            if (days > 0) append("${days}d")
            if (hours > 0) append("${hours}d")
            if (minutes > 0) append("${minutes}m")
            if (seconds > 0) append("${seconds}s")
        }.toString()
    }
}