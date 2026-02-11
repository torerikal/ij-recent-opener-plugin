package com.torerikal.recentopener

import com.intellij.openapi.util.NlsSafe

data class RecentProjectItem(
    val path: String,
    @NlsSafe val name: String,
    val projectGroup: String? = null
)
