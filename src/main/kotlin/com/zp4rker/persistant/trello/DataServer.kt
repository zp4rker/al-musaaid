package com.zp4rker.persistant.trello

import com.zp4rker.persistant.Trello
import com.zp4rker.discore.API
import com.zp4rker.discore.extensions.embed
import com.zp4rker.discore.extensions.getComplex
import org.json.JSONObject
import java.net.ServerSocket
import java.time.OffsetDateTime

/**
 * @author zp4rker
 */
class DataServer(private val channelId: String) : Thread() {

    private val serverSocket = ServerSocket(49718)
    var running = true

    override fun run() {
        while (running) {
            kotlin.runCatching {
                serverSocket.accept().use {
                    val input = it.getInputStream().reader()
                    val data = input.readText()

                    handle(data)
                }
            }.onFailure {
                if (running) it.printStackTrace()
            }
        }

        serverSocket.close()
    }

    private fun handle(data: String) {
        val json = JSONObject(data)

        val action = json.getJSONObject("action").getJSONObject("display").getString("translationKey")
        val actionDate = OffsetDateTime.parse(json.getComplex("action:date").toString())
        val cardName = json.getComplex("action:data:card:name").toString()
        val defaultColour = "#0D63B2"

        val embed = when (action.substring(7)) {
            "create_card" -> embed {
                title { text = "Added task to ${json.getComplex("action:data:list:name")}" }
                author { name = cardName }
                color = defaultColour
                timestamp = actionDate
            }

            "move_card_from_list_to_list" -> {
                val cardData = Trello.getCard(json.getComplex("action:data:card:id").toString())
                val listName = Trello.getList(cardData.getString("idList")).getString("name")
                if (listName != "In Progress") embed()
                else embed {
                    title { text = "Moved task to $listName" }
                    author { name = cardName }
                    color = defaultColour
                    timestamp = actionDate
                }
            }

            "added_a_due_date" -> embed {
                title { text = "Set due date" }
                author { name = cardName }
                color = defaultColour

                val cardData = Trello.getCard(json.getComplex("action:data:card:id").toString())
                timestamp = OffsetDateTime.parse(cardData.getString("due"))
            }

            "marked_the_due_date_complete" -> embed {
                title { text = "Completed task" }
                author { name = cardName }
                color = "#39A96E"
                timestamp = actionDate
            }

            else -> embed()
        }

        if (embed.title != null && embed.title!!.isNotEmpty()) API.getTextChannelById(channelId)!!.sendMessage(embed)
            .queue()
    }

    fun kill() {
        running = false
        serverSocket.close()
    }

}