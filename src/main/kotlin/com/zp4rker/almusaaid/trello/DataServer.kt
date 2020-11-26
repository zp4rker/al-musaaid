package com.zp4rker.almusaaid.trello

import com.zp4rker.disbot.API
import com.zp4rker.disbot.BOT
import com.zp4rker.disbot.extenstions.embed
import com.zp4rker.disbot.extenstions.getComplex
import org.json.JSONObject
import java.net.ServerSocket
import java.net.Socket
import java.time.OffsetDateTime

/**
 * @author zp4rker
 */
class DataServer(private val trelloKey: String, private val trelloToken: String, private val channelId: Long) :
    Thread() {

    private val serverSocket = ServerSocket(49718)
    var running = true

    private val trelloData = TrelloData(trelloKey, trelloToken)

    override fun run() {
        while (running) {
            val socket = serverSocket.accept()
            val input = socket.getInputStream().reader()
            val data = input.readText()
            input.close()
            socket.close()

            if (data == "kill") {
                BOT.logger.info("Shutting down DataServer...")
                continue
            }

            handle(data)
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
                val cardData = trelloData.getCard(json.getComplex("action:data:card:id").toString())
                val listName = trelloData.getList(cardData.getString("idList")).getString("name")
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

                val cardData = trelloData.getCard(json.getComplex("action:data:card:id").toString())
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

    fun kill() = with(Socket("localhost", 49718)) {
        running = false
        val out = getOutputStream().writer()
        out.write("kill")
        out.close()
        close()
    }

}