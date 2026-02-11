package com.torerikal.recentopener

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class RecentProjectsAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val contributorId = RecentProjectsSEContributor::class.java.simpleName
        SearchEverywhereManager.getInstance(project).show(contributorId, null, e)
    }
}
