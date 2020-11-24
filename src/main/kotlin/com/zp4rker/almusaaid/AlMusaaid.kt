package com.zp4rker.almusaaid

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.zp4rker.almusaaid.audio.AudioHandler
import com.zp4rker.almusaaid.audio.TrackHandler
import com.zp4rker.almusaaid.command.PurgeCommand
import com.zp4rker.almusaaid.command.audio.PlayCommand
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

lateinit var PLAYER: AudioPlayer
lateinit var PLAYERMANAGER: AudioPlayerManager
lateinit var TRACKS: TrackHandler
lateinit var AUDIOHANDLER: AudioHandler

fun main(args: Array<String>) {
    val trelloKey = args[1]
    val trelloToken = args[2]
    val channelId = args[3].toLong()

    val dataServer = DataServer(trelloKey, trelloToken, channelId)

    PLAYERMANAGER = DefaultAudioPlayerManager().also { AudioSourceManagers.registerRemoteSources(it) }
    PLAYER = PLAYERMANAGER.createPlayer()
    TRACKS = TrackHandler()
    AUDIOHANDLER = AudioHandler(PLAYER)

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

        commands = arrayOf(PurgeCommand, PlayCommand)
    }

    API.on<ReadyEvent> {
        BOT.logger.separator()
        BOT.logger.info("Ready to serve!")
        BOT.logger.separator()
    }

    dataServer.start()
}