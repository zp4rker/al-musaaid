package com.zp4rker.almusaaid

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.zp4rker.almusaaid.audio.AudioHandler
import com.zp4rker.almusaaid.audio.TrackHandler
import com.zp4rker.almusaaid.command.InfoCommand
import com.zp4rker.almusaaid.command.PurgeCommand
import com.zp4rker.almusaaid.command.TestCommand
import com.zp4rker.almusaaid.command.audio.*
import com.zp4rker.almusaaid.listener.Listeners
import com.zp4rker.almusaaid.listener.trello.TrelloListeners
import com.zp4rker.almusaaid.trello.DataServer
import com.zp4rker.almusaaid.trello.TrelloApi
import com.zp4rker.discore.API
import com.zp4rker.discore.BOT
import com.zp4rker.discore.Bot
import com.zp4rker.discore.bot
import com.zp4rker.discore.extenstions.event.on
import com.zp4rker.discore.extenstions.separator
import com.zp4rker.discore.util.loadYamlOrDefault
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.io.File
import java.time.Instant

/**
 * @author zp4rker
 */
val config = loadYamlOrDefault<Config>(File("config.yml"))

lateinit var PLAYER: AudioPlayer
lateinit var PMANAGER: AudioPlayerManager
lateinit var TSCHEDULER: TrackHandler
lateinit var AHANDLER: AudioHandler

lateinit var Trello: TrelloApi

lateinit var IdeaListId: String

val startTime: Instant = Instant.now()

fun main() {
    val trelloKey = config.trelloConf.key
    val trelloToken = config.trelloConf.token
    val channelId = config.trelloConf.channel
    IdeaListId = config.trelloConf.ideaListId

    Trello = TrelloApi(trelloKey, trelloToken)

    val dataServer = DataServer(channelId)

    PMANAGER = DefaultAudioPlayerManager().also { AudioSourceManagers.registerRemoteSources(it) }
    PLAYER = PMANAGER.createPlayer()
    TSCHEDULER = TrackHandler()
    AHANDLER = AudioHandler()

    bot {
        name = config.botSettings.name
        version = Bot::class.java.`package`.implementationVersion

        token = config.botSettings.token
        prefix = "/"

        activity = Activity.listening("your commands...")

        intents = GatewayIntent.ALL_INTENTS

        quit = {
            dataServer.kill()
        }

        cache = CacheFlag.values().asList()

        commands = listOf(
            // Misc commands
            TestCommand,
            InfoCommand,
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
        dataServer.start()

        TrelloListeners.register()
        Listeners.register()

        API.getUserByTag("zp4rker#3333")!!.openPrivateChannel().complete()

        BOT.logger.separator()
        BOT.logger.info("Ready to serve!")
        BOT.logger.separator()
    }
}