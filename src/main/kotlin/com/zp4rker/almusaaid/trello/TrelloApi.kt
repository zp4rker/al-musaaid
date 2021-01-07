package com.zp4rker.almusaaid.trello

import com.zp4rker.almusaaid.http.request
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author zp4rker
 */
class TrelloApi(private val trelloKey: String, private val trelloToken: String) {

    private val baseUrl = "https://api.trello.com/1"

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

    fun getLists(boardId: String) = JSONArray(
        request(
            "GET", "$baseUrl/boards/$boardId/lists", mapOf(
                "key" to trelloKey,
                "token" to trelloToken,
                "cards" to "open"
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

}