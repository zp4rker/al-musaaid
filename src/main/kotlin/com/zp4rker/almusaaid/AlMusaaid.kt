package com.zp4rker.almusaaid

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.zp4rker.almusaaid.audio.AudioHandler
import com.zp4rker.almusaaid.audio.TrackHandler
import com.zp4rker.almusaaid.command.PurgeCommand
import com.zp4rker.almusaaid.command.TestCommand
import com.zp4rker.almusaaid.command.audio.*
import com.zp4rker.almusaaid.command.trello.TCardCommand
import com.zp4rker.almusaaid.trello.DataServer
import com.zp4rker.almusaaid.trello.TrelloApi
import com.zp4rker.discore.API
import com.zp4rker.discore.BOT
import com.zp4rker.discore.Bot
import com.zp4rker.discore.bot
import com.zp4rker.discore.extenstions.event.on
import com.zp4rker.discore.extenstions.separator
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.requests.GatewayIntent

/**
 * @author zp4rker
 */

lateinit var PLAYER: AudioPlayer
lateinit var PMANAGER: AudioPlayerManager
lateinit var TSCHEDULER: TrackHandler
lateinit var AHANDLER: AudioHandler

lateinit var Trello: TrelloApi

fun main(args: Array<String>) {
    val trelloKey = args[1]
    val trelloToken = args[2]
    val channelId = args[3].toLong()

    Trello = TrelloApi(trelloKey, trelloToken)

    val dataServer = DataServer(channelId)

    PMANAGER = DefaultAudioPlayerManager().also { AudioSourceManagers.registerRemoteSources(it) }
    PLAYER = PMANAGER.createPlayer()
    TSCHEDULER = TrackHandler()
    AHANDLER = AudioHandler()

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

        commands = listOf(
            // Trello commands
            TCardCommand,
            // Misc commands
            TestCommand,
            PurgeCommand,
            // Audio commands
            QueueCommand,
            PlayCommand,
            PauseCommand,
            ResumeCommand,
            StopCommand
        )
    }

    API.on<ReadyEvent> {
        BOT.logger.separator()
        BOT.logger.info("Ready to serve!")
        BOT.logger.separator()
    }

    dataServer.start()
}