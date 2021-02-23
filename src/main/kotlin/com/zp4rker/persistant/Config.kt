package com.zp4rker.persistant

import com.zp4rker.discore.storage.BotConfig
import kotlinx.serialization.Serializable

/**
 * @author zp4rker
 */
@Serializable
data class Config(
    val owner: String = "zp4rker#3333",
    val botSettings: BotConfig = BotConfig(),
    val trelloConf: TrelloConf = TrelloConf()
) {

    @Serializable
    data class TrelloConf(
        val key: String = "insert key",
        val token: String = "insert token",
        val channel: String = "19851581954509",
        val ideaListId: String = "insert idea list id"
    )

    companion object {
        val default = Config()
    }

}
