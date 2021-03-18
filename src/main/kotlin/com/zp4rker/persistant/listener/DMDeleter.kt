package com.zp4rker.persistant.listener

import com.zp4rker.discore.API
import com.zp4rker.discore.event.on
import com.zp4rker.discore.util.unicodify
import com.zp4rker.persistant.config
import net.dv8tion.jda.api.entities.SelfUser
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent

/**
 * @author zp4rker
 */
object DMDeleter {

    private val emote = ":wastebasket:".unicodify()

    fun register() {
        API.on<PrivateMessageReactionAddEvent> { e ->
            if (e.user?.asTag != config.owner) return@on
            if (!e.reactionEmote.name.contains(emote)) return@on

            val msg = e.channel.retrieveMessageById(e.messageId).complete()
            if (msg.author !is SelfUser) return@on

            msg.delete().queue()
        }
    }

}