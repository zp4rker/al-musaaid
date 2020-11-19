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
        !it.author.isBot && it.message.contentRaw.startsWith("send ")
    }

    API.on(predicate) {
        val url = it.message.contentRaw.split(" ")[1]
        val content = it.message.contentRaw.split(" ").drop(2).joinToString(" ")

        val response = request("POST", url, headers = mapOf("Content-Type" to "application/json"), content = content)

        if (response.isNotEmpty()) it.channel.sendMessage("```json\n$response```").queue()
    }
}