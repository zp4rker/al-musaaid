package com.zp4rker.almusaaid

import com.zp4rker.disbot.Bot
import net.dv8tion.jda.api.entities.Activity

/**
 * @author zp4rker
 */

fun main(args: Array<String>) {
    val bot = Bot.create {
        name = "Al-Musā'id"
        token = args[0]
        prefix = "/"

        activity = Activity.listening("your commands...")
    }
}