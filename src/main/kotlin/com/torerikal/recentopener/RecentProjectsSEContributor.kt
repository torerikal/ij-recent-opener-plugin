package com.torerikal.recentopener

import com.intellij.ide.RecentProjectsManager
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory
import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.Processor
import java.io.File
import javax.swing.ListCellRenderer

import com.intellij.ide.RecentProjectsManagerBase

class RecentProjectsSEContributor(private val initEvent: AnActionEvent) : SearchEverywhereContributor<RecentProjectItem> {

    companion object {
        private val ID = RecentProjectsSEContributor::class.java.simpleName
    }

    override fun getSearchProviderId(): String = ID

    override fun getGroupName(): String = "Recent Projects"

    override fun getSortWeight(): Int = 1000

    override fun showInFindResults(): Boolean = true

    override fun isShownInSeparateTab(): Boolean = true

    override fun fetchElements(
        pattern: String,
        progressIndicator: ProgressIndicator,
        consumer: Processor<in RecentProjectItem>
    ) {
        val manager = RecentProjectsManager.getInstance() as? RecentProjectsManagerBase
        val recentPaths = manager?.getRecentPaths() ?: emptyList()
        val currentProjectPath = initEvent.project?.basePath

        for (path in recentPaths) {
            if (progressIndicator.isCanceled) return

            // Filter out current project
            if (currentProjectPath != null && FileUtil.pathsEqual(path, currentProjectPath)) {
                continue
            }

            // Get project name
            val file = File(path)
            val name = file.name

            // Simple string matching for now
            if (pattern.isEmpty() || StringUtil.containsIgnoreCase(name, pattern) || StringUtil.containsIgnoreCase(path, pattern)) {
                val item = RecentProjectItem(path, name)
                if (!consumer.process(item)) {
                    return
                }
            }
        }
    }

    override fun processSelectedItem(selected: RecentProjectItem, modifiers: Int, searchText: String): Boolean {
        ProjectUtil.openOrImport(selected.path, null, true)
        return true
    }

    override fun getElementsRenderer(): ListCellRenderer<in RecentProjectItem> {
        return object : ColoredListCellRenderer<RecentProjectItem>() {
            override fun customizeCellRenderer(
                list: javax.swing.JList<out RecentProjectItem>,
                value: RecentProjectItem,
                index: Int,
                selected: Boolean,
                hasFocus: Boolean
            ) {
                append(value.name)
                append("  ", SimpleTextAttributes.REGULAR_ATTRIBUTES)
                append(FileUtil.getLocationRelativeToUserHome(value.path), SimpleTextAttributes.GRAYED_ATTRIBUTES)
            }
        }
    }

    override fun getDataForItem(element: RecentProjectItem, dataId: String): Any? = null
}

class RecentProjectsSEContributorFactory : SearchEverywhereContributorFactory<RecentProjectItem> {
    override fun createContributor(initEvent: AnActionEvent): SearchEverywhereContributor<RecentProjectItem> {
        return RecentProjectsSEContributor(initEvent)
    }
}
