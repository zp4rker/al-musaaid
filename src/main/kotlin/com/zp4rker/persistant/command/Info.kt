package com.zp4rker.persistant.command

import com.zp4rker.persistant.startTime
import com.zp4rker.discore.API
import com.zp4rker.discore.DISCORE_VERSION
import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.command.Command
import com.zp4rker.discore.extensions.embed
import com.zp4rker.persistant.config
import net.dv8tion.jda.api.JDAInfo
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.lang.StringBuilder
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
object Info : Command() {

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
                text = Info::class.java.`package`.implementationVersion
            }

            field {
                title = "Discore Version"
                text = DISCORE_VERSION
            }

            field {
                title = "JDA Version"
                text = JDAInfo.VERSION
            }

            field {
                title = "Runtime"
                text = displayTime(runtime)
            }

            field {
                title = "Used Memory"
                text = Runtime.getRuntime().let { "${"%.2f".format((it.totalMemory() - it.freeMemory()) / 1048576.0)}MB / ${"%.2f".format(it.totalMemory() / 1048576.0)}MB" }
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

        val sb = StringBuilder()
        if (days > 0) sb.append("$days day${if (days == 1L) "" else "s"}")
        if (sb.toString().isNotBlank()) {
            if (minutes > 0 && seconds > 0) sb.append(", ") else sb.append(" and ")
        }
        if (hours > 0) sb.append("$hours hour${if (hours == 1L) "" else "s"}")
        if (sb.toString().isNotBlank()) {
            if (seconds > 0) sb.append(", ") else sb.append(" and ")
        }
        if (minutes > 0) sb.append("$minutes minute${if (minutes == 1L) "" else "s"}")
        if (sb.toString().isNotBlank()) {
            if (seconds > 0) sb.append(" and ")
        }
        if (seconds > 0) sb.append("$seconds second${if (seconds == 1L) "" else "s"}")
        return sb.toString()
    }
}