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
import org.json.JSONObject

/**
 * @author zp4rker
 */

fun main(args: Array<String>) {
    val trelloKey = args[1]
    val trelloToken = args[2]
    val trelloWebhook = args[3]

    val bot = Bot.create {
        name = "Al-Musā'id"
        version = Bot::class.java.`package`.implementationVersion

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
        it.message.embeds.isNotEmpty() && it.message.embeds[0].description == "empty data"
                && arrayOf("Set due date", "Moved card").contains(it.message.embeds[0].title)
    }

    API.on(predicate) {
        val cardId = it.message.embeds[0].footer!!.text
        it.message.delete().queue()

        val cardData = JSONObject(request("GET", "https://api.trello.com/1/cards/$cardId", mapOf(
            "key" to trelloKey,
            "token" to trelloToken,
            "fields" to "due,name,idList"
        )))

        var embedString = ""

        if (it.message.embeds[0].title == "Set due date") {
            embedString = """{ "embeds": [{
                |"author": { "name": "${cardData.getString("name")}" },
                |"title": "Set due date",
                |"color": 877490,
                |"timestamp": "${cardData.getString("due")}",
                |"footer": { "text": "Due by" }
            |}] }""".trimMargin()
        } else if (it.message.embeds[0].title == "Moved card") {
            val listName = JSONObject(request("GET", "https://api.trello.com/1/lists/${cardData.getString("idList")}", mapOf(
                "key" to trelloKey,
                "token" to trelloToken,
                "fields" to "name"
            ))).getString("name")

            if (listName != "In Progress") return@on

            embedString = """{ "embeds": [{
                |"author": { "name": "${cardData.getString("name")}" },
                |"title": "Moved task to $listName",
                |"color": 877490,
                |"timestamp": "${it.message.embeds[0].timestamp}"
            }] }""".trimMargin()
        }

        request("POST", trelloWebhook, headers = mapOf("Content-Type" to "application/json"), content = embedString)
    }
}