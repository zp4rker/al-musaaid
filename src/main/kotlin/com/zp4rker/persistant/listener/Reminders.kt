package com.zp4rker.persistant.listener

import com.zp4rker.discore.API
import com.zp4rker.discore.extenstions.event.on
import com.zp4rker.discore.util.unicodify
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

/**
 * @author zp4rker
 */
object Reminders {

    private val currentTime: OffsetDateTime get() = OffsetDateTime.now(ZoneId.of("Australia/Sydney"))

    private val inRegex = Regex("remind me to (.*) in (.*)")
    private val atRegex = Regex("remind me to (.*) at (.*)")
    private val durationRegex = Regex("(\\d+[ ]?[^\\d^\\s^,]+)")
    private val timeRegex = Regex("(\\d{1,2})[:, ]?(\\d{1,2})")

    fun register() {
        API.on<MessageReceivedEvent> { e ->
            if (e.author.asTag != "zp4rker#3333") return@on
            if (inRegex.matches(e.message.contentRaw)) remindIn(e.message)
            else if (atRegex.matches(e.message.contentRaw)) remindAt(e.message)
        }
    }

    private fun remind(message: Message, task: String, millis: Long) {
        Timer().schedule(millis) {
            message.channel.sendMessage("${if (message.channelType != ChannelType.PRIVATE) "${message.author.asMention} " else ""}$task").queue()
        }
    }

    private fun remindIn(m: Message) {
        val matches = inRegex.matchEntire(m.contentRaw)?.groupValues?.filter { it != m.contentRaw } ?: return
        val task = matches[0]
        val timeRaw = matches[1]

        val timeComponents = durationRegex.findAll(timeRaw).toList().also { if (it.isEmpty()) return }.map { it.value }
        val millis = componentsToMillis(timeComponents)

        remind(m, task, millis)
    }

    private fun remindAt(m: Message) {
        val matches = atRegex.matchEntire(m.contentRaw)?.groupValues?.filter { it != m.contentRaw } ?: return
        val task = matches[0]
        val timeRaw = matches[1]

        val timeComponents = timeRegex.matchEntire(timeRaw)?.groupValues?.filter { it != m.contentRaw } ?: return

        val hour = timeComponents[0].toInt()
        val minute = timeComponents[1].toInt()

        var time = OffsetDateTime.from(currentTime).withHour(hour).withMinute(minute)
        if (time.isBefore(currentTime)) time = time.plusDays(1)

        remind(m, task, currentTime.until(time, ChronoUnit.MILLIS))
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