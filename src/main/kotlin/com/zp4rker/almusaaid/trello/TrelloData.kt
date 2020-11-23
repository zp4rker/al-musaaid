package com.zp4rker.almusaaid.trello

import com.zp4rker.almusaaid.http.request
import org.json.JSONObject

/**
 * @author zp4rker
 */
object TrelloData {

    fun getCard(cardId: String, trelloKey: String, trelloToken: String) = JSONObject(request("GET", "https://api.trello.com/1/cards/$cardId", mapOf(
        "key" to trelloKey,
        "token" to trelloToken,
        "fields" to "due,name,idList"
    )))

}