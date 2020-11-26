package com.zp4rker.almusaaid.command

import com.zp4rker.disbot.command.Command
import de.swirtz.ktsrunner.objectloader.KtsObjectLoader
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object EmbedCommand : Command(aliases = arrayOf("embed"), minArgs = 1) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val input = message.contentStripped.run { substring(indexOf(" ") + 1) }

        val script = """
            import com.zp4rker.disbot.extenstions.embed
            
            embed { $input }
        """.trimIndent()

        val embed = KtsObjectLoader().load<MessageEmbed>(script)

        channel.sendMessage(embed).queue()
    }

}