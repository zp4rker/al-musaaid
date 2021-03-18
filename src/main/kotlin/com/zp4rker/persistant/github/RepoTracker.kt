package com.zp4rker.persistant.github

import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.extensions.embed
import com.zp4rker.persistant.config
import net.dv8tion.jda.api.entities.TextChannel
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

    fun scan(channel: TextChannel) {
        var startedTracking = 0
        var updatedTracking = 0
        for (repo in myself.listRepositories(100, GHMyself.RepositoryListFilter.OWNER)) {
            if (repo.isArchived || cache.has(repo)) continue

            LOGGER.debug("Scanning ${repo.name}")
            repo.hooks.firstOrNull { it.config["url"] == config.github.webhook }?.let {
                LOGGER.debug("Found hook")
                if (!it.events.contains(GHEvent.CREATE)) {
                    LOGGER.debug("Updating hook")
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
                LOGGER.debug("Adding hook")
                repo.createHook(
                    "web",
                    mapOf("content_type" to "json", "url" to config.github.webhook, "insecure_ssl" to "0"),
                    listOf(GHEvent.PUSH, GHEvent.CREATE),
                    true
                )
                startedTracking++
            }

            cache.add(repo)
        }
        cache.save()

        channel.sendMessage(embed {
            title {
                text = "Finished scanning repos!"
            }

            field {
                title = "Started tracking"
                text = "$startedTracking repositor${if (startedTracking == 1) "y" else "ies"}"
                inline = false
            }

            field {
                title = "Updated tracking"
                text = "$updatedTracking repositor${if (updatedTracking == 1) "y" else "ies"}"
                inline = false
            }
        }).queue()
    }
}