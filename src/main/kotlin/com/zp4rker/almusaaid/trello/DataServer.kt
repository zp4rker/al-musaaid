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
class DataServer(private val trelloKey: String, private val trelloToken: String, private val channelId: Long): Thread() {

    private val serverSocket = ServerSocket(49718)
    var running = true

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

        val embed = when (action.substring(7)) {
            "marked_the_due_date_complete" -> embed {
                title { text = "Completed task" }
                author { name = json.getComplex("action:data:card:name").toString() }
                colour = 0x0039A96E
                timestamp = OffsetDateTime.parse(json.getComplex("action:date").toString())
            }
            else -> embed {
                // ill get to this
            }
        }

        API.getTextChannelById(channelId)!!.sendMessage(embed).queue()
    }

    fun kill() = with(Socket("localhost", 49718)) {
        running = false
        val out = getOutputStream().writer()
        out.write("kill")
        out.close()
        close()
    }

}