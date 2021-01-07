package com.zp4rker.almusaaid.command

import com.zp4rker.discore.command.Command
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object TestCommand : Command(aliases = arrayOf("test"), permission = Permission.ADMINISTRATOR) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val regex = Regex("(\\d+)\\w+")
        val content = args.joinToString(" ")

        channel.sendMessage("${regex.matchEntire(content) != null}").queue()
        regex.matchEntire(content)?.let {
            it.groupValues.forEach { g ->
                channel.sendMessage(g).queue()
            }
        }
    }
}