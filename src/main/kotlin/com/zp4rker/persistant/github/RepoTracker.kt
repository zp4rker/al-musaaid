package com.zp4rker.persistant.github

import com.zp4rker.persistant.config
import org.kohsuke.github.GHEvent
import org.kohsuke.github.GHMyself
import org.kohsuke.github.GitHubBuilder
import java.io.File

/**
 * @author zp4rker
 */
object RepoTracker {
    private val cache = CacheFile(File("gh_cache.txt"))

    private val client = GitHubBuilder().withOAuthToken(config.github.token).build()
    private val myself = client.myself

    fun scan() {
        var startedTracking = 0
        var updatedTracking = 0
        for (repo in myself.listRepositories(100, GHMyself.RepositoryListFilter.OWNER)) {
            if (repo.isArchived || cache.has(repo)) continue

            repo.hooks.firstOrNull { it.config["url"] == config.github.webhook }?.let {
                if (!it.events.contains(GHEvent.CREATE)) {
                    it.delete()
                    repo.createHook(
                        "web",
                        mapOf("content_type" to "json", "url" to config.github.webhook, "insecure_ssl" to "0"),
                        listOf(GHEvent.PUSH, GHEvent.CREATE),
                        true
                    )
                    updatedTracking++
                }
            } ?: run {
                repo.createHook(
                    "web",
                    mapOf("content_type" to "json", "url" to config.github.webhook, "insecure_ssl" to "0"),
                    listOf(GHEvent.PUSH, GHEvent.CREATE),
                    true
                )
                startedTracking++
            }
        }
    }
}