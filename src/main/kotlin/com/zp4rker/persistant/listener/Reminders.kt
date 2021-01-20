package com.zp4rker.persistant.listener

import com.zp4rker.discore.API
import com.zp4rker.discore.extenstions.event.on
import com.zp4rker.discore.util.unicodify
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

/**
 * @author zp4rker
 */
object Reminders {

    private val msgRegex = Regex("remind me to (.*) in (.*)")
    private val timeRegex = Regex("(\\d+[ ]?[^\\d^\\s^,]+)")

    fun register() {
        API.on<MessageReceivedEvent> { e ->
            if (e.author.asTag != "zp4rker#3333") return@on
            val matches = msgRegex.matchEntire(e.message.contentRaw)?.groupValues?.filter { it != e.message.contentRaw } ?: return@on

            e.message.addReaction(":thumbsup:".unicodify()).queue()

            val task = matches[0]
            val timeRaw = matches[1]

            val timeComponents = timeRegex.findAll(timeRaw).toList().also { if (it.isEmpty()) return@on }.map { it.value }
            val millis = componentsToMillis(timeComponents)

            Timer().schedule(millis) {
                e.channel.sendMessage("${if (e.channelType != ChannelType.PRIVATE) "${e.author.asMention} " else ""}$task").queue()
            }
        }
    }

    private fun componentsToMillis(components: List<String>): Long {
        var millis = 0L
        for (rawComp in components) {
            val comp = rawComp.replace(" ", "")
            val amount = Regex("(\\d+)\\w+").matchEntire(comp)!!.groupValues[1].toLong()
            when {
                comp.matches(Regex("\\d+(?:h(?:our[s]?)?)")) -> {
                    millis += TimeUnit.HOURS.toMillis(amount)
                }
                comp.matches(Regex("\\d+(?:m(?:inute[s]?)?)")) -> {
                    millis += TimeUnit.MINUTES.toMillis(amount)
                }
                comp.matches(Regex("\\d+(?:s(?:econd[s]?)?)")) -> {
                    millis += TimeUnit.SECONDS.toMillis(amount)
                }
            }
        }
        return millis
    }

}