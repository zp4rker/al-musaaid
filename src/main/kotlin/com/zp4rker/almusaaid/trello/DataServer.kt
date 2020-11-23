package com.zp4rker.almusaaid.trello

import com.zp4rker.disbot.BOT
import com.zp4rker.disbot.extenstions.getComplex
import org.json.JSONObject
import java.lang.StringBuilder
import java.net.ServerSocket
import java.net.Socket

/**
 * @author zp4rker
 */
class DataServer(private val trelloKey: String, private val trelloToken: String): Thread() {

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
        println("Reveived action: $action")
        val sb = StringBuilder().apply { append("model = ") }.also { constructKeyTree(it, json.getJSONObject("model")) }
        println("Model key tree: $sb")
        println("model:descData = ${json.getComplex("model:descData")}")
    }

    private fun constructKeyTree(sb: StringBuilder, json: JSONObject) {
        sb.append("{ ")
        for (key in json.keySet()) {
            if (sb.toString().trim().endsWith("}")) sb.append(", ")
            sb.append(key)
            val obj = json[key]
            if (obj is JSONObject) {
                sb.append(" = ")
                constructKeyTree(sb, obj)
            } else if (key != json.keySet().last()) {
                sb.append(", ")
            }
        }
        sb.append(" }")
    }

    fun kill() = with(Socket("localhost", 49718)) {
        running = false
        val out = getOutputStream().writer()
        out.write("kill")
        out.close()
        close()
    }

}