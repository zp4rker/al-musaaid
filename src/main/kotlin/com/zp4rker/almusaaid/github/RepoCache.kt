package com.zp4rker.almusaaid.github

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.LongAsStringSerializer
import org.kohsuke.github.GHRepository

/**
 * @author zp4rker
 */
@Serializable
data class RepoCache(
    val repos: MutableSet<Repo> = mutableSetOf()
) {

    @Serializable
    data class Repo(
        @Serializable(with = LongAsStringSerializer::class)
        val id: Long,
        val name: String
    ) {
        constructor(repo: GHRepository) : this(repo.id, repo.name)
    }

}
