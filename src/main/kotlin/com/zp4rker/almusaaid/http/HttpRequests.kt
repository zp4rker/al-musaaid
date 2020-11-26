package com.zp4rker.almusaaid.http

import java.net.HttpURLConnection
import java.net.URL

/**
 * @author zp4rker
 */

fun request(
    method: String,
    baseUrl: String,
    parameters: Map<String, Any> = mapOf(),
    headers: Map<String, String> = mapOf(),
    content: String? = null
): String {
    val url = URL(
        "$baseUrl${
            if (parameters.isNotEmpty()) parameters.map { "${it.key}=${it.value}" }.joinToString("&", "?") else ""
        }"
    )

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = method.toUpperCase()

        headers.forEach { addRequestProperty(it.key, it.value) }

        content?.let {
            doOutput = true

            outputStream.use { os ->
                with(os.writer()) {
                    write(content)
                    close()
                }
            }
        }

        inputStream.use {
            return it.reader().run { readText().also { close() } }
        }
    }
}