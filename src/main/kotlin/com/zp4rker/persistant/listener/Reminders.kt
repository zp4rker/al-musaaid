package com.zp4rker.persistant.listener

import com.zp4rker.discore.API
import com.zp4rker.discore.extensions.expect
import com.zp4rker.discore.event.on
import com.zp4rker.discore.util.unicodify
import com.zp4rker.persistant.config
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
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

    private val currentTime: OffsetDateTime get() = OffsetDateTime.now(ZoneId.of(config.timezone))

    private val inRegex = Regex("(?:remind|tell) me(?: to)? (.*) in (.*)", RegexOption.IGNORE_CASE)
    private val atRegex = Regex("(?:remind|tell) me(?: to)? (.*) at (.*)", RegexOption.IGNORE_CASE)
    private val againRegex = Regex("(?:remind|tell) me again in (.*)", RegexOption.IGNORE_CASE)
    private val whenRegex = Regex("(?:remind|tell) me when it[']?s (.*)", RegexOption.IGNORE_CASE)
    private val durationRegex = Regex("(\\d+[ ]?[^\\d^\\s^,]+)", RegexOption.IGNORE_CASE)
    private val timeRegex = Regex("(\\d{1,2})[:, ]?(\\d{1,2})", RegexOption.IGNORE_CASE)

    fun register() {
        API.on<MessageReceivedEvent> { e ->
            if (e.author.asTag != config.owner) return@on

            if (!againRegex.matches(e.message.contentRaw)) {
                if (inRegex.matches(e.message.contentRaw)) remindIn(e.message)
                else if (atRegex.matches(e.message.contentRaw)) remindAt(e.message)
                else if (whenRegex.matches(e.message.contentRaw)) remindWhen(e.message)
            }
        }
    }

    private fun remind(message: Message, task: String, millis: Long) {
        message.addReaction(":thumbsup:".unicodify()).queue()
        Timer().schedule(millis) {
            message.channel.sendMessage("${if (message.channelType != ChannelType.PRIVATE) "${message.author.asMention} " else ""}$task").queue {
                message.channel.expect<MessageReceivedEvent>({ e -> e.message.referencedMessage == it && againRegex.matches(e.message.contentRaw) }, timeout = 5, timeoutUnit = TimeUnit.MINUTES) { e ->
                    val matches = againRegex.matchEntire(e.message.contentRaw)?.groupValues?.filter { s -> s != e.message.contentRaw } ?: return@expect
                    val timeRaw = matches[0]

                    val timeComponents = durationRegex.findAll(timeRaw).toList().also { list -> if (list.isEmpty()) return@expect }.map { c -> c.value }
                    val ms = componentsToMillis(timeComponents)

                    remind(e.message, task, ms)
                }
            }
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

        val hour = timeComponents[1].toInt()
        val minute = timeComponents[2].toInt()

        var time = OffsetDateTime.from(currentTime).withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        if (time.isBefore(currentTime)) time = time.plusDays(1)

        remind(m, task, currentTime.until(time, ChronoUnit.MILLIS))
    }

    private fun remindWhen(m: Message) {
        val matches = whenRegex.matchEntire(m.contentRaw)?.groupValues?.filter { it != m.contentRaw } ?: return
        val timeRaw = matches[0]

        if (timeRegex.matches(timeRaw)) {
            val timeComponents = timeRegex.matchEntire(timeRaw)?.groupValues?.filter { it != m.contentRaw } ?: return

            val hour = timeComponents[1].toInt()
            val minute = timeComponents[2].toInt()

            var time = OffsetDateTime.from(currentTime).withHour(hour).withMinute(minute).withSecond(0).withNano(0)
            if (time.isBefore(currentTime)) time = time.plusDays(1)

            remind(m, "It's $timeRaw now!", currentTime.until(time, ChronoUnit.MILLIS))
        } else {
            val timeComponents = durationRegex.findAll(timeRaw).toList().also { if (it.isEmpty()) return }.map { it.value }
            val millis = componentsToMillis(timeComponents)

            remind(m, "It's been $timeRaw!", millis)
        }
    }

    private fun componentsToMillis(components: List<String>): Long {
        var millis = 0L
        for (rawComp in components) {
            val comp = rawComp.replace(" ", "")
            val amount = Regex("(\\d+)\\w+").matchEntire(comp)!!.groupValues[1].toLong()
            when {
                comp.matches(Regex("\\d+(?:h(?:our[s]?)?)", RegexOption.IGNORE_CASE)) -> {
                    millis += TimeUnit.HOURS.toMillis(amount)
                }
                comp.matches(Regex("\\d+(?:m(?:inute[s]?)?)", RegexOption.IGNORE_CASE)) -> {
                    millis += TimeUnit.MINUTES.toMillis(amount)
                }
                comp.matches(Regex("\\d+(?:s(?:econd[s]?)?)", RegexOption.IGNORE_CASE)) -> {
                    millis += TimeUnit.SECONDS.toMillis(amount)
                }
            }
        }
        return millis
    }

}