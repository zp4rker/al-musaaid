package com.zp4rker.almusaaid.github

import com.charleskorn.kaml.Yaml
import com.zp4rker.almusaaid.discordHook
import com.zp4rker.almusaaid.githubAuth
import com.zp4rker.almusaaid.http.request
import com.zp4rker.discore.extenstions.embed
import com.zp4rker.discore.extenstions.toJson
import com.zp4rker.discore.util.encode
import kotlinx.serialization.decodeFromString
import org.kohsuke.github.GHEvent
import org.kohsuke.github.GHMyself
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHubBuilder
import java.io.File
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.scheduleAtFixedRate

/**
 * @author zp4rker
 */
object RepoTracker {

    private val rawHook = discordHook.dropLast("/github".length)
    private lateinit var myself: GHMyself

    val cacheFile: File = File("repocache.yml")
    var cache: RepoCache = if (cacheFile.length() > 0) {
        Yaml.default.decodeFromString(cacheFile.readText())
    } else {
        if (!cacheFile.exists()) cacheFile.createNewFile()
        RepoCache()
    }
    var cacheUpdated = false

    fun start() {
        val client = GitHubBuilder().withOAuthToken(githubAuth).build()
        myself = client.myself

        val name = myself.login
        val avatar = myself.avatarUrl

        var last = ZonedDateTime.now(ZoneOffset.UTC)
        Timer().scheduleAtFixedRate(0, TimeUnit.MINUTES.toMillis(1)) {

            for (repo in runCatching { myself.listRepositories(100, GHMyself.RepositoryListFilter.OWNER) }.getOrNull() ?: listOf<GHRepository>()) {
                when {
                    repo.createdAt.toInstant().epochSecond >= last.toInstant().epochSecond -> {
                        val embed = embed {
                            color = "#202225"

                            author {
                                this.name = name
                                this.iconUrl = avatar
                            }

                            title {
                                this.text = "Created new repository: $name/${repo.name}"
                                this.url = repo.httpTransportUrl
                            }
                        }

                        request(
                            method = "POST",
                            baseUrl = rawHook,
                            headers = mapOf("Content-Type" to "application/json"),
                            content = """{ "embeds": [${embed.toJson().toString(2)}] }"""
                        )

                        runCatching { track(repo) }
                    }

                    cache.repos.none { it.id == repo.id } && repo.hooks.none { it.config["url"] == discordHook } -> {
                        runCatching { track(repo) }
                    }

                    else -> {
                        cache.repos.add((RepoCache.Repo(repo))).also { cacheUpdated = true }
                    }
                }
            }

            last = ZonedDateTime.now(ZoneOffset.UTC)
            if (cacheUpdated) cacheFile.writeText(Yaml.default.encode(cache))
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
                this.text = "Started tracking: $name/${repo.name}"
                this.url = repo.httpTransportUrl
            }
        }
        request(
            method = "POST",
            baseUrl =  rawHook,
            headers = mapOf("Content-Type" to "application/json"),
            content = """{ "embeds": [${embed.toJson().toString(2)}] }"""
        )

        cache.repos.add(RepoCache.Repo(repo)).also { cacheUpdated = true }
    }

}