package com.zp4rker.persistant.command

import com.zp4rker.persistant.startTime
import com.zp4rker.discore.API
import com.zp4rker.discore.MANIFEST
import com.zp4rker.discore.command.Command
import com.zp4rker.discore.extenstions.embed
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
        val creator = API.getUserByTag("zp4rker#3333") ?: return
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
    }

    private fun displayTime(seconds: Long): String {
        var mutableSecs = seconds
        val days = TimeUnit.MILLISECONDS.toDays(mutableSecs).also { mutableSecs -= TimeUnit.DAYS.toMillis(it) }
        val hours = TimeUnit.MILLISECONDS.toHours(mutableSecs).also { mutableSecs -= TimeUnit.HOURS.toMillis(it) }
        val minutes = TimeUnit.MILLISECONDS.toMinutes(mutableSecs).also { mutableSecs -= TimeUnit.MINUTES.toMillis(it) }
        return StringBuilder().apply {
            if (days > 0) append("${days}d")
            if (hours > 0) append("${hours}d")
            if (minutes > 0) append("${minutes}m")
            if (mutableSecs > 0) append("${mutableSecs}s")
        }.toString()
    }
}