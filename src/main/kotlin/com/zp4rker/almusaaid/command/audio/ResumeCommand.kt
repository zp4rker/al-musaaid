package com.zp4rker.almusaaid.command.audio

import com.zp4rker.almusaaid.TRACKS
import com.zp4rker.disbot.command.Command
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object ResumeCommand : Command(aliases = arrayOf("resume")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        TRACKS.paused = false
        channel.sendMessage("Player resumed.").queue()
    }

}