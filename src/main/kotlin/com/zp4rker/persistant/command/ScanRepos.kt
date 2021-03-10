package com.zp4rker.persistant.command

import com.zp4rker.discore.command.Command
import com.zp4rker.persistant.github.RepoTracker
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object ScanRepos : Command(permission = Permission.ADMINISTRATOR) {
    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        RepoTracker.scan()
    }
}