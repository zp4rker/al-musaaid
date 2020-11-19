package com.zp4rker.almusaaid

import com.zp4rker.almusaaid.http.request
import com.zp4rker.disbot.API
import com.zp4rker.disbot.Bot
import com.zp4rker.disbot.command.Command
import com.zp4rker.disbot.extenstions.event.on
import com.zp4rker.disbot.extenstions.separator
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.requests.GatewayIntent

/**
 * @author zp4rker
 */

fun main(args: Array<String>) {
    val trelloKey = args[1]
    val trelloToken = args[2]

    val bot = Bot.create {
        name = "Al-Musā'id"
        token = args[0]
        prefix = "/"

        activity = Activity.listening("your commands...")

        intents = GatewayIntent.ALL_INTENTS

        commands = listOf(object : Command(aliases = arrayOf("test")) {
            override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
                channel.sendMessage("You're the best!").queue()
            }
        })
    }

    API.on<ReadyEvent> {
        bot.logger.separator()
        bot.logger.info("Ready to serve!")
    }

    val predicate: (GuildMessageReceivedEvent) -> Boolean = {
        it.author.isBot && it.message.embeds.isNotEmpty() && it.message.embeds[0].title == "Task Completed"
    }

    API.on(predicate) {
        val embed = it.message.embeds[0]
        val cardId = embed.footer!!.text

        request("PUT", "https://api.trello.com/1/cards/$cardId", mapOf(
            "key" to trelloKey,
            "token" to trelloToken,
            "dueComplete" to false
        ))
    }
}