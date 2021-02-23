package com.zp4rker.persistant.listener.trello

import com.zp4rker.persistant.IdeaListId
import com.zp4rker.persistant.Trello
import com.zp4rker.discore.API
import com.zp4rker.discore.extenstions.event.Predicate
import com.zp4rker.discore.extenstions.event.expect
import com.zp4rker.discore.extenstions.event.on
import com.zp4rker.discore.util.unicodify
import com.zp4rker.persistant.config
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
object IdeaListener {

    private val emote = ":octagonal_sign:".unicodify()

    fun register() {
        API.on<GuildMessageReceivedEvent> { e ->
            if (e.author.asTag != config.owner) return@on
            if (e.channel.name != "idea-hub") return@on
            e.channel.parent?.let { if (it.name != "private") return@on } ?: return@on

            if (e.message.referencedMessage != null) {
                handleReply(e)
                return@on
            }

            e.message.addReaction(emote).queue()
            e.channel.expect<GuildMessageReactionAddEvent>({ it.reactionEmote.name == emote && it.user.asTag == config.owner }) {
                val m = e.channel.sendMessage("Please reply to this message with the name of the idea ${":grin:".unicodify()}").complete()
                val content = collectContent(it.retrieveMessage().complete())
                val pred: Predicate<GuildMessageReceivedEvent> = { e2 ->
                    e2.message.referencedMessage == m && e2.author.asTag == config.owner
                }
                e.channel.expect(pred) { e2 ->
                    Trello.createCard(IdeaListId, e2.message.contentRaw, content.joinToString("\n") { m -> m.contentRaw })
                    e.channel.sendMessage("Recorded your idea! :grin:").queue { msg ->
                        e.channel.deleteMessages(listOf(m, e2.message, msg) + content).queueAfter(10, TimeUnit.SECONDS)
                    }
                }
            }
        }
    }

    private fun handleReply(e: GuildMessageReceivedEvent) {
        val reply = e.message
        val original = e.channel.retrieveMessageById(reply.referencedMessage!!.id).complete()

        if (original.reactions.none { it.reactionEmote.name == emote }) return

        reply.addReaction(emote).queue()
    }

    private fun collectContent(lastMessage: Message): List<Message> {
        val list = mutableListOf(lastMessage)
        var msg = lastMessage.channel.retrieveMessageById(lastMessage.id).complete()
        while (msg.referencedMessage != null) {
            list.add(msg.referencedMessage!!)
            msg = lastMessage.channel.retrieveMessageById(msg.referencedMessage!!.id).complete()
        }
        return list.reversed()
    }

}
