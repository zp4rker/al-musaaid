package com.zp4rker.almusaaid.command.audio

import com.zp4rker.almusaaid.TRACKS
import com.zp4rker.disbot.command.Command
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object QueueCommand : Command(aliases = arrayOf("queue", "q")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val trackList = TRACKS.getQueue().mapIndexed { i, t -> "**${i + 1}.** `${t.info.title}`" }.joinToString("\n")
        channel.sendMessage("Current queue:\n$trackList").queue()
    }

}