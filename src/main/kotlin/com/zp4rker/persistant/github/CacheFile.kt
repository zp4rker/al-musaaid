package com.zp4rker.persistant.github

import org.kohsuke.github.GHRepository
import java.io.File

/**
 * @author zp4rker
 */
class CacheFile(private val file: File) {
    private val repos = mutableSetOf<String>()

    init {
        if (!file.exists()) file.createNewFile()

        repos.addAll(file.readLines())
    }

    fun add(repo: GHRepository) = repos.add("${repo.id}")

    fun has(repo: GHRepository) = repos.contains("${repo.id}")

    fun save() = file.writeText(repos.joinToString("\n"))
}