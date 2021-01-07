package com.zp4rker.almusaaid.listener.trello

import com.zp4rker.almusaaid.IdeaListId
import com.zp4rker.almusaaid.Trello
import com.zp4rker.discore.API
import com.zp4rker.discore.extenstions.event.Predicate
import com.zp4rker.discore.extenstions.event.expect
import com.zp4rker.discore.extenstions.event.on
import com.zp4rker.discore.util.unicodify
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
            if (e.author.asTag != "zp4rker#3333") return@on
            if (e.channel.name != "idea-hub") return@on
            e.channel.parent?.let { if (it.name != "private") return@on } ?: return@on

            if (e.message.referencedMessage != null) return@on
            else handleReply(e)

            if (e.message.contentRaw.length < 1900) {
                val m = e.channel.sendMessage("Please reply to this message with the name of the idea ${":grin:".unicodify()}").complete()
                val pred: Predicate<GuildMessageReceivedEvent> = {
                    it.message.referencedMessage == m && it.author.asTag == "zp4rker#3333"
                }
                e.channel.expect(pred) {
                    Trello.createCard(IdeaListId, it.message.contentRaw, e.message.contentRaw)
                    e.channel.sendMessage("Recorded your idea! :grin:").queue { msg ->
                        e.channel.deleteMessages(listOf(e.message, m, it.message, msg)).queueAfter(10, TimeUnit.SECONDS)
                    }
                }
            } else {
                e.message.addReaction(emote).queue()
                e.channel.expect<GuildMessageReactionAddEvent>({ it.reactionEmote.name == emote && !it.user.isBot }) {
                    val m = e.channel.sendMessage("Please reply to this message with the name of the idea ${":grin:".unicodify()}").complete()
                    val content = collectContent(it.retrieveMessage().complete())
                    val pred: Predicate<GuildMessageReceivedEvent> = { e2 ->
                        e2.message.referencedMessage == m && e2.author.asTag == "zp4rker#3333"
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
    }

    private fun handleReply(e: GuildMessageReceivedEvent) {
        val reply = e.message
        val original = e.message.referencedMessage!!

        if (original.getReactionByUnicode(emote) == null) return

        reply.addReaction(emote).queue()
    }

    private fun collectContent(lastMessage: Message): List<Message> {
        val list = mutableListOf(lastMessage)
        var msg = lastMessage
        while (msg.referencedMessage != null) {
            list.add(msg.referencedMessage!!)
            msg = msg.referencedMessage!!
        }
        return list.reversed()
    }

}
