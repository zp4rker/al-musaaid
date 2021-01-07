package com.zp4rker.almusaaid.command

import com.zp4rker.almusaaid.Trello
import com.zp4rker.discore.command.Command
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.json.JSONObject

/**
 * @author zp4rker
 */
object TestCommand : Command(aliases = arrayOf("test")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val result = Trello.getBoards(Trello.getSelfMember().getString("id")).map { it as JSONObject }.filter {
            !it.getBoolean("closed") && Trello.getMembers(it.getString("id")).length() == 1
        }
        channel.sendMessage("${result.size}").queue()
        channel.sendMessage(result.joinToString { it.getString("name") }).queue()

        val ideasBoard = result.find { it.getString("name") == "Ideas" } ?: return
        val lists = Trello.getLists(ideasBoard.getString("id"), "none").map { it as JSONObject }

        channel.sendMessage("${lists.size}").queue()
        channel.sendMessage(lists.joinToString { it.getString("name") }).queue()

        val rawIdeasList = lists.find { it.getString("name") == "Raw Ideas" } ?: return

        channel.sendMessage(rawIdeasList.getString("id")).queue()

        Trello.createCard(rawIdeasList.getString("id"), "Test card", "Test description")
    }
}