package com.zp4rker.almusaaid.trello

import com.zp4rker.almusaaid.http.request
import org.json.JSONObject

/**
 * @author zp4rker
 */
class TrelloApi(private val trelloKey: String, private val trelloToken: String) {

    fun getCard(cardId: String) = JSONObject(
        request(
            "GET", "https://api.trello.com/1/cards/$cardId", mapOf(
                "key" to trelloKey,
                "token" to trelloToken,
                "fields" to "all"
            )
        )
    )

    fun getList(listId: String) = JSONObject(
        request(
            "GET", "https://api.trello.com/1/lists/$listId", mapOf(
                "key" to trelloKey,
                "token" to trelloToken,
                "fields" to "all"
            )
        )
    )

}