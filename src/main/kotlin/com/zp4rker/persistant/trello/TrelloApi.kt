package com.zp4rker.persistant.trello

import com.zp4rker.persistant.http.request
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author zp4rker
 */
class TrelloApi(private val trelloKey: String, private val trelloToken: String) {
    private val baseUrl = "https://api.trello.com/1"

    /* GET functions */

    fun getCard(cardId: String) = JSONObject(
        request(
            "GET", "$baseUrl/cards/$cardId", mapOf(
                "key" to trelloKey,
                "token" to trelloToken,
                "fields" to "all"
            )
        )
    )

    fun getList(listId: String) = JSONObject(
        request(
            "GET", "$baseUrl/lists/$listId", mapOf(
                "key" to trelloKey,
                "token" to trelloToken,
                "fields" to "all"
            )
        )
    )

    fun getSelfMember() = JSONObject(
        request(
            "GET", "$baseUrl/tokens/$trelloToken/member", mapOf(
                "key" to trelloKey,
                "token" to trelloToken,
                "fields" to "all"
            )
        )
    )

    fun getLists(boardId: String, cardsField: String = "open") = JSONArray(
        request(
            "GET", "$baseUrl/boards/$boardId/lists", mapOf(
                "key" to trelloKey,
                "token" to trelloToken,
                "cards" to cardsField
            )
        )
    )

    fun getBoards(memberId: String) = JSONArray(
        request(
            "GET", "$baseUrl/members/$memberId/boards", mapOf(
                "key" to trelloKey,
                "token" to trelloToken,
                "fields" to "all"
            )
        )
    )

    fun getMembers(boardId: String) = JSONArray(
        request(
            "GET", "$baseUrl/boards/$boardId/members", mapOf(
                "key" to trelloKey,
                "token" to trelloToken
            )
        )
    )

    /* POST functions */

    fun createCard(listId: String, name: String, desc: String? = null, pos: String = "bottom") = request(
        "POST", "$baseUrl/cards", mapOf(
            "key" to trelloKey,
            "token" to trelloToken,

            "idList" to listId,

            "name" to name,
            "desc" to desc,
            "pos" to if (pos.toCharArray().all(Char::isDigit)) pos.toInt() else pos
        )
    )

}