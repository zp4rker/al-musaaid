package com.zp4rker.almusaaid

import com.zp4rker.almusaaid.command.PurgeCommand
import com.zp4rker.almusaaid.trello.DataServer
import com.zp4rker.disbot.API
import com.zp4rker.disbot.BOT
import com.zp4rker.disbot.Bot
import com.zp4rker.disbot.bot
import com.zp4rker.disbot.extenstions.event.on
import com.zp4rker.disbot.extenstions.separator
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.requests.GatewayIntent

/**
 * @author zp4rker
 */

fun main(args: Array<String>) {
    val trelloKey = args[1]
    val trelloToken = args[2]
    val channelId = args[3].toLong()

    val dataServer = DataServer(trelloKey, trelloToken, channelId)

    bot {
        name = "Al-Musā'id"
        version = Bot::class.java.`package`.implementationVersion

        token = args[0]
        prefix = "/"

        activity = Activity.listening("your commands...")

        intents = GatewayIntent.ALL_INTENTS

        quit = {
            dataServer.kill()
        }

        commands = arrayOf(PurgeCommand)
    }

    API.on<ReadyEvent> {
        BOT.logger.separator()
        BOT.logger.info("Ready to serve!")
        BOT.logger.separator()
    }

    dataServer.start()
}