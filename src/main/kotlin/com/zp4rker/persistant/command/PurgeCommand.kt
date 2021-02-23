package com.zp4rker.persistant.command

import com.zp4rker.discore.command.Command
import com.zp4rker.discore.extenstions.author
import com.zp4rker.discore.extenstions.embed
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
object PurgeCommand :
    Command(aliases = arrayOf("purge", "clear"), args = arrayOf("(?:all)|\\d+"), permission = Permission.MESSAGE_MANAGE) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val amount = if (args[0] == "all") -1 else args[0].toInt()
        purge(channel, amount)
    }

    private fun purge(channel: TextChannel, amount: Int) {
        var total = 0
        var counter = amount + 1
        val allMessages = mutableListOf<Message>()
        val history = channel.history

        if (amount == -1) {
            var messages = history.retrievePast(100).complete().also(allMessages::addAll)
            while (messages.size == 100) {
                messages = history.retrievePast(100).complete().also(allMessages::addAll)
            }
            channel.purgeMessages(allMessages).last().get().also { total += allMessages.size }
        } else {
            while (counter > 0) {
                if (counter >= 100) {
                    history.retrievePast(100).complete().also(allMessages::addAll).also {
                        if (it.size < 100) counter = 0
                        else counter -= it.size
                    }
                } else {
                    history.retrievePast(counter).complete().also(allMessages::addAll).also { counter = 0 }
                }
            }
            channel.purgeMessages(allMessages).last().get().also { total += allMessages.size - 1 }
        }

        channel.sendMessage(embed(author = author { name = "Purged $total message${if (total == 1) "" else "s"}." })).queue {
            it.delete().queueAfter(5, TimeUnit.SECONDS)
        }
    }

}