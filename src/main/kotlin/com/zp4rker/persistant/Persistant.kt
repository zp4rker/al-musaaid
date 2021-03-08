package com.zp4rker.persistant

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.zp4rker.discore.API
import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.bot
import com.zp4rker.discore.extenstions.event.expect
import com.zp4rker.discore.util.loadYamlOrDefault
import com.zp4rker.log4kt.Log4KtLoggerFactory
import com.zp4rker.persistant.audio.AudioHandler
import com.zp4rker.persistant.audio.TrackHandler
import com.zp4rker.persistant.listener.Listeners
import com.zp4rker.persistant.listener.trello.TrelloListeners
import com.zp4rker.persistant.trello.DataServer
import com.zp4rker.persistant.trello.TrelloApi
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

fun main(args: Array<String>) {
    if (args.any { it.equals("-debug", true) }) Log4KtLoggerFactory.debugEnabled = true

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
        name = "persistant"

        token = config.botSettings.token
        prefix = config.botSettings.prefix

        activity = Activity.listening("my master")

        jdaBuilder.enableIntents(GatewayIntent.values().asList())
        jdaBuilder.enableCache(CacheFlag.values().asList())

        quit = {
            dataServer.kill()
        }
    }

    API.expect<ReadyEvent> {
        dataServer.start()

        TrelloListeners.register()
        Listeners.register()

        API.getUserByTag(config.owner)!!.openPrivateChannel().complete()

        LOGGER.info("Ready to serve!")
    }
}