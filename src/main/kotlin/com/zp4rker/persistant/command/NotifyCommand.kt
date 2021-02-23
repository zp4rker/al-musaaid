package com.zp4rker.persistant.command

import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.command.Command
import com.zp4rker.persistant.http.request
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

/**
 * @author zp4rker
 */
object NotifyCommand : Command(aliases = arrayOf("notify"), args = arrayOf(".*:.*:.*")) {
    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val base = "https://repo.maven.apache.org/maven2/"
        val module = args.first().split(":")
        val groupId = module[0].replace(".", "/")
        val artifactId = module[1]
        val version = module[2]
        val url = "$base$groupId/$artifactId/$version/$artifactId-$version.pom"

        Timer().schedule(0, TimeUnit.MINUTES.toMillis(5)) {
            LOGGER.debug("Querying ${args.first()}...")
            if (!request("GET", url).contains("404 Not Found")) {
                channel.sendMessage("${message.author.asMention} ${args.first()} is live!").queue {
                    cancel()
                }
            }
        }

        message.addReaction("👍").queue()
    }
}