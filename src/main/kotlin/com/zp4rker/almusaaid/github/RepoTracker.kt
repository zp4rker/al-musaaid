package com.zp4rker.almusaaid.github

import com.zp4rker.almusaaid.discordHook
import com.zp4rker.almusaaid.githubAuth
import com.zp4rker.almusaaid.http.request
import com.zp4rker.discore.extenstions.embed
import com.zp4rker.discore.extenstions.toJson
import org.kohsuke.github.*
import java.net.URL
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.scheduleAtFixedRate

/**
 * @author zp4rker
 */
object RepoTracker {

    private val rawHook = discordHook.dropLast("/github".length)
    private lateinit var myself: GHMyself

    fun start() {
        val client = GitHubBuilder().withOAuthToken(githubAuth).build()
        myself = client.myself

        val name = myself.login
        val avatar = myself.avatarUrl

        var last = LocalDate.now(ZoneOffset.UTC)
        Timer().scheduleAtFixedRate(0, TimeUnit.MINUTES.toMillis(1)) {
            val all = myself.listRepositories(100, GHMyself.RepositoryListFilter.OWNER)

            println("checking...")
            val new = all.filter { it.createdAt.toInstant().atZone(ZoneOffset.UTC).toLocalDate() > last }.also { last = LocalDate.now(ZoneOffset.UTC) }
            println("found ${new.size} new repos!")
            for (repo in new) {
                val embed = embed {
                    color = "#202225"

                    author {
                        this.name = name
                        this.iconUrl = avatar
                    }

                    title {
                        this.text = "[$name/${repo.name}] Created new repository"
                        this.url = repo.httpTransportUrl
                    }
                }
                request(
                    method = "POST",
                    baseUrl = rawHook,
                    headers = mapOf("Content-Type" to "application/json"),
                    content = """{ "embeds": [${embed.toJson().toString(2)}] }"""
                )
            }

            val untracked = all.filter { it.hooks.none { h -> h.config["url"] == discordHook } }
            untracked.forEach(::track)
        }
    }

    private fun track(repo: GHRepository) {
        if (repo.isArchived) return

        repo.createHook(
            "web",
            mapOf("content_type" to "json", "url" to discordHook, "insecure_ssl" to "0"),
            listOf(GHEvent.PUSH),
            true
        )

        val name = myself.login
        val avatar = myself.avatarUrl
        val embed = embed {
            color = "#202225"

            author {
                this.name = name
                this.iconUrl = avatar
            }

            title {
                this.text = "[$name/${repo.name}] Started tracking repository"
                this.url = repo.httpTransportUrl
            }
        }
        request(
            method = "POST",
            baseUrl =  rawHook,
            headers = mapOf("Content-Type" to "application/json"),
            content = """{ "embeds": [${embed.toJson().toString(2)}] }"""
        )

        Thread.sleep(300)
    }

}