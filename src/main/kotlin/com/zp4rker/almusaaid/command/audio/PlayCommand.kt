package com.zp4rker.almusaaid.command.audio

import com.zp4rker.almusaaid.AUDIOHANDLER
import com.zp4rker.almusaaid.PLAYER
import com.zp4rker.almusaaid.PLAYERMANAGER
import com.zp4rker.almusaaid.audio.TrackLoader
import com.zp4rker.disbot.command.Command
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object PlayCommand : Command(aliases = arrayOf("play", "p"), minArgs = 1) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val audioManager = channel.guild.audioManager

        if (audioManager.sendingHandler != AUDIOHANDLER) audioManager.sendingHandler = AUDIOHANDLER

        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(channel.guild.voiceChannels.first())
        }

        PLAYERMANAGER.loadItemOrdered(PLAYER, args[0], TrackLoader(channel))
    }

}