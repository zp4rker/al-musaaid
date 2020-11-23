package com.zp4rker.almusaaid.trello

import org.json.JSONObject
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

            if (data == "kill") continue

            handle(data)
        }

        serverSocket.close()
    }

    private fun handle(data: String) {
        val json = JSONObject(data)

        println("Reveived data with keys: ${json.keySet().joinToString(", ")}")
    }

    fun kill() = with(Socket("localhost", 49718)) {
        val out = getOutputStream().writer()
        out.write("kill")
        out.close()
        close()
    }

}