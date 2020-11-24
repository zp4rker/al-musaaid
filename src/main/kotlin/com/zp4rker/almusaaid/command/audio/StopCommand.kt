package com.zp4rker.almusaaid.command.audio

import com.zp4rker.almusaaid.PLAYER
import com.zp4rker.almusaaid.TRACKS
import com.zp4rker.disbot.command.Command
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object StopCommand : Command(aliases = arrayOf("stop")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        PLAYER.stopTrack()
        TRACKS.clearQueue()
        channel.guild.audioManager.closeAudioConnection()
        channel.sendMessage("Stopped player.").queue()
    }

}