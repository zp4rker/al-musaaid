package com.zp4rker.almusaaid

import com.zp4rker.disbot.Bot
import com.zp4rker.disbot.command.Command
import com.zp4rker.disbot.extenstions.on
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.ReadyEvent

/**
 * @author zp4rker
 */

fun main(args: Array<String>) {
    val bot = Bot.create {
        name = "Al-Musā'id"
        token = args[0]
        prefix = "/"

        activity = Activity.listening("your commands...")

        commands = listOf(object : Command(aliases = arrayOf("test"), ) {
            override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
                channel.sendMessage("You're the best!").queue()
            }
        })
    }

    bot.on<ReadyEvent> {
        println()
        bot.logger.info("Ready to serve!")
    }
}