package com.zp4rker.almusaaid.http

import java.net.HttpURLConnection
import java.net.URL

/**
 * @author zp4rker
 */

fun request(method: String, baseUrl: String, parameters: Map<String, Any>): String {
    val url = URL("$baseUrl${parameters.map { "${it.key}=${it.value}" }.joinToString("&", "?")}")

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = method.toUpperCase()

        return inputStream.reader().readText()
    }
}